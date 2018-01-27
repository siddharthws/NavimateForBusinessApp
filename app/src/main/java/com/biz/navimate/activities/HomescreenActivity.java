package com.biz.navimate.activities;

import android.Manifest;
import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.PermissionChecker;
import android.view.View;
import android.widget.FrameLayout;

import com.biz.navimate.R;
import com.biz.navimate.application.App;
import com.biz.navimate.constants.Constants;
import com.biz.navimate.database.DbHelper;
import com.biz.navimate.fragments.NvmMapFragment;
import com.biz.navimate.interfaces.IfaceServer;
import com.biz.navimate.objects.Camera;
import com.biz.navimate.objects.Lead;
import com.biz.navimate.objects.LocationObj;
import com.biz.navimate.objects.LocationUpdate;
import com.biz.navimate.objects.Statics;
import com.biz.navimate.objects.Task;
import com.biz.navimate.runnables.LocationUpdateRunnable;
import com.biz.navimate.server.SyncFormsTask;
import com.biz.navimate.server.SyncDbTask;
import com.biz.navimate.services.LocationService;
import com.biz.navimate.services.WebSocketService;
import com.biz.navimate.viewholders.ActivityHolder;
import com.biz.navimate.views.RlDialog;
import com.biz.navimate.views.RlDrawer;
import com.biz.navimate.views.TvCalibri;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

public class HomescreenActivity     extends     BaseActivity
                                    implements  RlDrawer.DrawerItemClickListener,
                                                LocationUpdateRunnable.IfaceRunnableLocationInit {
    // ----------------------- Constants ----------------------- //
    private static final String TAG = "HOMESCREEN_ACTIVITY";


    // ----------------------- Classes ---------------------------//
    // ----------------------- Interfaces ----------------------- //
    // ----------------------- Globals ----------------------- //
    private ActivityHolder.Homescreen ui = null;

    private LocationUpdateRunnable locationUpdateRunnable = null;

    // ----------------------- Constructor ----------------------- //
    // ----------------------- Abstracts ----------------------- //
    // ----------------------- Overrides ----------------------- //
    // Init Overrides
    @Override
    protected void InflateLayout() {
        // Set content view
        setContentView(R.layout.activity_homescreen);
    }

    @Override
    protected void FindViews() {
        // Init view holder
        ui = new ActivityHolder.Homescreen();
        holder = ui;

        // Add Fragments
        ui.flMap            = (FrameLayout) findViewById(R.id.fl_map_fragment);
        ui.mapFragment      = NvmMapFragment.AddFragment(getSupportFragmentManager());
        ui.rlDrawer         = (RlDrawer) findViewById(R.id.rl_drawer);
        ui.tvTaskCount      = (TvCalibri) findViewById(R.id.tv_toolbar_tasks_count);
    }

    @Override
    protected void SetViews() {
        // Init Drawer listener
        ui.rlDrawer.SetItemClickListener(this);

        // Initialize Location Runnable and Helper
        locationUpdateRunnable  = new LocationUpdateRunnable(this);

        // Init List and Map as per current tasks
        InitMap();

        // Check for location permission / GPS
        if (!LocationService.IsLocationPermissionGranted(this) ||
            !LocationService.IsGpsEnabled(this)) {
            // Add slow Init client to enable location
            LocationService.AddClient(this, Constants.Location.CLIENT_TAG_INIT, LocationUpdate.SLOW);

            // Set Listener to remove client
            locationUpdateRunnable.SetInitListener(this);

            // Post Update Runnable
            locationUpdateRunnable.Post(0);
        }

        // Start Task Sync without dialog
        SyncDb(false);
    }

    @Override
    public void onStart() {
        super.onStart();

        // Check for location related issues and ask to resolve
        if ((ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PermissionChecker.PERMISSION_GRANTED) ||
            (!LocationService.IsGpsEnabled(this))){
            if (!RlDialog.IsShowing()) {
                locationUpdateRunnable.Post(0);
            }
        }
    }

    @Override
    public void onDestroy() {
        // Stop Services
        WebSocketService.StopService();
        LocationService.StopService();

        // Uninit App
        App.Uninitialize();

        super.onDestroy();
    }

    @Override
    public void onBackPressed()
    {
        if (RlDialog.IsShowing()) {
            // hide dialog if showing
            RlDialog.Hide();
        } else if (ui.rlDrawer.IsShowing()) {
            // Hide Drawer
            ui.rlDrawer.Close();
        } else {
            // Finish the activity
            finish();
        }
    }

    @Override
    public void onDrawerItemClick(int actionId) {
        switch (actionId) {
            case RlDrawer.DRAWER_ACTION_TASK : {
                TaskActivity.Start(this);
                break;
            }
            case RlDrawer.DRAWER_ACTION_FORMS : {
                FormsActivity.Start(this);
                break;
            }
            case RlDrawer.DRAWER_ACTION_EXIT : {
                finish();
                break;
            }
        }
    }

    // Location Init Listeners
    @Override
    public void onLocationInitSuccess(LocationObj location) {
        // Remove Init Client
        LocationService.RemoveClient(Constants.Location.CLIENT_TAG_INIT);
    }

    @Override
    public void onLocationInitError() {
        // Remove Init Client
        LocationService.RemoveClient(Constants.Location.CLIENT_TAG_INIT);
    }

    // ----------------------- Public APIs ----------------------- //
    public static void Start(BaseActivity activity) {
        BaseActivity.Start(activity, HomescreenActivity.class, -1, null, Constants.RequestCodes.INVALID, null);
    }

    public static void Refresh() {
        // Get current activity
        BaseActivity currentActivity = App.GetCurrentActivity();

        // Check if homescreen is active
        if ((currentActivity != null) &&
            (currentActivity.getClass().equals(HomescreenActivity.class))) {
            // Re-initialize Tasks UI
            ((HomescreenActivity) currentActivity).InitMap();
        }
    }

    // Button Click APIs
    public void ButtonClickDrawer(View view) {
        ui.rlDrawer.Open();
    }

    public void ButtonClickSync(View view) {
        // Sync DB with waiting dialog
        SyncDb(true);
    }

    public void ButtonClickTask(View view) {
        // Start Task Activity
        TaskActivity.Start(this);
    }

    // ----------------------- Private APIs ----------------------- //
    // API to Init List and Map UI as per the open tasks in current database
    private void InitMap() {
        // Add markers for all tasks in database
        ui.mapFragment.markerHelper.RefreshTaskMarkers();

        // Get LatLng Bounds for all open tasks
        ArrayList<Task> openTasks = DbHelper.taskTable.GetOpenTasks();
        ArrayList<LatLng> bounds = new ArrayList<>();
        for (Task task : openTasks)
        {
            Lead lead = (Lead) DbHelper.leadTable.GetById(task.leadId);

            // Include in bounds for camera update
            bounds.add(lead.position);
        }

        // Add current location to bounds if available
        LatLng currentLocation = LocationService.cache.GetLocation().latlng;
        if (Statics.IsPositionValid(currentLocation)) {
            bounds.add(currentLocation);
        }

        // Update Map Camera
        if (bounds.size() > 1) {
            ui.mapFragment.cameraHelper.Move(new Camera.Bounds(bounds, true));
        } else if (bounds.size() == 1) {
            ui.mapFragment.cameraHelper.Move(new Camera.Location(bounds.get(0), 0, true));
        }

        // Set Task Count
        ui.tvTaskCount.setText(String.valueOf(openTasks.size()));
    }

    // API to send Sync Db Request
    private void SyncDb(final boolean bDialog) {
        final Context context = this;

        // Sync Forms followed by DB
        SyncFormsTask syncForms = new SyncFormsTask(context, bDialog);
        syncForms.SetListener(new IfaceServer.SyncForms() {
            @Override
            public void onFormsSynced() {
                SyncDbTask syncDb = new SyncDbTask(context, bDialog);
                syncDb.SetListener(new IfaceServer.SyncTasks() {
                    @Override
                    public void onTaskCompleted() {
                        // Re-Initialize UI
                        InitMap();
                    }
                });
                syncDb.execute();
            }
        });
        syncForms.execute();
    }
}
