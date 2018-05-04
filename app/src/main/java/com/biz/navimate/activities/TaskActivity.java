package com.biz.navimate.activities;

import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;

import com.biz.navimate.R;
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
        ui.lvList               = (ListView)        findViewById(R.id.lv_task);
    }

    @Override
    protected void SetViews() {
        // Initialize List
        listAdpater = new TaskListAdapter(this, ui.lvList);
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

    // Button Click APIs
    public void ButtonClickBack(View view)
    {
        super.onBackPressed();
    }

    public void ButtonClickSync(View view)
    {
        SyncDbTask syncTask = new SyncDbTask(this, true, false, true, true);
        syncTask.SetListener(() -> {
            // Re-Initialize UI
            InitList();
        });
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

        // Add form items in reverse order
        ArrayList<Task> tasks = DbHelper.taskTable.GetOpenTasks();
        for (int i = tasks.size() - 1; i >= 0; i--) {
            listAdpater.Add(new ListItem.Task(tasks.get(i)));
        }
    }
}
