package com.biz.navimate.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import com.biz.navimate.R;
import com.biz.navimate.constants.Constants;
import com.biz.navimate.interfaces.IfaceServer;
import com.biz.navimate.lists.GenericListAdapter;
import com.biz.navimate.objects.ListItem;
import com.biz.navimate.objects.core.ObjNvmCompact;
import com.biz.navimate.server.GetObjectListTask;
import com.biz.navimate.viewholders.ActivityHolder;
import com.biz.navimate.views.RlListView;
import com.biz.navimate.views.compound.NvmToolbar;

import java.util.ArrayList;

public class LeadListActivity   extends         BaseActivity
                                implements      AdapterView.OnItemClickListener,
                                                NvmToolbar.IfaceToolbarSearch,
                                                IfaceServer.GetObjectList,
                                                RlListView.LoadMoreListener {
    // ----------------------- Constants ----------------------- //
    private static final String TAG = "PICK_CONTACTS_ACTIVITY";

    private static final int MODE_VIEWER = 1;
    private static final int MODE_PICKER = 2;

    // ----------------------- Interfaces ----------------------- //
    // ----------------------- Globals ----------------------- //
    private ActivityHolder.LeadList ui = null;
    private GenericListAdapter adapter = null;
    private GetObjectListTask objListTask = null;
    private boolean bTaskRunning = false;
    private String searchText = "";
    private int mode = 0;

    // ----------------------- Constructor ----------------------- //
    // ----------------------- Overrides ----------------------- //

    @Override
    protected void InflateLayout() {
        // Set content view
        setContentView(R.layout.activity_lead_list);
    }

    @Override
    protected void FindViews() {
        // Init view holder
        ui = new ActivityHolder.LeadList();
        holder = ui;

        // Activity View
        ui.rlvList              = (RlListView)      findViewById(R.id.rlv_leads);
    }

    @Override
    protected void SetViews() {
        // Read extras
        mode = getIntent().getExtras().getInt(Constants.Extras.MODE);

        // Initialize List
        adapter = new GenericListAdapter(this, ui.rlvList.GetListView(), true);
        GetObjects(0, 30);

        // Set Listeners
        ui.rlvList.GetListView().setOnItemClickListener(this);
        ui.rlvList.SetLoadMoreListener(this);
        ui.toolbar.SetSearchListener(this);
    }

    @Override
    public void onToolbarButtonClick(int id) {
        super.onToolbarButtonClick(id);
        
        switch (id) {
            case R.id.ib_tb_add:
                LeadDetailsActivity.Start(this);
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
    {
        // Get Clicked Object
        ListItem.Generic clickedItem = (ListItem.Generic) adapter.getItem(i);

        if (mode == MODE_VIEWER) {
            LeadDetailsActivity.Start(this, clickedItem.sId, false);
        } else {
            // Send activity result
            Intent resultData = new Intent();
            resultData.putExtra(Constants.Extras.PICKED_OBJECT,
                    new ObjNvmCompact(  clickedItem.sId, clickedItem.title));
            setResult(RESULT_OK, resultData);

            // Finish this activity
            finish();
        }
    }

    @Override
    public void onToolbarSearch(String text) {
        // Clear list
        adapter.Clear();

        // Get Searched objects
        this.searchText = text;
        GetObjects(0, 30);
    }

    @Override
    public void onLoadMore() {
        //Get more products
        GetObjects(adapter.getCount(), 10);
    }

    @Override
    public void onObjectListSuccess(ArrayList<ObjNvmCompact> objects, int totalCount) {
        //Add products to list
        AddToList(objects, totalCount);
        bTaskRunning = false;
    }

    @Override
    public void onObjectListFailed() {
        // Show error UI
        ui.rlvList.ShowError();
        bTaskRunning = false;
    }

    // ----------------------- Public APIs ----------------------- //
    public static void StartViewer(BaseActivity parentActivity)
    {
        Bundle extras = new Bundle();
        extras.putInt(Constants.Extras.MODE, MODE_VIEWER);

        BaseActivity.Start(parentActivity, LeadListActivity.class, -1, extras, Constants.RequestCodes.INVALID, null);
    }

    public static void StartPicker(BaseActivity parentActivity)
    {
        Bundle extras = new Bundle();
        extras.putInt(Constants.Extras.MODE, MODE_PICKER);

        BaseActivity.Start(parentActivity, LeadListActivity.class, -1, extras, Constants.RequestCodes.LEAD_PICKER, null);
    }

    // Button Click APIs
    public void ButtonClickBack(View view)
    {
        super.onBackPressed();
    }

    // ----------------------- Private APIs ----------------------- //
    private void GetObjects(int startIndex, int count){
        // Check if objectList task is still running
        if(objListTask != null && bTaskRunning){
            objListTask.cancel(true);
        }

        //Start Task to get objects
        objListTask = new GetObjectListTask(this, Constants.Template.TYPE_LEAD, startIndex, count, searchText);
        objListTask.SetListener(this);
        objListTask.execute();
        bTaskRunning = true;

        // Show waiting UI in list
        ui.rlvList.ShowWaiting();
    }

    private void AddToList(ArrayList<ObjNvmCompact> objects, int totalCount){
        // Put object ids and names contents into listAdapter
        for (ObjNvmCompact obj : objects) {
            adapter.Add(new ListItem.Generic(0L, obj.id, obj.name, 0,0, R.drawable.bg_white_shadow_slant));
        }

        // Update List UI
        if (adapter.getCount() == 0) {
            ui.rlvList.ShowBlank();
        } else {
            ui.rlvList.ShowList();
        }

        // show load more if items still left to be fetched
        if (adapter.getCount() < totalCount){
            ui.rlvList.ToggleLoadMore(true);
        }
    }
}
