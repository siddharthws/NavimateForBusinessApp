package com.biz.navimate.dialogs;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.biz.navimate.R;
import com.biz.navimate.constants.Constants;
import com.biz.navimate.database.DbHelper;
import com.biz.navimate.objects.Data;
import com.biz.navimate.objects.Dialog;
import com.biz.navimate.objects.Field;
import com.biz.navimate.objects.Form;
import com.biz.navimate.objects.FormEntry;
import com.biz.navimate.objects.Lead;
import com.biz.navimate.objects.Template;
import com.biz.navimate.objects.Value;
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
        Dialog.TaskInfo currentData = (Dialog.TaskInfo) data;
        Lead lead = (Lead) DbHelper.leadTable.GetById(currentData.task.leadId);

        // Set Text
        ui.btnLead.setText(lead.title);

        // Set Form Fields
        Data data = (Data) DbHelper.dataTable.GetById(currentData.task.dataId);
        for (Long valueId  : data.valueIds) {
            Value value = (Value) DbHelper.valueTable.GetById(valueId);
            Field field = (Field) DbHelper.fieldTable.GetById(value.fieldId);
            FormEntry.Base entry = FormEntry.Parse(field, value.value);
            RlFormField fieldUi = new RlFormField(context, entry, true);
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
        Dialog.TaskInfo currentData = (Dialog.TaskInfo) data;
        Lead lead = (Lead) DbHelper.leadTable.GetById(currentData.task.leadId);
        Template formTemplate = (Template) DbHelper.templateTable.GetById(currentData.task.formTemplateId);

        switch (v.getId()) {
            case R.id.btn_dismiss: {
                // Hide this dialog box
                RlDialog.Hide();
                break;
            }
            case R.id.btn_submit_form: {
                // Create new form object
                Form form = new Form(currentData.task.formTemplateId, currentData.task.dbId, Constants.Misc.ID_INVALID, false);

                // Open Submit form dialog with this form
                RlDialog.Show(new Dialog.SubmitForm(form, false));
                break;
            }
            case R.id.btn_lead: {
                RlDialog.Show(new Dialog.Lead(lead));
                break;
            }
            case R.id.btn_maps: {
                Intent intent = new Intent(Intent.ACTION_VIEW,
                                            Uri.parse(  "geo:" + lead.position.latitude + "," +
                                                                 lead.position.longitude +
                                                         "?q=" + lead.position.latitude + "," +
                                                                 lead.position.longitude +
                                                           "(" + lead.title + ")"));
                context.startActivity(intent);
                break;
            }
        }
    }
    // ----------------------- Public APIs ----------------------- //
    // ----------------------- Private APIs ----------------------- //
}
