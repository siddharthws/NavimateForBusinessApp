package com.biz.navimate.activities;

import android.Manifest;
import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.PermissionChecker;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ListView;

import com.biz.navimate.R;
import com.biz.navimate.application.App;
import com.biz.navimate.constants.Constants;
import com.biz.navimate.database.DbHelper;
import com.biz.navimate.fragments.NvmMapFragment;
import com.biz.navimate.interfaces.IfaceList;
import com.biz.navimate.interfaces.IfaceServer;
import com.biz.navimate.interpolators.PowerInterpolator;
import com.biz.navimate.lists.TaskListAdapter;
import com.biz.navimate.misc.AnimHelper;
import com.biz.navimate.misc.LocationCache;
import com.biz.navimate.objects.Anim;
import com.biz.navimate.objects.Camera;
import com.biz.navimate.objects.Data;
import com.biz.navimate.objects.Dialog;
import com.biz.navimate.objects.Lead;
import com.biz.navimate.objects.ListItem;
import com.biz.navimate.objects.Statics;
import com.biz.navimate.objects.Task;
import com.biz.navimate.objects.Template;
import com.biz.navimate.runnables.LocationUpdateRunnable;
import com.biz.navimate.server.SyncFormsTask;
import com.biz.navimate.server.SyncDbTask;
import com.biz.navimate.viewholders.ActivityHolder;
import com.biz.navimate.views.RlDialog;
import com.biz.navimate.views.RlDrawer;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

public class HomescreenActivity     extends     BaseActivity
                                    implements  IfaceList.Task,
                                                RlDrawer.DrawerItemClickListener {
    // ----------------------- Constants ----------------------- //
    private static final String TAG = "HOMESCREEN_ACTIVITY";


    // ----------------------- Classes ---------------------------//
    // ----------------------- Interfaces ----------------------- //
    // ----------------------- Globals ----------------------- //
    private ActivityHolder.Homescreen ui = null;
    private TaskListAdapter adapter = null;
    private AnimHelper animHelper = null;

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
        ui.lvTasks          = (ListView) findViewById(R.id.lv_tasks);
        ui.ibList          = (ImageButton) findViewById(R.id.ib_toolbar_list);
        ui.ibMap          = (ImageButton) findViewById(R.id.ib_toolbar_map);
    }

    @Override
    protected void SetViews() {
        // Init adapter
        adapter = new TaskListAdapter(this, ui.lvTasks);
        adapter.SetListener(this);

        // Init Drawer listener
        ui.rlDrawer.SetItemClickListener(this);

        // Initialize Location Runnable and Helper
        locationUpdateRunnable  = new LocationUpdateRunnable(this);

        // Initialize animations
        animHelper = new AnimHelper(this);

        // Init List and Map as per current tasks
        InitTasksUi();
    }

    @Override
    public void onStart() {
        super.onStart();

        // Send sync request (Show dialog if no tasks in adapter)
        if (adapter.getCount() == 0) {
            SyncDb(true);
        } else {
            SyncDb(false);
        }

        // Check for location related issues and ask to resolve
        if ((ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PermissionChecker.PERMISSION_GRANTED) ||
            (!Statics.IsGpsEnabled(this))){
            if (!RlDialog.IsShowing()) {
                locationUpdateRunnable.Post(0);
            }
        }
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
        } else if (ui.flMap.getVisibility() == View.VISIBLE) {
            // Go to List if map is visible
            ButtonClickList(null);
        } else {
            // Finish the activity
            finish();
        }
    }

    @Override
    public void onItemClick(Task task) {
        Lead lead = (Lead) DbHelper.leadTable.GetById(task.leadId);

        // Center map on this task marker
        ui.mapFragment.cameraHelper.Move(new Camera.Location(lead.position, 0, true));

        // Perform a Map button click
        ButtonClickMap(null);
    }

    @Override
    public void onSubmitFormClick(Task task) {
        Template formTemplate = (Template) DbHelper.templateTable.GetById(task.formTemplateId);
        Data templateData = (Data) DbHelper.dataTable.GetById(formTemplate.defaultDataId);
        RlDialog.Show(new Dialog.SubmitForm(templateData, task.dbId, false));
    }

    @Override
    public void onDrawerItemClick(int actionId) {
        switch (actionId) {
            case RlDrawer.DRAWER_ACTION_EXIT : {
                finish();
                break;
            }
        }
    }

    // ----------------------- Public APIs ----------------------- //
    public static void Start(BaseActivity activity) {
        BaseActivity.Start(activity, HomescreenActivity.class, -1, null, Constants.RequestCodes.INVALID, null);
    }

    public static void RefreshTasks() {
        // Get current activity
        BaseActivity currentActivity = App.GetCurrentActivity();

        // Check if homescreen is active
        if ((currentActivity != null) &&
            (currentActivity.getClass().equals(HomescreenActivity.class))) {
            // Re-initialize Tasks UI
            ((HomescreenActivity) currentActivity).InitTasksUi();
        }
    }

    // Button Click APIs
    public void ButtonClickDrawer(View view) {
        ui.rlDrawer.Open();
    }

    public void ButtonClickMap(View view) {
        // Play slide animation on list and map
        animHelper.Swap(ui.lvTasks, ui.flMap);

        // Play fade anims on buttons
        animHelper.Animate(new Anim.Base(Anim.TYPE_FADE_OUT, ui.ibMap, new PowerInterpolator(false, 1), null));
        animHelper.Animate(new Anim.Base(Anim.TYPE_FADE_IN, ui.ibList, new PowerInterpolator(false, 1), null));
    }

    public void ButtonClickList(View view) {
        // Play slide animation on list and map
        animHelper.SwapReverse(ui.flMap, ui.lvTasks);

        // Play fade anims on buttons
        animHelper.Animate(new Anim.Base(Anim.TYPE_FADE_OUT, ui.ibList, new PowerInterpolator(false, 1), null));
        animHelper.Animate(new Anim.Base(Anim.TYPE_FADE_IN, ui.ibMap, new PowerInterpolator(false, 1), null));
    }

    public void ButtonClickSync(View view) {
        // Sync DB with waiting dialog
        SyncDb(true);
    }

    // ----------------------- Private APIs ----------------------- //
    // API to Init List and Map UI as per the open tasks in current database
    private void InitTasksUi () {
        // Add markers for all tasks in database
        ui.mapFragment.markerHelper.RefreshTaskMarkers();

        // Clear adapter
        adapter.Clear();

        ArrayList<LatLng> bounds = new ArrayList<>();
        ArrayList<Task> openTasks = DbHelper.taskTable.GetOpenTasks();
        // Iterate through all tasks
        for (Task task : openTasks)
        {
            Lead lead = (Lead) DbHelper.leadTable.GetById(task.leadId);

            // Include in bounds for camera update
            bounds.add(lead.position);

            // Add list item to adapter
            adapter.Add(new ListItem.Task(task));
        }

        // Add current location to bounds if available
        LatLng currentLocation = LocationCache.instance.GetLocation().latlng;
        if (Statics.IsPositionValid(currentLocation)) {
            bounds.add(currentLocation);
        }

        // Update Map Camera
        if (bounds.size() > 1) {
            ui.mapFragment.cameraHelper.Move(new Camera.Bounds(bounds, true));
        } else if (bounds.size() == 1) {
            ui.mapFragment.cameraHelper.Move(new Camera.Location(bounds.get(0), 0, true));
        }
    }

    // APi to send Sync Db Request
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
                        InitTasksUi();
                    }
                });
                syncDb.execute();
            }
        });
        syncForms.execute();
    }
}
