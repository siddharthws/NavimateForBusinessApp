package com.biz.navimate.dialogs;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.PermissionChecker;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.biz.navimate.R;
import com.biz.navimate.activities.BaseActivity;
import com.biz.navimate.application.App;
import com.biz.navimate.debug.Dbg;
import com.biz.navimate.interfaces.IfacePermission;
import com.biz.navimate.objects.Dialog;
import com.biz.navimate.viewholders.DialogHolder;
import com.biz.navimate.views.RlDialog;

/**
 * Created by Siddharth on 01-10-2017.
 */

public class TaskInfoDialog     extends     BaseDialog
                                implements  View.OnClickListener, IfacePermission.Call {
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
        ui.tvAddress = (TextView) ui.dialogView.findViewById(R.id.tv_address);
        ui.btnPhone = (Button) ui.dialogView.findViewById(R.id.btn_phone);
        ui.btnMaps = (Button) ui.dialogView.findViewById(R.id.btn_maps);
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
        ui.tvAddress.setText(currentData.task.lead.address);
        ui.btnPhone.setText("Call : " + currentData.task.lead.phone);
        ui.tvEmail.setText(currentData.task.lead.email);

        // Set Listeners
        ui.btnSubmit.setOnClickListener(this);
        ui.btnDismiss.setOnClickListener(this);
        ui.btnPhone.setOnClickListener(this);
        ui.btnMaps.setOnClickListener(this);
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
            case R.id.btn_phone: {
                // Check permission
                if (ContextCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) == PermissionChecker.PERMISSION_GRANTED) {
                    onCallPermissionSuccess();
                } else {
                    BaseActivity currentActivity = App.GetCurrentActivity();

                    if (currentActivity != null) {
                        currentActivity.SetCallPermissionListener(this);
                        currentActivity.RequestPermission(new String[]{Manifest.permission.CALL_PHONE});
                    } else {
                        Dbg.error(TAG, "Current Activity is null. Cannot ask permission");
                    }
                }
                break;
            }
            case R.id.btn_maps: {
                Intent intent = new Intent(Intent.ACTION_VIEW,
                                            Uri.parse(  "geo:" + currentData.task.lead.position.latitude + "," +
                                                                 currentData.task.lead.position.longitude +
                                                         "?q=" + currentData.task.lead.position.latitude + "," +
                                                                 currentData.task.lead.position.longitude +
                                                           "(" + currentData.task.lead.title + ")"));
                context.startActivity(intent);
                break;
            }
        }
    }

    @Override
    public void onCallPermissionSuccess() {
        Dialog.TaskInfo currentData = (Dialog.TaskInfo) data;
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) == PermissionChecker.PERMISSION_GRANTED) {
            Intent intent = new Intent(Intent.ACTION_CALL);
            intent.setData(Uri.parse("tel:" + currentData.task.lead.phone));
            context.startActivity(intent);
        }
    }

    @Override
    public void onCallPermissionFailure() {

    }
    // ----------------------- Public APIs ----------------------- //
    // ----------------------- Private APIs ----------------------- //
}
