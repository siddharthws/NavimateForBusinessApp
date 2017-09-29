package com.biz.navimate.activities;

import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.biz.navimate.R;
import com.biz.navimate.constants.Constants;
import com.biz.navimate.debug.Dbg;
import com.biz.navimate.fragments.NvmMapFragment;
import com.biz.navimate.interfaces.IfaceList;
import com.biz.navimate.interfaces.IfaceServer;
import com.biz.navimate.lists.TaskListAdapter;
import com.biz.navimate.objects.Camera;
import com.biz.navimate.objects.ListItem;
import com.biz.navimate.objects.MarkerObj;
import com.biz.navimate.objects.Statics;
import com.biz.navimate.objects.Task;
import com.biz.navimate.server.GetTasksTask;
import com.biz.navimate.viewholders.ActivityHolder;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

public class HomescreenActivity     extends     BaseActivity
                                    implements  IfaceServer.GetTasks,
                                                IfaceList.Task
{
    // ----------------------- Constants ----------------------- //
    private static final String TAG = "HOMESCREEN_ACTIVITY";


    // ----------------------- Classes ---------------------------//
    // ----------------------- Interfaces ----------------------- //
    // ----------------------- Globals ----------------------- //
    private ActivityHolder.Homescreen ui = null;
    private TaskListAdapter adapter = null;

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
        ui.mapFragment      = NvmMapFragment.AddFragment(getSupportFragmentManager());
        ui.lvTasks          = (ListView) findViewById(R.id.lv_tasks);
    }

    @Override
    protected void SetViews() {
        // Init adapter
        adapter = new TaskListAdapter(this, ui.lvTasks);
        adapter.SetListener(this);

        // Get Tasks from server
        GetTasksTask getTasks = new GetTasksTask(this);
        getTasks.SetListener(this);
        getTasks.execute();
    }

    @Override
    public void onTasksSuccess() {
        // Add markers for all tasks in database
        ArrayList<LatLng> bounds = new ArrayList<>();
        for (Task task : Statics.GetCurrentTasks())
        {
            ui.mapFragment.markerHelper.Add(new MarkerObj.Task(task));

            // Include in bounds for camera update
            bounds.add(task.lead.position);
        }

        // Update Map Camera
        ui.mapFragment.cameraHelper.Move(new Camera.Bounds(bounds, true));

        // Add tasks to list
        adapter.Clear();
        for (Task task : Statics.GetCurrentTasks()) {
            adapter.Add(new ListItem.Task(task));
        }
    }

    @Override
    public void onTasksFailed() {
        Dbg.Toast(this, "Unable to get tasks from server...", Toast.LENGTH_SHORT);
    }

    @Override
    public void onItemClick(Task task) {

    }

    @Override
    public void onSubmitFormClick(Task task) {

    }

    // ----------------------- Public APIs ----------------------- //
    public static void Start(BaseActivity activity) {
        BaseActivity.Start(activity, HomescreenActivity.class, -1, null, Constants.RequestCodes.INVALID, null);
    }

    // Button Click APIs
    public void ButtonClickBack(View view) {
        finish();
    }

    public void ButtonClickMap(View view) {
        // Placeholder
    }

    public void ButtonClickList(View view) {
        // Placeholder
    }

    // ----------------------- Private APIs ----------------------- //
}
