package com.biz.navimate.activities;

import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;

import com.biz.navimate.R;
import com.biz.navimate.application.App;
import com.biz.navimate.constants.Constants;
import com.biz.navimate.database.DbHelper;
import com.biz.navimate.interfaces.IfaceList;
import com.biz.navimate.lists.TaskListAdapter;
import com.biz.navimate.objects.Dialog;
import com.biz.navimate.objects.ListItem;
import com.biz.navimate.objects.Task;
import com.biz.navimate.server.SyncDbTask;
import com.biz.navimate.viewholders.ActivityHolder;
import com.biz.navimate.views.RlDialog;
import com.biz.navimate.views.RlListView;

import java.util.ArrayList;

public class TaskActivity   extends     BaseActivity
                            implements  IfaceList.Task
{
    // ----------------------- Constants ----------------------- //
    private static final String TAG = "TASK_ACTIVITY";

    // ----------------------- Interfaces ----------------------- //
    // ----------------------- Globals ----------------------- //
    private ActivityHolder.Task  ui                 = null;
    private TaskListAdapter listAdpater              = null;

    // ----------------------- Constructor ----------------------- //
    // ----------------------- Overrides ----------------------- //

    @Override
    protected void InflateLayout() {
        // Set content view
        setContentView(R.layout.activity_task);
    }

    @Override
    protected void FindViews() {
        // Init view holder
        ui = new ActivityHolder.Task();
        holder = ui;

        // Activity View
        ui.ibSync               = (ImageButton)     findViewById(R.id.ib_toolbar_sync);
        ui.ibBack               = (ImageButton)     findViewById(R.id.ib_toolbar_back);
        ui.rlvList              = (RlListView)      findViewById(R.id.rlv_task);
    }

    @Override
    protected void SetViews() {
        // Initialize List
        listAdpater = new TaskListAdapter(this, ui.rlvList.GetListView());
        listAdpater.SetListener(this);
        InitList();
    }

    @Override
    public void onItemClick(Task task) {
        // Open Task Info Dialog
        RlDialog.Show(new Dialog.TaskInfo(task));
    }

    // ----------------------- Public APIs ----------------------- //
    public static void Start(BaseActivity parentActivity)
    {
        BaseActivity.Start(parentActivity, TaskActivity.class, -1, null, Constants.RequestCodes.INVALID, null);
    }

    public static void RefreshList() {
        // Get current activity
        BaseActivity currentActivity = App.GetCurrentActivity();

        // Check if task activity is active
        if ((currentActivity != null) && (currentActivity.getClass().equals(TaskActivity.class))) {
            // Re-initialize Tasks UI
            ((TaskActivity) currentActivity).InitList();
        }
    }

    // Button Click APIs
    public void ButtonClickBack(View view)
    {
        super.onBackPressed();
    }

    public void ButtonClickSync(View view)
    {
        SyncDbTask syncTask = new SyncDbTask(this, true);
        syncTask.execute();
    }

    public void ButtonClickAddTask(View view) {
        //Pop up the Add Task Dialog
        RlDialog.Show(new Dialog.AddTask());
    }

    // ----------------------- Private APIs ----------------------- //
    private void InitList() {
        // Reset Adapter
        listAdpater.Clear();

        // Add all tasks to list in reverse order of ID
        ArrayList<Task> tasks = DbHelper.taskTable.GetClosedTasks();
        tasks.addAll(DbHelper.taskTable.GetOpenTasks());
        for (int i = tasks.size() - 1; i >= 0; i--) {
            listAdpater.Add(new ListItem.Task(tasks.get(i)));
        }

        // Update List UI
        if (tasks.size() == 0) {
            ui.rlvList.ShowBlank();
        } else {
            ui.rlvList.ShowList();
        }
    }
}
