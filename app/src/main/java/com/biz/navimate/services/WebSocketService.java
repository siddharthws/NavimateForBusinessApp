package com.biz.navimate.services;

import android.content.Context;

import com.biz.navimate.constants.Constants;
import com.biz.navimate.debug.Dbg;
import com.biz.navimate.misc.Preferences;
import com.biz.navimate.objects.LocationObj;
import com.biz.navimate.objects.LocationUpdate;
import com.biz.navimate.objects.Statics;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.WebSocket;
import ua.naiksoftware.stomp.Stomp;
import ua.naiksoftware.stomp.StompHeader;
import ua.naiksoftware.stomp.client.StompClient;

/**
 * Created by Siddharth on 22-12-2017.
 */

public class WebSocketService extends BaseService {
    // ----------------------- Constants ----------------------- //
    private static final String TAG = "WEB_SOCKET_SERVICE";

    // Macros for time intervals
    private static final int   TIME_MS_5_S            = 5 * 1000; // 5 second interval
    private static final int   TIME_MS_30_S           = 30 * 1000; // 30 second interval

    // ----------------------- Classes ---------------------------//
    // ----------------------- Interfaces ----------------------- //
    // ----------------------- Globals ----------------------- //
    private static WebSocketService service   = null;

    // Stomp related objects
    private StompClient wsClient = null;
    private List<StompHeader> headers = null;

    // Heartbeat related objects
    private long lastHbTimeMs = 0L;

    // Tracking related objects
    private boolean bTrack = false;
    private LocationObj locCache = null;

    // ----------------------- Constructor ----------------------- //
    // ----------------------- Overrides ----------------------- //
    @Override
    public void Init(){
        service = this;

        // Init Headers
        headers = new ArrayList<>();
        headers.add(new StompHeader(Constants.Server.KEY_ID, String.valueOf(Preferences.GetUser().appId)));

        // Connect
        Connect();
    }

    @Override
    public void StickyServiceJob()
    {
        Dbg.info(TAG, "Sticky Job execution");
        // Check if socket is connected
        if (wsClient.isConnected()) {
            // Perform Heartbeat logic
            Heartbeat();

            // Perform Tracking Logic
            if (bTrack) {
                Track();
            }
        } else {
            // Perform Connection Logic
            Connect();
        }

        // Getinterval for next sleep
        long interval = GetSleepInterval();

        // Sleep
        Sleep(interval);
    }

    @Override
    public void Destroy(){
        // Stop Tracking
        StopTracking();

        // Disconnect WS Client
        if (wsClient.isConnected() || wsClient.isConnecting()) {
            wsClient.disconnect();
        }
    }

    // ----------------------- Public APIs ----------------------- //
    // APIs to start / stop / check status of the service
    public static void StartService(Context context) {
        if (!IsRunning()) {
            // Start Service
            StartService(context, WebSocketService.class);
        }
    }

    public static void StopService() {
        // Stop service
        if (IsRunning()) {
            // Stop this service
            StopService(service);
        }
    }

    public static boolean IsRunning()
    {
        return IsRunning(service);
    }

    // ----------------------- Private APIs ----------------------- //
    // API to connect to server through stomp client
    private void Connect() {
        // Check if client is connected or being connected
        if ((wsClient != null) && (wsClient.isConnected() || wsClient.isConnecting())) {
            return;
        }

        // Init Stomp Client
        wsClient = Stomp.over(WebSocket.class, Constants.Server.URL_STOMP);

        // Set Lifecycle event listener
        wsClient.lifecycle().subscribe(lifecycleEvent -> {
            switch (lifecycleEvent.getType()) {
                case OPENED: {
                    onConnectionOpened();
                    Dbg.info(TAG, "Connection established succesfully");
                    break;
                }
                case ERROR: {
                    Dbg.error(TAG, "Error while establishing Stomp Connection : " + lifecycleEvent.getException().getLocalizedMessage());
                    break;
                }
                case CLOSED: {
                    Dbg.error(TAG, "Stomp connection has been closed : " + lifecycleEvent.getMessage());
                    break;
                }
            }
        });

        // Connect using headers
        wsClient.connect(headers, true);
    }

    private void onConnectionOpened() {
        // Subscribe to start and stop tracking channels
        wsClient.topic(Constants.Server.URL_START_TRACKING).subscribe(message -> {
            StartTracking();
        });
        wsClient.topic(Constants.Server.URL_STOP_TRACKING).subscribe(message -> {
            StopTracking();
        });

        // Start tracking by default
        StartTracking();
    }

    // API to send heart beat to server
    private void Heartbeat() {
        // Check if new ping needs to be sent
        if ((System.currentTimeMillis() - lastHbTimeMs) > TIME_MS_30_S) {
            // Send empty string to server
            wsClient.send(Constants.Server.URL_HEARTBEAT, "").subscribe();

            // Update last heartbeat time
            lastHbTimeMs = System.currentTimeMillis();
        }
    }

    // APIs to Start / Stop Tracking
    private void StartTracking() {
        if (!IsRunning()) {
            Dbg.error(TAG, "Service is not running. Cannot start tracking");
            return;
        }

        // Add tracker client to receive fast updates
        LocationService.AddClient(service, Constants.Location.CLIENT_TAG_TRACKER, LocationUpdate.V_FAST);

        // Set Tracking Flag
        service.bTrack = true;

        // Invalidate location cache
        locCache = null;

        // Interrupt sleep to execute 1 iteration
        service.bInterruptSleep = true;
    }

    private void StopTracking() {
        // Remove tracker client
        LocationService.RemoveClient(Constants.Location.CLIENT_TAG_TRACKER);

        // Set Tracking Flag
        service.bTrack = false;
    }

    // API to perform tracking logic
    private void Track() {
        if (LocationService.IsUpdating()) {
            if (!IsTrackingCacheUpdated()) {
                // Send location on socket
                LocationObj currentLoc = LocationService.cache.GetLocation();
                SendTrackingUpdate(currentLoc, Constants.Tracker.ERROR_NONE);
            } else if ((System.currentTimeMillis() - locCache.timestamp) > TIME_MS_30_S) {
                // Send IDLE status if last location update was 30 seconds ago
                SendTrackingUpdate(null, Constants.Tracker.ERROR_IDLE);
            }
        } else if (!LocationService.IsGpsEnabled(this)) {
            // Send GPS error to server
            SendTrackingUpdate(null, Constants.Tracker.ERROR_NO_GPS);
        } else if (!LocationService.IsLocationPermissionGranted(this)) {
            // Send Permission error to server
            SendTrackingUpdate(null, Constants.Tracker.ERROR_NO_PERMISSION);
        } else {
            // Send Location not updating error to server
            SendTrackingUpdate(null, Constants.Tracker.ERROR_NO_UPDATES);
        }
    }

    // API to send tracking related data to server
    private void SendTrackingUpdate(LocationObj location, int status) {
        // Prepare Base Payload
        JSONObject payload = new JSONObject();
        try {
            if (location != null) {
                payload.put(Constants.Server.KEY_LAT,  location.latlng.latitude);
                payload.put(Constants.Server.KEY_LNG, location.latlng.longitude);
                payload.put(Constants.Server.KEY_TIMESTAMP, location.timestamp);
                payload.put(Constants.Server.KEY_SPEED,     location.speed);
            }

            // Add status to payload
            payload.put(Constants.Server.KEY_STATUS,     status);
        } catch (JSONException e) {
            Dbg.error(TAG, "JSON Exception while preparing tracking payload");
            Dbg.stack(e);
            return;
        }

        // Send to server through stomp
        wsClient.send(Constants.Server.URL_TRACKING_UPDATE, payload.toString()).subscribe();

        // Update Cache
        if (location != null) {
            locCache = location;
        }
    }

    // API to check if location cache is updated
    private boolean IsTrackingCacheUpdated() {
        // Get current location
        LocationObj currentLoc = LocationService.cache.GetLocation();

        // Validate Location cache and current location
        if ((currentLoc == null) || (locCache == null) || !Statics.IsPositionValid(currentLoc.latlng) || !Statics.IsPositionValid(locCache.latlng)) {
            return false;
        }

        // Check if locations are approximately close
        if (!LocationService.IsApproxLocations(currentLoc.latlng, locCache.latlng)) {
            // Check if current location is newer than location cache
            if (currentLoc.timestamp > locCache.timestamp) {
                return false;
            }
        }

        return true;
    }

    // API to get sleep interval as per current params
    private long GetSleepInterval() {
        long interval = TIME_MS_30_S;

        if (service.bTrack && LocationService.IsUpdating() && wsClient.isConnected()) {
            // Change to 5 seconds for Tracking
            interval = TIME_MS_5_S;
        }

        return interval;
    }
}
