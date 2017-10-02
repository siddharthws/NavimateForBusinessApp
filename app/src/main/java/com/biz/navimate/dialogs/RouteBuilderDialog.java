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
import com.biz.navimate.debug.Dbg;
import com.biz.navimate.lists.RemovableListAdapter;
import com.biz.navimate.objects.Dialog;
import com.biz.navimate.objects.Lead;
import com.biz.navimate.objects.ListItem;
import com.biz.navimate.viewholders.DialogHolder;
import com.biz.navimate.views.RlDialog;

/**
 * Created by Siddharth on 02-10-2017.
 */

public class RouteBuilderDialog     extends     BaseDialog
                                    implements  View.OnClickListener
{
    private static final String TAG = "ROUTE_BUILDER_DIALOG";

    // ----------------------- Classes ---------------------------//
    // ----------------------- Interfaces ----------------------- //
    // ----------------------- Globals ----------------------- //
    private DialogHolder.RouteBuilder ui = null;
    private RemovableListAdapter adapter = null;

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
            adapter.Add(new ListItem.Lead(lead));
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
                Dbg.Toast(context, "Pick Leads...", Toast.LENGTH_SHORT);
                break;
            }
            case R.id.btn_build:
            {
                Dbg.Toast(context, "Building Route...", Toast.LENGTH_SHORT);
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
    // ----------------------- Public APIs ----------------------- //
    // ----------------------- Private APIs ----------------------- //
}
