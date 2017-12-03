package com.biz.navimate.activities;

import android.Manifest;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.PermissionChecker;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.biz.navimate.R;
import com.biz.navimate.constants.Constants;
import com.biz.navimate.debug.Dbg;
import com.biz.navimate.fragments.NvmMapFragment;
import com.biz.navimate.interfaces.IfaceList;
import com.biz.navimate.interfaces.IfaceServer;
import com.biz.navimate.interpolators.PowerInterpolator;
import com.biz.navimate.lists.TaskListAdapter;
import com.biz.navimate.misc.AnimHelper;
import com.biz.navimate.misc.LocationUpdateHelper;
import com.biz.navimate.objects.Anim;
import com.biz.navimate.objects.Camera;
import com.biz.navimate.objects.Dialog;
import com.biz.navimate.objects.ListItem;
import com.biz.navimate.objects.Statics;
import com.biz.navimate.objects.Task;
import com.biz.navimate.runnables.LocationUpdateRunnable;
import com.biz.navimate.server.GetTasksTask;
import com.biz.navimate.viewholders.ActivityHolder;
import com.biz.navimate.views.RlDialog;
import com.biz.navimate.views.RlDrawer;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

public class HomescreenActivity     extends     BaseActivity
                                    implements  IfaceServer.GetTasks,
                                                IfaceList.Task, RlDrawer.DrawerItemClickListener {
    // ----------------------- Constants ----------------------- //
    private static final String TAG = "HOMESCREEN_ACTIVITY";


    // ----------------------- Classes ---------------------------//
    // ----------------------- Interfaces ----------------------- //
    // ----------------------- Globals ----------------------- //
    private ActivityHolder.Homescreen ui = null;
    private TaskListAdapter adapter = null;
    private AnimHelper animHelper = null;

    private LocationUpdateHelper locationUdpateHelper = null;
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
        locationUdpateHelper    = new LocationUpdateHelper(this);

        // Initialize animations
        animHelper = new AnimHelper(this);
    }

    @Override
    public void onStart() {
        super.onStart();

        // Get Tasks from server
        GetTasksTask getTasks = new GetTasksTask(this);
        getTasks.SetListener(this);
        getTasks.execute();

        if (Statics.GetCurrentTasks().size() == 0) {
            RlDialog.Show(new Dialog.Waiting("Getting tasks from server..."));
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
    public void onTasksSuccess() {
        if (adapter.getCount() == 0) {
            RlDialog.Hide();
        }

        // Add markers for all tasks in database
        ui.mapFragment.markerHelper.RefreshTaskMarkers();

        // Update Camera to bounds
        ArrayList<LatLng> bounds = new ArrayList<>();
        for (Task task : Statics.GetCurrentTasks())
        {
            // Include in bounds for camera update
            bounds.add(task.lead.position);
        }

        if (bounds.size() > 1) {
            ui.mapFragment.cameraHelper.Move(new Camera.Bounds(bounds, true));
        } else if (bounds.size() == 1) {
            ui.mapFragment.cameraHelper.Move(new Camera.Location(bounds.get(0), 0, true));
        } else {
            // TODO : Center on current location
        }

        // Add tasks to list
        adapter.Clear();
        for (Task task : Statics.GetCurrentTasks()) {
            adapter.Add(new ListItem.Task(task));
        }
    }

    @Override
    public void onTasksFailed() {
        RlDialog.Hide();
        Dbg.Toast(this, "Unable to get tasks from server...", Toast.LENGTH_SHORT);
    }

    @Override
    public void onItemClick(Task task) {
        // Center map on this task marker
        ui.mapFragment.cameraHelper.Move(new Camera.Location(task.lead.position, 0, true));

        // Perform a Map button click
        ButtonClickMap(null);
    }

    @Override
    public void onSubmitFormClick(Task task) {
        RlDialog.Show(new Dialog.SubmitForm(task.template, task.id, false));
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

    // ----------------------- Private APIs ----------------------- //
}
