package com.biz.navimate.tasks;

import android.content.Context;
import android.os.AsyncTask;

import com.biz.navimate.debug.Dbg;
import com.biz.navimate.objects.Dialog;
import com.biz.navimate.objects.Route;
import com.biz.navimate.objects.Statics;
import com.biz.navimate.views.RlDialog;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.PolyUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

/**
 * Created by Siddharth on 02-10-2017.
 */

public class DirectionsTask extends AsyncTask<Void, Integer, Route.Way>
{
    // ----------------------- Constants ----------------------- //
    private static final String TAG = "DIRECTIONS_TASK";

    // Cache Size
    private static final int CACHE_MAX_SIZE = 20;

    // URL params
    private static final String DIRECTION_URL_BASE = "https://maps.googleapis.com/maps/api/directions/json?";
    private static final String DIRECTION_URL_ORIGIN = "origin=";
    private static final String DIRECTION_URL_DESTINATION = "destination=";
    private static final String DIRECTION_URL_WAYPOINTS = "waypoints=";
    private static final String DIRECTION_URL_OPTIMIZE = "optimize:true";
    private static final String DIRECTION_URL_API_KEY = "key=";

    // ----------------------- Interface ----------------------- //
    public interface IfaceDirectionResultListener
    {
        void OnDirectionResultSuccess(Route.Way route);
        void OnDirectionResultError();
    }
    private IfaceDirectionResultListener listener = null;
    public void SetOnDirectionResultListener(IfaceDirectionResultListener listener)
    {
        this.listener = listener;
    }

    // ----------------------- Globals ----------------------- //
    // Temporary route cache
    private static ArrayList<Route.Way> cache = null;

    private Context context = null;

    private LatLng[] checkpoints = null;
    private int currentIndex = 0;
    private boolean bShowDialogs = false;

    // ----------------------- Constructor ----------------------- //
    public DirectionsTask(Context context, boolean bShowDialogs, LatLng... checkpoints)
    {
        if (cache == null)
        {
            cache = new ArrayList<>();
        }

        this.context = context;
        this.bShowDialogs = bShowDialogs;
        this.checkpoints = checkpoints;
    }

    // ----------------------- Overrides ----------------------- //
    @Override
    public void onPreExecute()
    {
        // Start progress dialog with 0 progress
        if (bShowDialogs)
        {
            RlDialog.Show(new Dialog.Waiting("Getting Directions..."));
        }
    }

    @Override
    protected Route.Way doInBackground(Void... params)
    {
        // Validate there are more than 1 point in array
        if (checkpoints.length <= 1)
        {
            Dbg.error(TAG, "Invalid number of checkpoints. Not getting directions");
            return null;
        }

        return GetDirections();
    }

    @Override
    public void onPostExecute(Route.Way result)
    {
        // Call listener
        if (listener != null)
        {
            if ((result != null) && (result.GetCheckpoints().size() != 0))
            {
                listener.OnDirectionResultSuccess(result);
            }
            else
            {
                listener.OnDirectionResultError();
            }
        }

        // Hide dialog box
        if (bShowDialogs)
        {
            RlDialog.Hide();
        }

        return;
    }

    // ----------------------- Public APIs ----------------------- //
    public Route.Way GetDirections()
    {
        Route.Way route = null;

        // Check if route is available in Cache
        route = GetFromCache();
        if (route != null)
        {
            return route;
        }

        // Create Directions URL
        String urlAddress = DIRECTION_URL_BASE;

        // Feed source and destination to URL
        LatLng start = checkpoints[0];
        LatLng end = checkpoints[checkpoints.length - 1];
        urlAddress += DIRECTION_URL_ORIGIN + start.latitude + "," + start.longitude + "&";
        urlAddress += DIRECTION_URL_DESTINATION + end.latitude + "," + end.longitude + "&";

        // Add Waypoints to URL
        if (checkpoints.length > 2)
        {
            urlAddress += DIRECTION_URL_WAYPOINTS + DIRECTION_URL_OPTIMIZE;

            // Feed all waypoints to URL
            for (int i = 1; i < (checkpoints.length - 1); i++)
            {
                urlAddress += "|" + checkpoints[i].latitude + "," + checkpoints[i].longitude;
            }

            // Add '&'
            urlAddress += '&';
        }

        // Add API Key to URL
        urlAddress += DIRECTION_URL_API_KEY + Statics.GetApiKey(context);
        Dbg.info(TAG, "URL = " + urlAddress);

        try
        {
            // Open connection read response from server
            URL url = new URL(urlAddress);
            URLConnection urlConn = url.openConnection();
            BufferedReader ipReader = new BufferedReader(new InputStreamReader(urlConn.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line = "";
            while ((line = ipReader.readLine()) != null)
            {
                sb.append(line);
            }
            ipReader.close();

            if (sb.toString().length() > 0)
            {
                // Check result status
                Dbg.info(TAG, "RouteWaypoint received : " + sb.toString());
                JSONObject directionResultJson = new JSONObject(sb.toString());
                String status = directionResultJson.getString("status");
                if (status.equals("OK"))
                {
                    // Get RouteWaypoint JSON Object from result
                    JSONArray routesJson = directionResultJson.getJSONArray("routes");
                    if (routesJson.length() > 0)
                    {
                        // Get first route (multiple route suggestions ar enot supported currently.
                        JSONObject primaryRouteJson = routesJson.getJSONObject(0);

                        // ParseToObject JSON to RouteWaypoint Object
                        route = ParseDirectionRouteJson(primaryRouteJson);

                        if (route != null)
                        {
                            // Add to temp cache
                            AddToCache(route);
                        }
                    }
                    else
                    {
                        Dbg.error(TAG, "No route found...");
                    }
                }
                else
                {
                    Dbg.error(TAG, "Google directions request failed with status " + status);
                }
            }
            else
            {
                Dbg.error(TAG, "String length 0 from server");
            }
        }
        catch (MalformedURLException e)
        {
            Dbg.stack(e);
        }
        catch (IOException e)
        {
            Dbg.stack(e);
        }
        catch (JSONException e)
        {
            Dbg.stack(e);
        }

        return route;
    }

    // ----------------------- Private APIs ----------------------- //
    private Route.Way ParseDirectionRouteJson(JSONObject primaryRouteJson) throws JSONException
    {
        // Extract Legs Array. Number of legs depend on the no. of waypoints given in URL
        JSONArray legsJson = primaryRouteJson.getJSONArray("legs");
        ArrayList<Route.Leg> legs = new ArrayList<>();
        for (int i = 0; i < legsJson.length(); i++)
        {
            // Get Leg JSON Object
            JSONObject legJson = legsJson.getJSONObject(i);

            // Get total leg disatnce and duration
            int legDistance = 0, legDuration = 0;
            if (!legJson.isNull("distance"))
            {
                legDistance = legJson.getJSONObject("distance").getInt("value");
            }

            if (!legJson.isNull("duration"))
            {
                legDuration = legJson.getJSONObject("duration").getInt("value");
            }

            // Get Steps inside this leg
            JSONArray stepsJson = legJson.getJSONArray("steps");

            // Create array of steps from the steps JSON given by google API
            ArrayList<Route.Step> steps = new ArrayList<>();
            for (int stepId = 0; stepId < stepsJson.length(); stepId++)
            {
                // Get JSON containing Step Information
                JSONObject stepJson = stepsJson.getJSONObject(stepId);

                // get distance and duration of step
                int stepDistance = 0;
                if (!stepJson.isNull("distance"))
                {
                    stepDistance = stepJson.getJSONObject("distance").getInt("value");
                }

                // Add leg duration
                int stepDuration = 0;
                if (!stepJson.isNull("duration"))
                {
                    stepDuration = stepJson.getJSONObject("duration").getInt("value");
                }

                // Get polyline string & decode into list of LatLng.
                String polyline = stepJson.getJSONObject("polyline").getString("points");
                ArrayList<LatLng> stepPoints = (ArrayList<LatLng>) PolyUtil.decode(polyline);

                // Create a step object using the points
                Route.Step step = new Route.Step(stepPoints);
                steps.add(step);
            }

            // Create a Leg Object for this Leg
            Route.Leg leg = new Route.Leg(steps, legDuration, legDistance);
            legs.add(leg);
            Dbg.info(TAG, "Leg = " + i + " Start = " + leg.GetAllPoints().size());
        }

        // Create Route using the legs
        Route.Way route = new Route.Way(legs);

        return route;
    }

    private void AddToCache(Route.Way route)
    {
        // Avoid Cache Overflow
        while (cache.size() > CACHE_MAX_SIZE)
        {
            cache.remove(CACHE_MAX_SIZE);
        }

        cache.add(route);
    }

    private Route.Way GetFromCache()
    {
        // Check if these checkpoints are present in any route
        for (Route.Way route : cache)
        {
            if (route.GetCheckpoints().equals(checkpoints))
            {
                return route;
            }
        }

        // Route not found
        return null;
    }
}
