package com.biz.navimate.activities;

import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.biz.navimate.R;
import com.biz.navimate.constants.Constants;
import com.biz.navimate.database.DbHelper;
import com.biz.navimate.debug.Dbg;
import com.biz.navimate.lists.LeadListAdapter;
import com.biz.navimate.objects.Lead;
import com.biz.navimate.objects.ListItem;
import com.biz.navimate.viewholders.ActivityHolder;
import com.biz.navimate.views.TvCalibri;

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
    private ArrayList<Long> pickedLeads                  = null;

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
        ui.ibDone               = (ImageButton)     findViewById(R.id.ib_toolbar_done);
        ui.ibBack               = (ImageButton)     findViewById(R.id.ib_toolbar_back);
        ui.tvSelectedCount      = (TvCalibri)       findViewById(R.id.tv_selected_count);
        ui.lvList               = (ListView)        findViewById(R.id.lv_leads);
    }

    @Override
    protected void SetViews() {
        // Init picked lists
        pickedLeads     = new ArrayList<>();
        ui.tvSelectedCount.setText("0 leads picked...");

        // Initialize List
        listAdpater = new LeadListAdapter(this, ui.lvList);
        for (Lead lead : (CopyOnWriteArrayList<Lead>) DbHelper.leadTable.GetAll())
        {
            listAdpater.Add(new ListItem.Lead(lead, false));
        }

        // Set Listeners
        ui.lvList.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
    {
        // Get Clicked Object
        ListItem.Lead clickedItem = (ListItem.Lead) listAdpater.getItem(i);

        // Check if item was already selected
        if (pickedLeads.contains(clickedItem.lead.dbId))
        {
            // Remove from selected list
            pickedLeads.remove((Object) clickedItem.lead.dbId);

            // Remove tick mark from item view
            clickedItem.bSelected = false;
        }
        else
        {
            // Add to picked leads
            pickedLeads.add(clickedItem.lead.dbId);

            // Add a tick icon to view to indicate selection
            clickedItem.bSelected = true;
        }

        // Reset selected count
        int numSelected = pickedLeads.size();
        ui.tvSelectedCount.setText(numSelected + " leads selected...");

        // Refresh List Adapter
        listAdpater.notifyDataSetChanged();
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

    public void ButtonClickDone(View view)
    {
        // Ensure some contacts are selected
        if (pickedLeads.size() == 0)
        {
            Dbg.Toast(this, "No leads selected...", Toast.LENGTH_SHORT);
            return;
        }

        // Send activity result
        Intent resultData = new Intent();
        resultData.putExtra(Constants.Extras.LEAD_PICKER, pickedLeads);
        setResult(RESULT_OK, resultData);

        // Finish this activity
        finish();
    }

    // ----------------------- Private APIs ----------------------- //
}
