package com.biz.navimate.dialogs;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.biz.navimate.R;
import com.biz.navimate.activities.LeadDetailsActivity;
import com.biz.navimate.application.App;
import com.biz.navimate.objects.Dialog;
import com.biz.navimate.objects.core.ObjForm;
import com.biz.navimate.objects.FormEntry;
import com.biz.navimate.objects.Task;
import com.biz.navimate.viewholders.DialogHolder;
import com.biz.navimate.views.RlDialog;
import com.biz.navimate.views.RlFormField;

import java.util.ArrayList;

/**
 * Created by Siddharth on 01-10-2017.
 */

public class TaskInfoDialog     extends     BaseDialog
                                implements  View.OnClickListener{
    // ----------------------- Constants ----------------------- //
    private static final String TAG = "TASK_INFO_DIALOG";

    // ----------------------- Classes ---------------------------//
    // ----------------------- Interfaces ----------------------- //
    // ----------------------- Globals ----------------------- //
    private DialogHolder.TaskInfo ui = null;

    // ----------------------- Constructor ----------------------- //
    public TaskInfoDialog(Context context) {
        super(context);
    }


    // ----------------------- Overrides ----------------------- //
    @Override
    protected void SetViewHolder(LayoutInflater inflater, ViewGroup container) {
        // Init Holder
        ui = new DialogHolder.TaskInfo();
        holder = ui;

        // Inflate View
        ui.dialogView = inflater.inflate(R.layout.dialog_task_info, container);

        // Find Views
        ui.tvId         = (TextView) ui.dialogView.findViewById(R.id.tv_task_id);
        ui.btnLead      = (Button) ui.dialogView.findViewById(R.id.btn_lead);
        ui.llFields     = (LinearLayout)   ui.dialogView.findViewById(R.id.ll_fields_task_info);
        ui.fields       = new ArrayList<>();
        ui.btnMaps      = (Button) ui.dialogView.findViewById(R.id.btn_maps);
        ui.btnSubmit    = (Button) ui.dialogView.findViewById(R.id.btn_submit_form);
        ui.btnDismiss   = (Button) ui.dialogView.findViewById(R.id.btn_dismiss);
    }

    @Override
    protected void SetContentView() {
        // Get current data
        Task task = ((Dialog.TaskInfo) data).task;

        // Set Text
        ui.tvId.setText("ID : " + task.publicId);
        ui.btnLead.setText(task.lead.name);

        // Set Form Fields
        for (FormEntry.Base value  : task.values) {
            RlFormField fieldUi = new RlFormField(context, value, true);
            ui.llFields.addView(fieldUi);
            ui.fields.add(fieldUi);
        }

        // Set Listeners
        ui.btnSubmit.setOnClickListener(this);
        ui.btnDismiss.setOnClickListener(this);
        ui.btnLead.setOnClickListener(this);
        ui.btnMaps.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        // Get current data
        Task task = ((Dialog.TaskInfo) data).task;

        switch (v.getId()) {
            case R.id.btn_dismiss: {
                // Hide this dialog box
                RlDialog.Hide();
                break;
            }
            case R.id.btn_submit_form: {
                // Create new form object
                ObjForm form = new ObjForm(task);

                // Open Submit form dialog with this form
                RlDialog.Show(new Dialog.SubmitForm(form, false));
                break;
            }
            case R.id.btn_lead: {
                LeadDetailsActivity.Start(App.GetCurrentActivity(), task.lead.serverId, false);
                break;
            }
            case R.id.btn_maps: {
                Intent intent = new Intent(Intent.ACTION_VIEW,
                                            Uri.parse(  "geo:" + task.lead.place.lat + "," +
                                                                 task.lead.place.lng +
                                                         "?q=" + task.lead.place.lat + "," +
                                                                 task.lead.place.lng +
                                                           "(" + task.lead.name + ")"));
                context.startActivity(intent);
                break;
            }
        }
    }
    // ----------------------- Public APIs ----------------------- //
    // ----------------------- Private APIs ----------------------- //
}
