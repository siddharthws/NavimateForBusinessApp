package com.biz.navimate.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import com.biz.navimate.R;
import com.biz.navimate.constants.Constants;
import com.biz.navimate.debug.Dbg;
import com.biz.navimate.interfaces.IfaceDialog;
import com.biz.navimate.maps.CameraHelper;
import com.biz.navimate.maps.MarkerHelper;
import com.biz.navimate.maps.RouteHelper;
import com.biz.navimate.maps.TouchableSupportMapFragment;
import com.biz.navimate.misc.Preferences;
import com.biz.navimate.objects.Camera;
import com.biz.navimate.objects.Dialog;
import com.biz.navimate.objects.Lead;
import com.biz.navimate.objects.LocationObj;
import com.biz.navimate.objects.LocationUpdate;
import com.biz.navimate.objects.MarkerObj;
import com.biz.navimate.objects.Route;
import com.biz.navimate.objects.Statics;
import com.biz.navimate.runnables.LocationUpdateRunnable;
import com.biz.navimate.services.LocationService;
import com.biz.navimate.views.RlDialog;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.Marker;

import java.util.ArrayList;

/**
 * Created by Siddharth on 28-09-2017.
 */

public class NvmMapFragment     extends     BaseFragment
                                implements  OnMapReadyCallback,
                                            GoogleMap.OnMapLoadedCallback,
                                            GoogleMap.OnMarkerClickListener,
                                            Runnable
{
    // ----------------------- Constants ----------------------- //
    private final static String TAG = "NVM_MAP_FRAGMENT";

    // Fragment Identifier
    public static final String  FRAGMENT_IDENTIFIER     = "nvm_map_fragment";
    public static final int     FRAGMENT_LAYOUT_ID      = R.id.fl_map_fragment;

    // Map Update Callback time
    public static final int MAP_UPDATE_CB_TIME_MS       = 1000;

    // ----------------------- Interfaces ----------------------- //
    // ----------------------- Globals ----------------------- //
    private TouchableSupportMapFragment supportMapFragment         = null;
    private GoogleMap                   googleMap                  = null;
    private LocationUpdateRunnable locationUpdateRunnable          = null;
    private MarkerObj.CurrentLocation clMarker                     = null;

    // UI
    private ImageButton ibCurrentLocation = null, ibRoute = null, ibCustomize = null;

    // Helpers
    public MarkerHelper markerHelper                                = null;
    public RouteHelper  routeHelper                                 = null;
    public CameraHelper cameraHelper                                = null;

    // Map refresh
    private Handler mapUpdateHandler        = null;

    // ----------------------- Constructor ----------------------- //
    public NvmMapFragment()
    {
        super();
    }

    // Factory method to create new instance of fragment
    public static NvmMapFragment newInstance()
    {
        // Init Map fragment properties and return object
        NvmMapFragment fragment    = new NvmMapFragment();

        // Init Helpers
        fragment.markerHelper = new MarkerHelper();
        fragment.routeHelper    = new RouteHelper();
        fragment.cameraHelper = new CameraHelper();

        return fragment;
    }

    // ----------------------- Overrides ----------------------- //
    // Fragment related overrides
    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState)
    {
        View fragmentView = inflater.inflate(R.layout.fragment_nvm_map, container, false);

        // Init UI
        supportMapFragment             = (TouchableSupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map_fragment);
        ibCurrentLocation = (ImageButton) fragmentView.findViewById(R.id.ib_current_location);
        ibRoute = (ImageButton) fragmentView.findViewById(R.id.ib_route);
        ibCustomize = (ImageButton) fragmentView.findViewById(R.id.ib_cusotmize);

        // Ready the map
        supportMapFragment.getMapAsync(this);

        // Init Location Helper
        locationUpdateRunnable            = new LocationUpdateRunnable(getContext());

        // Add current location marker to cache
        clMarker = new MarkerObj.CurrentLocation(getContext());
        markerHelper.Add(clMarker);

        // Init Map Update handler
        mapUpdateHandler = new Handler();

        ibCurrentLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ButtonClickCurrentLocation();
            }
        });

        ibRoute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ButtonClickRoute();
            }
        });

        ibCustomize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ButtonClickCustomize();
            }
        });

        return fragmentView;
    }

    @Override
    public void onResume()
    {
        super.onResume();

        // Add Location client (since map is visible)
        LocationService.AddClient(getContext(), Constants.Location.CLIENT_TAG_MAP, LocationUpdate.FAST);

        // Start map update callbacks
        mapUpdateHandler.postDelayed(this, MAP_UPDATE_CB_TIME_MS);

        // Init Map settings
        InitSettings();
    }

    @Override
    public void onPause()
    {
        // Reove map update callbacks
        mapUpdateHandler.removeCallbacks(this);

        // Remove Location Client since map will not be visible anymore
        LocationService.RemoveClient(Constants.Location.CLIENT_TAG_MAP);

        super.onPause();
    }

    // Map Related Overrides
    /* Called when map is ready on screen
     */
    @Override
    public void onMapReady(GoogleMap googleMap)
    {
        // Init Google map object
        this.googleMap = googleMap;

        // Set callbacks
        googleMap.setOnMapLoadedCallback(this);

        // Set Clieck Listeners
        googleMap.setOnMarkerClickListener(this);

        // Set compass to bottom left (else it gets hidden by toolbar)
        InitMapCompassButton();

        // Init Map settings
        InitSettings();
    }

    // Called when map has been loaded on the screen
    @Override
    public void onMapLoaded()
    {
        // Load markers on map
        markerHelper.LoadMap(googleMap);

        // Load Routes on map
        routeHelper.LoadMap(googleMap);

        // Load Camera
        cameraHelper.LoadMap(googleMap);
    }

    @Override
    public boolean onMarkerClick(Marker marker)
    {
        // Get Marker Object from marker
        MarkerObj.Base markerObj = markerHelper.GetMarkerObjFromMarker(marker);
        if (markerObj.type == MarkerObj.MARKER_TYPE_CURRENT_LOCATION) {
            return true;
        }

        MarkerObj.Task clickedMarker = (MarkerObj.Task) markerObj;

        // Open Lead Dialog Box
        RlDialog.Show(new Dialog.TaskInfo(clickedMarker.task));

        // Consume the click
        return true;
    }

    @Override
    public void run()
    {
        // Refresh Current Location marker
        clMarker.Refresh();

        // Post again a deay of 1 second
        mapUpdateHandler.postDelayed(this, MAP_UPDATE_CB_TIME_MS);
    }

    // ----------------------- Public APIs ----------------------- //
    // API to add this fragment to an activity
    public static NvmMapFragment AddFragment(FragmentManager fm)
    {
        NvmMapFragment fragment = null;

        if (fm.findFragmentByTag(FRAGMENT_IDENTIFIER) != null)
        {
            // If fragment is already added, return the added instance
            fragment = (NvmMapFragment) fm.findFragmentByTag(FRAGMENT_IDENTIFIER);
        }
        else
        {
            // Create new instance
            fragment = NvmMapFragment.newInstance();

            // Add to fm
            fm.beginTransaction().add(FRAGMENT_LAYOUT_ID, fragment, FRAGMENT_IDENTIFIER).commit();
        }

        return fragment;
    }

    // Button Click APIs
    public void ButtonClickCurrentLocation() {
        // Check if Location is Updating
        if (LocationService.IsUpdating())
        {
            // Update Camera to current position
            cameraHelper.Move(new Camera.Location(LocationService.cache.GetLocation().latlng, 0, true));
        }
        else
        {
            // Set Location Update listener
            locationUpdateRunnable.SetInitListener(new LocationUpdateRunnable.IfaceRunnableLocationInit()
            {
                @Override
                public void onLocationInitSuccess(LocationObj location)
                {
                    // Perform Button Click Logic Again if location was initialized succesfully
                    ButtonClickCurrentLocation();
                }

                @Override
                public void onLocationInitError()
                {
                    // Do Nothing
                }
            });

            // Attempt to start location updates
            locationUpdateRunnable.Post(0);
        }
    }

    public void ButtonClickRoute() {
        // Launch route builder dialog
        RlDialog.Show(new Dialog.RouteBuilder(new ArrayList<Lead>(), new IfaceDialog.RouteBuilder() {
            @Override
            public void onRouteBuilt(Route.Way route) {
                // Clear routes form map and add this one
                routeHelper.Clear();
                routeHelper.Add(route);
            }
        }));
    }

    public void ButtonClickCustomize() {
        // Launch route builder dialog
        RlDialog.Show(new Dialog.MapSettings(new IfaceDialog.MapSettings() {
            @Override
            public void onSettingsUpdated() {
                // Re-initalize settings
                InitSettings();
            }
        }));
    }

    // ----------------------- Private APIs ----------------------- //
    private void InitMapCompassButton()
    {
        try
        {
            // Get Map View
            View mapView = supportMapFragment.getView();
            if (mapView != null)
            {
                // Get Parent of map
                final ViewGroup mapParent = (ViewGroup) mapView.findViewWithTag("GoogleMapMyLocationButton").getParent();

                mapParent.post(new Runnable() {
                    @Override
                    public void run()
                    {
                        try
                        {
                            // Ste all map buttons to bottom left
                            for (int i = 0, n = mapParent.getChildCount(); i < n; i++)
                            {
                                // Get child's layout params
                                View view = mapParent.getChildAt(i);
                                RelativeLayout.LayoutParams rlp = (RelativeLayout.LayoutParams) view.getLayoutParams();

                                // position on right bottom
                                rlp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                                rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
                                rlp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, 0);
                                rlp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);

                                // Set margin from screen edge
                                rlp.leftMargin      = Statics.GetPxFromDip(20);
                                rlp.bottomMargin    = Statics.GetPxFromDip(20);

                                // Reset Layout
                                view.requestLayout();
                            }
                        }
                        catch (Exception ex)
                        {
                            Dbg.error(TAG, "Exception while initializing compass");
                            Dbg.stack(ex);
                        }
                    }
                });
            }
        }
        catch (Exception ex)
        {
            Dbg.error(TAG, "Exception while initializing compass");
            Dbg.stack(ex);
        }
    }

    private void InitSettings() {
        if (googleMap != null)
        {
            this.googleMap.setMapType(Preferences.GetMapType());
            this.googleMap.setTrafficEnabled(Preferences.GetMapTrafficOverlay());
        }
    }
}
