package com.biz.navimate.dialogs;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.biz.navimate.R;
import com.biz.navimate.activities.BaseActivity;
import com.biz.navimate.activities.LeadPickerActivity;
import com.biz.navimate.application.App;
import com.biz.navimate.debug.Dbg;
import com.biz.navimate.interfaces.IfaceResult;
import com.biz.navimate.lists.RemovableListAdapter;
import com.biz.navimate.misc.LocationCache;
import com.biz.navimate.objects.Dialog;
import com.biz.navimate.objects.Lead;
import com.biz.navimate.objects.ListItem;
import com.biz.navimate.objects.LocationObj;
import com.biz.navimate.objects.Route;
import com.biz.navimate.objects.Statics;
import com.biz.navimate.tasks.DirectionsTask;
import com.biz.navimate.viewholders.DialogHolder;
import com.biz.navimate.views.RlDialog;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

/**
 * Created by Siddharth on 02-10-2017.
 */

public class RouteBuilderDialog     extends     BaseDialog
                                    implements  View.OnClickListener, DirectionsTask.IfaceDirectionResultListener {
    private static final String TAG = "ROUTE_BUILDER_DIALOG";

    // ----------------------- Classes ---------------------------//
    // ----------------------- Interfaces ----------------------- //
    // ----------------------- Globals ----------------------- //
    private DialogHolder.RouteBuilder ui = null;
    private RemovableListAdapter adapter = null;
    private DirectionsTask directionsTask = null;

    // ----------------------- Constructor ----------------------- //
    public RouteBuilderDialog(Context context)
    {
        super(context);
    }


    // ----------------------- Overrides ----------------------- //
    @Override
    protected void SetViewHolder(LayoutInflater inflater, ViewGroup container)
    {
        // Init Holder
        ui = new DialogHolder.RouteBuilder();
        holder = ui;

        // Inflate View
        ui.dialogView = inflater.inflate(R.layout.dialog_route_builder, container);

        // Find Views
        ui.llAddLeads = (LinearLayout)   ui.dialogView.findViewById(R.id.ll_add_lead);
        ui.lvLeads      = (ListView) ui.dialogView.findViewById(R.id.lv_leads);
        ui.btnBuild  = (Button)     ui.dialogView.findViewById(R.id.btn_build);
        ui.btnCancel  = (Button)     ui.dialogView.findViewById(R.id.btn_cancel);
    }

    @Override
    protected void SetContentView()
    {
        // Get current data
        Dialog.RouteBuilder currentData = (Dialog.RouteBuilder) data;

        // Init list
        adapter = new RemovableListAdapter(context, ui.lvLeads);
        for (Lead lead : currentData.leads) {
            adapter.Add(new ListItem.Lead(lead, false));
        }

        // Set Listeners
        ui.llAddLeads.setOnClickListener(this);
        ui.btnBuild.setOnClickListener(this);
        ui.btnCancel.setOnClickListener(this);
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.ll_add_lead :
            {
                BaseActivity currentActivity = App.GetCurrentActivity();
                if (currentActivity != null) {
                    currentActivity.SetLeadPickerResultListener(new IfaceResult.LeadPicker() {
                        @Override
                        public void onLeadPicked(ArrayList<Integer> leadIds) {
                            for (Integer leadId : leadIds) {
                                adapter.Add(new ListItem.Lead(Statics.GetLeadById(leadId), false));
                            }
                        }
                    });
                    LeadPickerActivity.Start(currentActivity);
                }
                break;
            }
            case R.id.btn_build:
            {
                ButtonClickBuild();
                break;
            }
            case R.id.btn_cancel:
            {
                // Hide this dialog box
                RlDialog.Hide();
                break;
            }
        }
    }

    @Override
    public void OnDirectionResultSuccess(Route.Way route)
    {
        // Get current data
        Dialog.RouteBuilder currentData = (Dialog.RouteBuilder) data;

        // Call Listener
        if (currentData.listener != null)
        {
            currentData.listener.onRouteBuilt(route);
        }
    }

    @Override
    public void OnDirectionResultError()
    {
        // Get current data
        Dialog.RouteBuilder currentData = (Dialog.RouteBuilder) data;

        // Re launch dialog
        ArrayList<ListItem.Lead> leadItems = (ArrayList<ListItem.Lead>) adapter.GetList();
        ArrayList<Lead> leads = new ArrayList<>();
        for (ListItem.Lead leadItem : leadItems)
        {
            Lead lead = leadItem.lead;
            leads.add(lead);
        }
        RlDialog.Show(new Dialog.RouteBuilder(leads, currentData.listener));
    }
    // ----------------------- Public APIs ----------------------- //
    // ----------------------- Private APIs ----------------------- //
    private void ButtonClickBuild()
    {
        // Ensure number of leads is more than 1
        if (adapter.getCount() == 0)
        {
            Dbg.Toast(context, "Select at least 1 leads to route", Toast.LENGTH_SHORT);
            return;
        }

        // Get all checkpoints to route amongst
        ArrayList<LatLng> checkpoints = GetCheckpoints();

        // Add current location as the first one
        LocationObj currentLoc = LocationCache.instance.GetLocation();
        if (Statics.IsPositionValid(currentLoc.latlng)) {
            checkpoints.add(0, currentLoc.latlng);
        } else {
            Dbg.Toast(context, "Current location is not available", Toast.LENGTH_SHORT);
            return;
        }

        // Start Directions Task
        directionsTask = new DirectionsTask(context, true, checkpoints.toArray(new LatLng[checkpoints.size()]));
        directionsTask.SetOnDirectionResultListener(this);
        directionsTask.execute();
    }

    private ArrayList<LatLng> GetCheckpoints() {
        // Get List of leads selected
        ArrayList<ListItem.Lead> leadItems = (ArrayList<ListItem.Lead>) adapter.GetList();

        // Get List of checkpoints from lead locations
        ArrayList<LatLng> checkpoints = new ArrayList<>();
        for (ListItem.Lead leadItem : leadItems)
        {
            Lead lead = leadItem.lead;
            checkpoints.add(lead.position);
        }

        return checkpoints;
    }
}
