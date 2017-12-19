package com.biz.navimate.misc;

import android.content.Context;
import android.content.SharedPreferences;

import com.biz.navimate.application.App;
import com.biz.navimate.constants.Constants;
import com.biz.navimate.objects.AppVersionObject;
import com.biz.navimate.objects.LocationObj;
import com.biz.navimate.objects.User;
import com.google.android.gms.maps.GoogleMap;

/**
 * Created by Siddharth on 24-09-2017.
 */

public class Preferences {
    // ----------------------- Constants ----------------------- //
    private static final String TAG = "PREFERENCES";

    // ----------------------- Classes ---------------------------//
    // ----------------------- Interfaces ----------------------- //
    // ----------------------- Globals ----------------------- //
    // App Information
    private static AppVersionObject currentVersion;

    // User Information
    private static User user;

    // Map Settings
    private static int mapType                      = -1;
    private static Boolean bMapTrafficOverlay       = false;

    // User Location cache
    private static LocationObj lastKnowLocation         = null;

    // ----------------------- Constructor ----------------------- //
    // ----------------------- Overrides ----------------------- //
    // ----------------------- Public APIs ----------------------- //
    // Initialize Statics
    public static void Init(Context context)
    {
        SharedPreferences sharedPref = context.getSharedPreferences(Constants.Preferences.PREF_FILE,
                                                                    Context.MODE_PRIVATE);

        // Read App version information
        currentVersion  =   new AppVersionObject(   App.GetVersionNumber(context),
                                                    App.GetVersionName(context));

        // Read user information
        user = new User(sharedPref.getString(Constants.Preferences.KEY_NAME, ""),
                        sharedPref.getString(Constants.Preferences.KEY_PHONE, ""),
                        sharedPref.getString(Constants.Preferences.KEY_EMAIL, ""),
                        sharedPref.getInt(Constants.Preferences.KEY_APP_ID, User.INVALID_ID));

        // Read map settings
        mapType                 = sharedPref.getInt(Constants.Preferences.KEY_MAP_TYPE, GoogleMap.MAP_TYPE_NORMAL);
        bMapTrafficOverlay      = sharedPref.getBoolean(Constants.Preferences.KEY_MAP_TRAFFIC_OVERLAY, false);

        // Init Location Object
        lastKnowLocation        = new LocationObj(  Double.valueOf(sharedPref.getString(Constants.Preferences.KEY_LATITUDE, "0")),
                                                    Double.valueOf(sharedPref.getString(Constants.Preferences.KEY_LONGITUDE, "0")),
                                                    sharedPref.getLong(Constants.Preferences.KEY_TIMESTAMP, 0L), 0.0f);
    }

    // ----------------------- Private APIs ----------------------- //

    /*
     * SET APIs
     */
    // User Info
    public static void SetUser (Context context, User user)
    {
        SharedPreferences sharedPref = context.getSharedPreferences(Constants.Preferences.PREF_FILE,
                                                                    Context.MODE_PRIVATE);
        SharedPreferences.Editor prefEdit   = sharedPref.edit();

        // Write App registration data
        prefEdit.putString(Constants.Preferences.KEY_NAME, user.name);
        prefEdit.putString(Constants.Preferences.KEY_PHONE, user.phone);
        prefEdit.putString(Constants.Preferences.KEY_EMAIL, user.email);
        prefEdit.putInt(Constants.Preferences.KEY_APP_ID, user.appId);
        prefEdit.apply();

        // Set cache
        Preferences.user = user;
    }

    public static void SetLocation(Context context, LocationObj location)
    {
        SharedPreferences sharedPref        = context.getSharedPreferences( Constants.Preferences.PREF_FILE,
                                                                            Context.MODE_PRIVATE);
        SharedPreferences.Editor prefEdit   = sharedPref.edit();

        // Set setting
        prefEdit.putString(Constants.Preferences.KEY_LATITUDE,   String.valueOf(location.latlng.latitude));
        prefEdit.putString(Constants.Preferences.KEY_LONGITUDE,  String.valueOf(location.latlng.longitude));
        prefEdit.putLong(Constants.Preferences.KEY_TIMESTAMP,  location.timestamp);
        prefEdit.apply();

        // Set Cache
        lastKnowLocation = location;
    }

    // Map Settings
    public static void SetMapType(Context context, int mapType)
    {
        SharedPreferences sharedPref        = context.getSharedPreferences( Constants.Preferences.PREF_FILE,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor prefEdit   = sharedPref.edit();

        // Set setting
        prefEdit.putInt(Constants.Preferences.KEY_MAP_TYPE, mapType);
        prefEdit.commit();

        // Set cache
        Preferences.mapType = mapType;
    }

    public static void SetMapTrafficOverlay(Context context, Boolean bOverlay)
    {
        SharedPreferences sharedPref        = context.getSharedPreferences( Constants.Preferences.PREF_FILE,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor prefEdit   = sharedPref.edit();

        // Set setting
        prefEdit.putBoolean(Constants.Preferences.KEY_MAP_TRAFFIC_OVERLAY, bOverlay);
        prefEdit.commit();

        // Set cache
        Preferences.bMapTrafficOverlay = bOverlay;
    }

    /*
     * GET APIs
     */
    // User Info
    public static User GetUser ()
    {
        return user;
    }

    public static AppVersionObject GetVersion ()
    {
        return currentVersion;
    }

    public static LocationObj GetLocation()
    {
        return lastKnowLocation;
    }

    // Map Settings
    public static int GetMapType ()
    {
        return mapType;
    }

    public static boolean GetMapTrafficOverlay ()
    {
        return bMapTrafficOverlay;
    }
}
