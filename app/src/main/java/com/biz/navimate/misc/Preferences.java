package com.biz.navimate.misc;

import android.content.Context;
import android.content.SharedPreferences;

import com.biz.navimate.constants.Constants;
import com.biz.navimate.objects.User;

/**
 * Created by Siddharth on 24-09-2017.
 */

public class Preferences {
    // ----------------------- Constants ----------------------- //
    private static final String TAG = "PREFERENCES";

    // ----------------------- Classes ---------------------------//
    // ----------------------- Interfaces ----------------------- //
    // ----------------------- Globals ----------------------- //
    // User Information
    private static User user;

    // ----------------------- Constructor ----------------------- //
    // ----------------------- Overrides ----------------------- //
    // ----------------------- Public APIs ----------------------- //
    // Initialize Statics
    public static void Init(Context context)
    {
        SharedPreferences sharedPref = context.getSharedPreferences(Constants.Preferences.PREF_FILE,
                                                                    Context.MODE_PRIVATE);

        // Read user information
        user = new User(sharedPref.getString(Constants.Preferences.KEY_NAME, ""),
                        sharedPref.getString(Constants.Preferences.KEY_PHONE, ""),
                        sharedPref.getString(Constants.Preferences.KEY_EMAIL, ""),
                        sharedPref.getInt(Constants.Preferences.KEY_APP_ID, User.INVALID_ID));
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

    /*
     * GET APIs
     */
    // User Info
    public static User GetUser ()
    {
        return user;
    }
}
