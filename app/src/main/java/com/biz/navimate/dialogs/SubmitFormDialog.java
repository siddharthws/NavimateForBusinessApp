package com.biz.navimate.dialogs;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.biz.navimate.R;
import com.biz.navimate.activities.HomescreenActivity;
import com.biz.navimate.constants.Constants;
import com.biz.navimate.database.DbHelper;
import com.biz.navimate.debug.Dbg;
import com.biz.navimate.objects.Data;
import com.biz.navimate.objects.Dialog;
import com.biz.navimate.objects.Form;
import com.biz.navimate.objects.LocationObj;
import com.biz.navimate.objects.LocationUpdate;
import com.biz.navimate.objects.Task;
import com.biz.navimate.objects.Value;
import com.biz.navimate.runnables.LocationUpdateRunnable;
import com.biz.navimate.server.SyncFormsTask;
import com.biz.navimate.services.LocationService;
import com.biz.navimate.viewholders.DialogHolder;
import com.biz.navimate.views.RlDialog;
import com.biz.navimate.views.RlFormField;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

/**
 * Created by Siddharth on 29-09-2017.
 */

public class SubmitFormDialog   extends     BaseDialog
                                implements  View.OnClickListener,
                                            LocationUpdateRunnable.IfaceRunnableLocationInit {
    // ----------------------- Constants ----------------------- //
    private static final String TAG = "SUBMIT_FORM_DIALOG";

    // ----------------------- Classes ---------------------------//
    // ----------------------- Interfaces ----------------------- //
    // ----------------------- Globals ----------------------- //
    private DialogHolder.SubmitForm ui = null;
    private Long submitDataId = Constants.Misc.ID_INVALID;
    private LatLng submitLocation = null;
    private LocationUpdateRunnable locUpdateRunnable = null;

    // ----------------------- Constructor ----------------------- //
    public SubmitFormDialog(Context context)
    {
        super(context);
        locUpdateRunnable = new LocationUpdateRunnable(context);
        locUpdateRunnable.SetInitListener(this);
    }

    // ----------------------- Overrides ----------------------- //
    @Override
    protected void SetViewHolder(LayoutInflater inflater, ViewGroup container)
    {
        // Init Holder
        ui = new DialogHolder.SubmitForm();
        holder = ui;

        // Inflate View
        ui.dialogView = inflater.inflate(R.layout.dialog_submit_form, container);

        // Find Views
        ui.llFields      = (LinearLayout)   ui.dialogView.findViewById(R.id.ll_fields);
        ui.fields       = new ArrayList<>();
        ui.cbCloseTask  = (CheckBox)   ui.dialogView.findViewById(R.id.cbCloseTask);
        ui.btnSubmit    = (Button)     ui.dialogView.findViewById(R.id.btn_submit);
        ui.btnCancel    = (Button)     ui.dialogView.findViewById(R.id.btn_cancel);
    }

    @Override
    protected void SetContentView()
    {
        // Get current data
        Dialog.SubmitForm currentData = (Dialog.SubmitForm) data;

        // Set Form Fields
        for (Long valueId  : currentData.data.valueIds) {
            Value value = (Value) DbHelper.valueTable.GetById(valueId);
            RlFormField fieldUi = new RlFormField(context, value, currentData.bReadOnly);
            ui.llFields.addView(fieldUi);
            ui.fields.add(fieldUi);
        }

        // Hide Submit button if read only dialog
        if (currentData.bReadOnly) {
            ui.btnSubmit.setVisibility(View.GONE);
            ui.cbCloseTask.setEnabled(false);
        }

        // Set Close task Checkbox
        ui.cbCloseTask.setChecked(currentData.bCloseTask);

        // Set Listeners
        ui.btnSubmit.setOnClickListener(this);
        ui.btnCancel.setOnClickListener(this);
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.btn_submit:
            {
                ButtonClickSubmit();
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
    public void onLocationInitSuccess(LocationObj location) {
        submitLocation = location.latlng;
        SubmitForm();
    }

    @Override
    public void onLocationInitError() {
        submitLocation = new LatLng(0, 0);
        SubmitForm();
    }
    // ----------------------- Public APIs ----------------------- //
    // ----------------------- Private APIs ----------------------- //
    private void ButtonClickSubmit(){
        // Add Submit Form Location client
        LocationService.AddClient(context, Constants.Location.CLIENT_TAG_SUBMIT_FORM, LocationUpdate.SLOW);

        // Get Value object in each form field
        ArrayList<Value> values = new ArrayList<>();
        ArrayList<Long> valueIds = new ArrayList<>();
        for (RlFormField rlField : ui.fields) {
            // Get Value from UI and save in table
            Value value = rlField.GetValue();
            DbHelper.valueTable.Save(value);

            // Add to values IDs
            valueIds.add(value.dbId);
        }

        // Create Data to be submitted and save in DB
        Data submitData = new Data(Constants.Misc.ID_INVALID, Constants.Misc.ID_INVALID, Constants.Misc.ID_INVALID, valueIds);
        DbHelper.dataTable.Save(submitData);
        submitDataId = submitData.dbId;

        // Check for current location
        if (LocationService.IsUpdating()) {
            onLocationInitSuccess(LocationService.cache.GetLocation());
        } else {
            locUpdateRunnable.Post(0);
        }
    }

    private void SubmitForm() {
        Dialog.SubmitForm currentData = (Dialog.SubmitForm) data;

        // Create and Save Form Object
        boolean bCloseTask = ui.cbCloseTask.isChecked();
        Form submissionForm = new Form(Constants.Misc.ID_INVALID,
                Constants.Misc.ID_INVALID,
                Constants.Misc.ID_INVALID,
                currentData.taskId,
                submitDataId,
                bCloseTask,
                submitLocation,
                System.currentTimeMillis());
        DbHelper.formTable.Save(submissionForm);

        if (bCloseTask) {
            // Update task object
            Task task = (Task) DbHelper.taskTable.GetById(currentData.taskId);
            task.status = Task.TaskStatus.CLOSED;
            DbHelper.taskTable.Save(task);

            // Refresh Homescreen Tasks
            HomescreenActivity.Refresh();
        }

        // Show Success toast
        Dbg.Toast(context, "Your form has been saved. Syncing now...", Toast.LENGTH_SHORT);

        // Remove Submit Form Location client
        LocationService.RemoveClient(Constants.Location.CLIENT_TAG_SUBMIT_FORM);

        // Close dialog
        RlDialog.Hide();

        // Start Form Sync task
        SyncFormsTask syncForms = new SyncFormsTask(context, false);
        syncForms.execute();
    }
}
