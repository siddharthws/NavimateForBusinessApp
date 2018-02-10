package com.biz.navimate.dialogs;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.biz.navimate.R;
import com.biz.navimate.activities.HomescreenActivity;
import com.biz.navimate.constants.Constants;
import com.biz.navimate.database.DbHelper;
import com.biz.navimate.debug.Dbg;
import com.biz.navimate.lists.SpinnerAdapter;
import com.biz.navimate.objects.Data;
import com.biz.navimate.objects.Dialog;
import com.biz.navimate.objects.Form;
import com.biz.navimate.objects.LocationObj;
import com.biz.navimate.objects.LocationUpdate;
import com.biz.navimate.objects.Task;
import com.biz.navimate.objects.Template;
import com.biz.navimate.objects.Value;
import com.biz.navimate.runnables.LocationUpdateRunnable;
import com.biz.navimate.server.SyncFormsTask;
import com.biz.navimate.services.LocationService;
import com.biz.navimate.viewholders.DialogHolder;
import com.biz.navimate.views.RlDialog;
import com.biz.navimate.views.RlFormField;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

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
    private LocationUpdateRunnable locUpdateRunnable = null;
    private SpinnerAdapter spinnerAdapter = null;

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
        ui.spTemplate   = (Spinner) ui.dialogView.findViewById(R.id.spFormTemplate);
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
        Form form = currentData.form;

        // Initialize Spinner
        spinnerAdapter = new SpinnerAdapter(context, ui.spTemplate);

        // Add a descriptive item to spinner
        spinnerAdapter.AddItem("-- select form --", Constants.Misc.ID_INVALID);

        // Add all form templates to spinner
        for (Template template : (CopyOnWriteArrayList<Template>) DbHelper.templateTable.GetAll()) {
            if (template.type == Constants.Template.TYPE_FORM) {
                // Add to spinner
                spinnerAdapter.AddItem(template.name, template.dbId);

                // Set selected template if task's form template matches this
                if (template.dbId == currentData.form.templateId) {
                    ui.spTemplate.setSelection(spinnerAdapter.getCount());
                }
            }
        }

        // Check if task is attached to the form
        if (form.taskId != Constants.Misc.ID_INVALID) {
            // Make checkbox viisble
            ui.cbCloseTask.setVisibility(View.VISIBLE);

            // Set Close task Checkbox
            ui.cbCloseTask.setChecked(form.bCloseTask);
        }

        // Disable functionality for read only dialogs
        if (currentData.bReadOnly) {
            ui.btnSubmit.setVisibility(View.GONE);
            ui.cbCloseTask.setEnabled(false);
            ui.spTemplate.setEnabled(false);
        }

        // Set fields using value IDs
        ArrayList<Long> valueIds = new ArrayList<>();
        if (form.dataId != Constants.Misc.ID_INVALID) {
            // Init Field with values saved in form
            valueIds = ((Data) DbHelper.dataTable.GetById(form.dataId)).valueIds;
        } else if (form.templateId != Constants.Misc.ID_INVALID) {
            // Init fields with template default values
            Template template = (Template) DbHelper.templateTable.GetById(form.templateId);
            valueIds = ((Data) DbHelper.dataTable.GetById(template.defaultDataId)).valueIds;
        }
        SetFields(valueIds);

        // Set Spinner item click listener
        spinnerAdapter.SetListener(new SpinnerAdapter.IfaceSpinner() {
            @Override
            public void onItemSelected(long id) {
                // Set form's template ID
                form.templateId = id;

                // Get template
                Template template = (Template) DbHelper.templateTable.GetById(id);

                // Get default data of template
                Data defaultData = (Data) DbHelper.dataTable.GetById(template.defaultDataId);

                // Set fields to default data of this template
                SetFields(defaultData.valueIds);
            }
        });

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
        // Update location in form
        ((Dialog.SubmitForm) data).form.latlng = location.latlng;
        SubmitForm();
    }

    @Override
    public void onLocationInitError() {
        ((Dialog.SubmitForm) data).form.latlng = new LatLng(0, 0);
        SubmitForm();
    }
    // ----------------------- Public APIs ----------------------- //
    // ----------------------- Private APIs ----------------------- //
    private void ButtonClickSubmit(){
        // Add Submit Form Location client
        LocationService.AddClient(context, Constants.Location.CLIENT_TAG_SUBMIT_FORM, LocationUpdate.SLOW);

        // Get Value object in each form field
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

        // Update form
        ((Dialog.SubmitForm) data).form.dataId = submitData.dbId;

        // Check for current location
        if (LocationService.IsUpdating()) {
            onLocationInitSuccess(LocationService.cache.GetLocation());
        } else {
            locUpdateRunnable.Post(0);
        }
    }

    // Method to set fields as per given value IDs
    private void SetFields(ArrayList<Long> valueIds) {
        // Clear existing fields
        ui.llFields.removeAllViews();
        ui.fields.clear();

        // Set Form Fields UI using data values
        for (Long valueId  : valueIds) {
            // Get Value Object
            Value value = (Value) DbHelper.valueTable.GetById(valueId);

            // Prepare Form Field UI
            RlFormField fieldUi = new RlFormField(context, value, ((Dialog.SubmitForm) data).bReadOnly);

            // Add view to Linear Layout and save in cache
            ui.llFields.addView(fieldUi);
            ui.fields.add(fieldUi);
        }
    }

    private void SubmitForm() {
        // Get form object
        Form form = ((Dialog.SubmitForm) data).form;

        // Set any remaining data in form before saving
        form.bCloseTask = ui.cbCloseTask.isChecked();
        form.timestamp = System.currentTimeMillis();

        // Save Form Object
        DbHelper.formTable.Save(form);

        // Update Task status if applicable
        if ((form.taskId != Constants.Misc.ID_INVALID) && form.bCloseTask) {
            // Update task object
            Task task = (Task) DbHelper.taskTable.GetById(form.taskId);
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
