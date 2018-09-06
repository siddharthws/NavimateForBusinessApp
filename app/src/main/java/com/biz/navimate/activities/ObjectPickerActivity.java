package com.biz.navimate.activities;

import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;

import com.biz.navimate.R;
import com.biz.navimate.constants.Constants;
import com.biz.navimate.interfaces.IfaceServer;
import com.biz.navimate.lists.GenericListAdapter;
import com.biz.navimate.objects.ListItem;
import com.biz.navimate.objects.core.ObjNvmCompact;
import com.biz.navimate.server.GetObjectListTask;
import com.biz.navimate.viewholders.ActivityHolder;
import com.biz.navimate.views.RlListView;
import com.biz.navimate.views.compound.EtClearable;
import com.biz.navimate.views.custom.NvmEditText;

import java.util.ArrayList;

public class ObjectPickerActivity    extends     BaseActivity
                                     implements  AdapterView.OnItemClickListener,
                                                 RlListView.LoadMoreListener,
                                                 IfaceServer.GetObjectList,
                                                 NvmEditText.IfaceEditText {
    // ----------------------- Constants ----------------------- //
    private static final String TAG = "OBJECT_PICKER_ACTIVITY";

    // ----------------------- Interfaces ----------------------- //
    // ----------------------- Globals ----------------------- //
    private ActivityHolder.ObjectPicker ui = null;
    private GenericListAdapter listAdpater = null;
    private GetObjectListTask objListTask = null;
    private boolean bTaskRunning = false;

    // ----------------------- Constructor ----------------------- //
    // ----------------------- Overrides ----------------------- //

    @Override
    protected void InflateLayout() {
        // Set content view
        setContentView(R.layout.activity_object_picker);
    }

    @Override
    protected void FindViews() {
        // Init view holder
        ui = new ActivityHolder.ObjectPicker();
        holder = ui;

        // Activity View
        ui.ibBack               = (ImageButton)     findViewById(R.id.ib_toolbar_back);
        ui.rlvList              = (RlListView)      findViewById(R.id.rlv_products);
        ui.etcSearch            = (EtClearable)     findViewById(R.id.etc_search);
    }

    @Override
    protected void SetViews() {
        // Initialize List
        listAdpater = new GenericListAdapter(this, ui.rlvList.GetListView(), true);
        GetObjects(0);

        // Set Listeners
        ui.rlvList.GetListView().setOnItemClickListener(this);
        ui.rlvList.SetLoadMoreListener(this);
        ui.etcSearch.SetListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        // Get Clicked item
        ListItem.Generic clickedItem = (ListItem.Generic) listAdpater.getItem(i);

        // Send activity result with id and name of picked object
        Intent resultData = new Intent();
        resultData.putExtra(Constants.Extras.PICKED_OBJECT, new ObjNvmCompact(clickedItem.sId, clickedItem.title));
        setResult(RESULT_OK, resultData);

        // Finish this activity
        finish();
    }

    @Override
    public void onLoadMore() {
        //Get more products
        GetObjects(listAdpater.getCount());
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

    @Override
    public void onTextChanged(String text) { }

    @Override
    public void onTextChangedDebounced(String text) {
        // Clear list
        listAdpater.Clear();

        // Get Searched objects
        GetObjects(0);
    }
    // ----------------------- Public APIs ----------------------- //
    public static void Start(BaseActivity parentActivity) {
        BaseActivity.Start(parentActivity, ObjectPickerActivity.class, -1, null, Constants.RequestCodes.OBJECT_PICKER, null);
    }

    // Button Click APIs
    public void ButtonClickBack(View view) {
        super.onBackPressed();
    }

    // ----------------------- Private APIs ----------------------- //
    private void AddToList(ArrayList<ObjNvmCompact> objects, int totalCount){
        // Put object ids and names contents into listAdapter
        for (ObjNvmCompact obj : objects) {
            listAdpater.Add(new ListItem.Generic(0L, obj.id, obj.name, 0,0, R.drawable.bg_white_shadow_slant));
        }

        // Update List UI
        if (listAdpater.getCount() == 0) {
            ui.rlvList.ShowBlank();
        } else {
            ui.rlvList.ShowList();
        }

        // show load more if items still left to be fetched
        if (listAdpater.getCount() < totalCount){
            ui.rlvList.ToggleLoadMore(true);
        }
    }

    private void GetObjects(int startIndex){
        // Check if productList task is still running
        if(objListTask !=null && bTaskRunning){
            objListTask.cancel(true);
        }

        //Start Task to get Products
        objListTask = new GetObjectListTask(getApplicationContext(),startIndex,ui.etcSearch.GetText());
        objListTask.SetListener(this);
        objListTask.execute();
        bTaskRunning = true;
        ui.rlvList.ShowWaiting();
    }
}
