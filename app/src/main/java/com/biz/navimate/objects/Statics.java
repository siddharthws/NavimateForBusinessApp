package com.biz.navimate.objects;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Looper;
import android.view.View;

import com.biz.navimate.debug.Dbg;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

/**
 * Created by Siddharth on 27-09-2017.
 */

public class Statics {
    // ----------------------- Constants ----------------------- //
    private static final String TAG = "STATICS";

    // ----------------------- Globals ----------------------- //
    // Screen size
    public static Point SCREEN_SIZE = null;

    // Screen Density
    public static float SCREEN_DENSITY = 0f;

    // Current taks of rep
    private static ArrayList<Task> currentTasks = new ArrayList<>();

    // ----------------------- Public APIs ----------------------- //
    // Check if this thread is UI thread
    public static boolean IsOnUiThread()
    {
        return (Looper.myLooper() == Looper.getMainLooper());
    }

    // Convert Dip into Pixels
    public static int GetPxFromDip(int dip)
    {
        int px = (int) Math.ceil(dip * SCREEN_DENSITY);
        return px;
    }

    // Get API key from Manifest's Meta data TAG
    public static String GetApiKey(Context context)
    {
        String key = "";

        try
        {
            // Get Application Info
            ApplicationInfo appInfo = context.getPackageManager().getApplicationInfo(
                                        context.getPackageName(),
                                        PackageManager.GET_META_DATA);

            // Get From Meta Data
            if (appInfo.metaData != null)
            {
                key = appInfo.metaData.getString("com.google.android.geo.API_KEY");
            }
        }
        catch (PackageManager.NameNotFoundException e)
        {
            Dbg.error(TAG, "Error while retrieving API Key");
            Dbg.stack(e);
        }

        return key;
    }

    // Position validity check
    public static boolean IsPositionValid(LatLng position)
    {
        if (position != null)
        {
            if ((position.latitude != 0) && (position.longitude != 0))
            {
                return true;
            }
        }

        return false;
    }

    // Distacne Calculation API
    public static double deg2rad(double deg)
    {
        return (deg * Math.PI / 180.0);
    }

    public static double rad2deg(double rad)
    {
        return (rad * 180 / Math.PI);
    }

    public static int GetDistanceBetweenCoordinates(LatLng point1, LatLng point2)
    {
        double lat1 = point1.latitude;
        double lat2 = point2.latitude;
        double lon1 = point1.longitude;
        double lon2 = point2.longitude;

        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;

        int distanceM = (int) (dist * 1609.344);

        return distanceM;
    }

    // Getter Setter APIs for current tasks
    public static void SetCurrentTasks(ArrayList<Task> tasks) {
        currentTasks.clear();
        currentTasks.addAll(tasks);
    }

    public static ArrayList<Task> GetCurrentTasks() {
        return currentTasks;
    }

    public static ArrayList<Lead> GetCurrentLeads() {
        ArrayList<Lead> leads = new ArrayList<>();
        for (Task task : currentTasks) {
            if (!leads.contains(task.lead)) {
                leads.add(task.lead);
            }
        }
        return leads;
    }

    public static void RemoveFromTasks(int taskId)
    {
        Task taskToRemove = null;
        for (Task task : currentTasks) {
            if (task.id == taskId) {
                taskToRemove = task;
                break;
            }
        }

        if (taskToRemove != null) {
            currentTasks.remove(taskToRemove);
        }
    }

    public static Lead GetLeadById(Integer leadId) {
        for (Task task : currentTasks) {
            if (task.lead.id == leadId) {
                return task.lead;
            }
        }

        return null;
    }

    // Bitmap related APIs
    public static Bitmap GetBitmapFromView(View view) {
        Bitmap bitmap = Bitmap.createBitmap(    view.getWidth(),
                                                view.getHeight(),
                                                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawColor(Color.WHITE);
        view.layout(view.getLeft(),
                    view.getTop(),
                    view.getRight(),
                    view.getBottom());
        view.draw(canvas);
        return bitmap;
    }

    public static Bitmap ScaleBitmap(Bitmap origBitmap) {
        // Get scaling factor form size
        double maxSizeB = 256 * 1024; // 256 KB Limit
        double origSizeB = origBitmap.getByteCount();
        double scalingFactor = Math.sqrt(origSizeB / maxSizeB);

        // Get Scaled heights
        int scaledHeight = (int) (origBitmap.getHeight() / scalingFactor);
        int scaledWidth  = (int) (origBitmap.getWidth() / scalingFactor);

        // Scale bitmap
        Bitmap scaledBitmap = Bitmap.createScaledBitmap(origBitmap, scaledWidth, scaledHeight, true);
        return scaledBitmap;
    }
}
