package com.biz.navimate.dialogs;

import android.content.Context;
import android.support.v7.widget.AppCompatCheckBox;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.biz.navimate.R;
import com.biz.navimate.activities.BaseActivity;
import com.biz.navimate.activities.LeadListActivity;
import com.biz.navimate.application.App;
import com.biz.navimate.constants.Constants;
import com.biz.navimate.database.DbHelper;
import com.biz.navimate.debug.Dbg;
import com.biz.navimate.interfaces.IfaceResult;
import com.biz.navimate.interfaces.IfaceServer;
import com.biz.navimate.lists.RemovableListAdapter;
import com.biz.navimate.objects.Dialog;
import com.biz.navimate.objects.core.ObjLead;
import com.biz.navimate.objects.ListItem;
import com.biz.navimate.objects.LocationObj;
import com.biz.navimate.objects.Route;
import com.biz.navimate.objects.Statics;
import com.biz.navimate.objects.core.ObjNvm;
import com.biz.navimate.objects.core.ObjNvmCompact;
import com.biz.navimate.runnables.LocationUpdateRunnable;
import com.biz.navimate.server.GetObjectDetailsTask;
import com.biz.navimate.services.LocationService;
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
    private LocationUpdateRunnable locationUpdateRunnable = null;

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
        ui.cbOptimize      = (AppCompatCheckBox) ui.dialogView.findViewById(R.id.cb_optimize);
        ui.btnBuild  = (Button)     ui.dialogView.findViewById(R.id.btn_build);
        ui.btnCancel  = (Button)     ui.dialogView.findViewById(R.id.btn_cancel);
    }

    @Override
    protected void SetContentView()
    {
        // Init Location Update runnable
        locationUpdateRunnable = new LocationUpdateRunnable(context);

        // Get current data
        Dialog.RouteBuilder currentData = (Dialog.RouteBuilder) data;

        // Init list
        adapter = new RemovableListAdapter(context, ui.lvLeads);
        for (ObjLead lead : currentData.leads) {
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
                        public void onLeadPicked(ObjNvmCompact compactObj) {
                            // Get lead object
                            GetObjectDetailsTask objTask = new GetObjectDetailsTask(context, Constants.Template.TYPE_LEAD, compactObj.id);
                            objTask.SetListener(new IfaceServer.GetObjectDetails() {
                                @Override
                                public void onObjectDetailsSuccess(ObjNvm obj) {
                                    adapter.Add(new ListItem.Lead((ObjLead) obj, false));
                                }

                                @Override
                                public void onObjectDetailsFailed() {
                                    Dbg.Toast(context,"Could not get details for " + compactObj.name, Toast.LENGTH_SHORT);
                                }
                            });
                            objTask.execute();
                        }
                    });
                    LeadListActivity.StartPicker(currentActivity);
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
        ArrayList<ObjLead> leads = new ArrayList<>();
        for (ListItem.Lead leadItem : leadItems)
        {
            ObjLead lead = leadItem.lead;
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
        LocationObj currentLoc = LocationService.cache.GetLocation();
        if (Statics.IsPositionValid(currentLoc.latlng)) {
            checkpoints.add(0, currentLoc.latlng);
        } else {
            // Try enabling location
            locationUpdateRunnable.SetInitListener(new LocationUpdateRunnable.IfaceRunnableLocationInit() {
                @Override
                public void onLocationInitSuccess(LocationObj location) {
                    ButtonClickBuild();
                }

                @Override
                public void onLocationInitError() {
                    Dbg.Toast(context, "Current location is not available", Toast.LENGTH_SHORT);
                }
            });
            locationUpdateRunnable.Post(0);
            return;
        }

        // Start Directions Task
        directionsTask = new DirectionsTask(context, true, ui.cbOptimize.isChecked(), checkpoints);
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
            ObjLead lead = leadItem.lead;
            checkpoints.add(lead.place.GetLatLng());
        }

        return checkpoints;
    }
}
