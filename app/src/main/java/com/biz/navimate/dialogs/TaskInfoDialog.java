package com.biz.navimate.dialogs;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.biz.navimate.R;
import com.biz.navimate.objects.Dialog;
import com.biz.navimate.viewholders.DialogHolder;
import com.biz.navimate.views.RlDialog;

/**
 * Created by Siddharth on 01-10-2017.
 */

public class TaskInfoDialog     extends     BaseDialog
                                implements  View.OnClickListener {
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
        ui.tvTitle = (TextView) ui.dialogView.findViewById(R.id.tv_title);
        ui.tvDescription = (TextView) ui.dialogView.findViewById(R.id.tv_description);
        ui.tvPhone = (TextView) ui.dialogView.findViewById(R.id.tv_phone);
        ui.tvEmail = (TextView) ui.dialogView.findViewById(R.id.tv_email);
        ui.btnSubmit = (Button) ui.dialogView.findViewById(R.id.btn_submit_form);
        ui.btnDismiss = (Button) ui.dialogView.findViewById(R.id.btn_dismiss);
    }

    @Override
    protected void SetContentView() {
        // Get current data
        Dialog.TaskInfo currentData = (Dialog.TaskInfo) data;

        // Set Text
        ui.tvTitle.setText(currentData.task.lead.title);
        ui.tvDescription.setText(currentData.task.lead.description);
        ui.tvPhone.setText(currentData.task.lead.phone);
        ui.tvEmail.setText(currentData.task.lead.email);

        // Set Listeners
        ui.btnSubmit.setOnClickListener(this);
        ui.btnDismiss.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        // Get current data
        Dialog.TaskInfo currentData = (Dialog.TaskInfo) data;

        switch (v.getId()) {
            case R.id.btn_dismiss: {
                // Hide this dialog box
                RlDialog.Hide();
                break;
            }
            case R.id.btn_submit_form: {
                RlDialog.Show(new Dialog.SubmitForm(currentData.task.template, currentData.task.id, false));
                break;
            }
        }
    }
    // ----------------------- Public APIs ----------------------- //
    // ----------------------- Private APIs ----------------------- //
}
