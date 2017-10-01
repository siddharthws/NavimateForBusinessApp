package com.biz.navimate.misc;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.biz.navimate.debug.Dbg;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

/**
 * Created by Siddharth on 01-10-2017.
 */

public class GoogleApiClientHolder      implements      GoogleApiClient.ConnectionCallbacks,
                                                        GoogleApiClient.OnConnectionFailedListener
{
    // ----------------------- Constants ----------------------- //
    private static final String TAG = "GOOGLE_API_CLIENT_HOLDER";

    // ----------------------- Classes ---------------------------//
    // ----------------------- Interfaces ----------------------- //
    // ----------------------- Globals ----------------------- //
    // Global instance
    public static GoogleApiClientHolder instance = null;

    // APi client
    public GoogleApiClient apiClient       = null;

    // ----------------------- Constructor ----------------------- //
    public GoogleApiClientHolder(Context context)
    {
        // Create API Client Object
        apiClient = new GoogleApiClient.Builder(context)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        // Connect to API Client
        apiClient.connect();
    }

    // ----------------------- Overrides ----------------------- //
    @Override
    public void onConnected(@Nullable Bundle bundle)
    {
        Dbg.info(TAG, "API Client Connected");
    }

    @Override
    public void onConnectionSuspended(int i)
    {
        Dbg.error(TAG, "API Client connection suspended : " + i);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult)
    {
        Dbg.error(TAG, "API Client connection failed");
    }

    // ----------------------- Public APIs ----------------------- //
    // Static method to get instance of Holder Object
    public static void InitInstance(Context context)
    {
        // Initialize instance if null
        if (instance == null)
        {
            instance = new GoogleApiClientHolder(context);
        }
    }

    // ----------------------- Private APIs ----------------------- //
}
