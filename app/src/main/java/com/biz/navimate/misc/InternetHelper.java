package com.biz.navimate.misc;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by Siddharth on 16-11-2016.
 */

public class InternetHelper
{
    // ----------------------- Constants ----------------------- //
    private static final String TAG = "INTERNET_HELPER";

    // ----------------------- Globals ----------------------- //
    // ----------------------- Constructor ----------------------- //
    // ----------------------- Public APIs ----------------------- //
    /*
     * Api to check if internet is available or not
     */
    public static Boolean IsInternetAvailable (Context context)
    {
        Boolean bIsInternetAvailable = false;

        if (context == null)
        {
            // Ignore call if context is null
            bIsInternetAvailable = true;
        }
        else
        {
            // Get Active Network information object
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = cm.getActiveNetworkInfo();

            // Validate network info and connection status and return result
            if ((networkInfo != null)  && networkInfo.isConnected())
            {
                bIsInternetAvailable = true;
            }
        }

        return bIsInternetAvailable;
    }
}
