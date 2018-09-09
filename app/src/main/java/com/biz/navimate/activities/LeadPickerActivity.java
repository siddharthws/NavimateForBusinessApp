package com.biz.navimate.activities;

import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;

import com.biz.navimate.R;
import com.biz.navimate.constants.Constants;
import com.biz.navimate.database.DbHelper;
import com.biz.navimate.lists.LeadListAdapter;
import com.biz.navimate.objects.core.ObjLead;
import com.biz.navimate.objects.ListItem;
import com.biz.navimate.objects.core.ObjNvmCompact;
import com.biz.navimate.viewholders.ActivityHolder;
import com.biz.navimate.views.RlListView;

import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

public class LeadPickerActivity extends         BaseActivity
                                implements      AdapterView.OnItemClickListener
{
    // ----------------------- Constants ----------------------- //
    private static final String TAG = "PICK_CONTACTS_ACTIVITY";

    // ----------------------- Interfaces ----------------------- //
    // ----------------------- Globals ----------------------- //
    private ActivityHolder.LeadPicker  ui              = null;
    private LeadListAdapter listAdpater                  = null;

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
        listAdpater = new LeadListAdapter(this, ui.rlvList.GetListView());
        InitList();

        // Set Listeners
        ui.rlvList.GetListView().setOnItemClickListener(this);
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
        ListItem.Lead clickedItem = (ListItem.Lead) listAdpater.getItem(i);

        // Send activity result
        Intent resultData = new Intent();
        resultData.putExtra(Constants.Extras.PICKED_OBJECT,
                            new ObjNvmCompact(  clickedItem.lead.serverId,
                                                clickedItem.lead.name));
        setResult(RESULT_OK, resultData);

        // Finish this activity
        finish();
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
    private void InitList() {
        // Reset Adapter
        listAdpater.Clear();

        // Add all tasks to list in reverse order of ID
        CopyOnWriteArrayList<ObjLead> leads = (CopyOnWriteArrayList<ObjLead>) DbHelper.leadTable.GetAll();
        for (int i = 0; i < leads.size(); i++) {
            listAdpater.Add(new ListItem.Lead(leads.get(i), false));
        }

        // Update List UI
        if (leads.size() == 0) {
            ui.rlvList.ShowBlank();
        } else {
            ui.rlvList.ShowList();
        }
    }
}
