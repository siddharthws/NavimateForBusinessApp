package com.biz.navimate.activities;

import android.content.Intent;
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
import java.util.concurrent.CopyOnWriteArrayList;

public class LeadPickerActivity extends         BaseActivity
                                implements      AdapterView.OnItemClickListener,
                                                NvmToolbar.IfaceToolbarSearch,
                                                IfaceServer.GetObjectList,
                                                RlListView.LoadMoreListener {
    // ----------------------- Constants ----------------------- //
    private static final String TAG = "PICK_CONTACTS_ACTIVITY";

    // ----------------------- Interfaces ----------------------- //
    // ----------------------- Globals ----------------------- //
    private ActivityHolder.LeadPicker  ui = null;
    private GenericListAdapter adapter = null;
    private GetObjectListTask objListTask = null;
    private boolean bTaskRunning = false;
    private String searchText = "";

    // ----------------------- Constructor ----------------------- //
    // ----------------------- Overrides ----------------------- //

    @Override
    protected void InflateLayout() {
        // Set content view
        setContentView(R.layout.activity_lead_picker);
    }

    @Override
    protected void FindViews() {
        // Init view holder
        ui = new ActivityHolder.LeadPicker();
        holder = ui;

        // Activity View
        ui.rlvList              = (RlListView)      findViewById(R.id.rlv_leads);
    }

    @Override
    protected void SetViews() {
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
        
        switch (id) { }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
    {
        // Get Clicked Object
        ListItem.Lead clickedItem = (ListItem.Lead) adapter.getItem(i);

        // Send activity result
        Intent resultData = new Intent();
        resultData.putExtra(Constants.Extras.PICKED_OBJECT,
                            new ObjNvmCompact(  clickedItem.lead.serverId,
                                                clickedItem.lead.name));
        setResult(RESULT_OK, resultData);

        // Finish this activity
        finish();
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
    public static void Start(BaseActivity parentActivity)
    {
        BaseActivity.Start(parentActivity, LeadPickerActivity.class, -1, null, Constants.RequestCodes.LEAD_PICKER, null);
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
