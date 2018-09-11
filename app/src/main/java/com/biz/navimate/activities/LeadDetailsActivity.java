package com.biz.navimate.activities;

import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.biz.navimate.R;
import com.biz.navimate.adapters.spinner.BaseSpinnerAdapter;
import com.biz.navimate.constants.Constants;
import com.biz.navimate.database.DbHelper;
import com.biz.navimate.debug.Dbg;
import com.biz.navimate.interfaces.IfaceServer;
import com.biz.navimate.objects.Dialog;
import com.biz.navimate.objects.Field;
import com.biz.navimate.objects.Template;
import com.biz.navimate.objects.core.ObjLead;
import com.biz.navimate.objects.core.ObjNvm;
import com.biz.navimate.objects.templating.fieldvalues.FieldValue;
import com.biz.navimate.server.GetObjectDetailsTask;
import com.biz.navimate.viewholders.ActivityHolder;
import com.biz.navimate.views.RlDialog;
import com.biz.navimate.views.compound.LabelBox;
import com.biz.navimate.views.compound.LocationEditorView;
import com.biz.navimate.views.custom.Dropdown;
import com.biz.navimate.views.templating.fields.FieldView;
import com.biz.navimate.views.templating.fields.TextFieldView;

import java.util.ArrayList;

public class LeadDetailsActivity    extends     BaseActivity
                                    implements  BaseSpinnerAdapter.IfaceSpinner,
                                                TextFieldView.IfaceTextField,
                                                IfaceServer.GetObjectDetails {
    // ----------------------- Constants ----------------------- //
    private static final String TAG = "LEAD_DETAILS_ACTIVITY";

    // ----------------------- Interfaces ----------------------- //
    // ----------------------- Globals ----------------------- //
    protected ActivityHolder.LeadDetails ui = null;

    private ObjLead lead = null;

    private Field nameField = null;
    private ArrayList<Template> templates = null;

    private String id = "";

    // ----------------------- Constructor ----------------------- //
    public static void Start(BaseActivity parentActivity) {
        BaseActivity.Start(parentActivity, LeadDetailsActivity.class, -1, null, Constants.RequestCodes.INVALID, null);
    }

    public static void Start(BaseActivity parentActivity, String id) {
        Bundle extras = new Bundle();
        extras.putString(Constants.Extras.ID, id);

        BaseActivity.Start(parentActivity, LeadDetailsActivity.class, -1, extras, Constants.RequestCodes.INVALID, null);
    }

    // ----------------------- Abstracts ----------------------- //
    // ----------------------- Overrides ----------------------- //
    @Override
    protected void InflateLayout() {
        // Set content view
        setContentView(R.layout.activity_lead_details);
    }

    @Override
    protected void FindViews() {
        // Init view holder
        ui = new ActivityHolder.LeadDetails();
        holder = ui;

        // Find UI
        ui.llRoot           = (LinearLayout)        findViewById(R.id.ll_details_root);
        ui.tfvname          = (TextFieldView)       findViewById(R.id.tfv_name);
        ui.lblLocation      = (LabelBox)            findViewById(R.id.lbl_lead_location);
        ui.lcvLocation      = (LocationEditorView)  findViewById(R.id.lcv_lead_location);
        ui.lblTemplate      = (LabelBox)            findViewById(R.id.lbl_lead_template);
        ui.ddTemplate       = (Dropdown)            findViewById(R.id.dd_lead_template);
        ui.fields           = new ArrayList<>();
    }

    @Override
    protected void SetViews() {
        // Initialize params
        InitParams();

        // Init field object for name
        nameField = new Field("Name", Constants.Template.FIELD_TYPE_TEXT, "");
        nameField.bMandatory = true;

        // Init list of templates
        templates = DbHelper.templateTable.GetByType(Constants.Template.TYPE_LEAD);
        ArrayList<String> templateNames = new ArrayList<>();
        for (Template template : templates) { templateNames.add(template.name); }
        ui.ddTemplate.SetList(templateNames);

        // Set listeners
        ui.toolbar.SetListener(this);
        ui.ddTemplate.SetListener(this);
        ui.tfvname.SetListener(this);

        // Init Lead Details in UI
        InitLeadDetailsUi();
    }

    @Override
    public void onToolbarButtonClick(int id) {
        switch (id) {
            default:
                super.onToolbarButtonClick(id);
        }
    }

    // Listener for template selector dropdown
    @Override
    public void onItemSelected(int position) {
        // Ignore if lead is not yet initialized
        if (lead == null) {
            return;
        }

        // Reset field values as per new template
        lead.SetTemplate(templates.get(position));

        // Reset Field Views
        ResetFieldViews();
    }

    // Listeners for name editor text change events
    @Override
    public void onTextFieldChanged(String text) {
        ui.toolbar.SetTitle(text);
    }

    @Override
    public void onTextFieldChangedDebounced(String text) {}

    // Listeners for getting the data object
    @Override
    public void onObjectDetailsSuccess(ObjNvm obj) {
        // Save in cache
        this.lead = new ObjLead((ObjLead) obj);

        // Update UI
        InitLeadDetailsUi();

        // Hide waiting dialog
        RlDialog.Hide();
    }

    @Override
    public void onObjectDetailsFailed() {
        // Show error text
        Dbg.Toast(this, "Could not get details...", Toast.LENGTH_SHORT);

        // Hide waiting dialog
        RlDialog.Hide();
    }

    // ----------------------- Public APIs ----------------------- //
    // ----------------------- Private APIs ----------------------- //
    // Method to initialize params
    private void InitParams() {
        Bundle extras = getIntent().getExtras();
        if(extras != null) {
            id = extras.getString(Constants.Extras.ID);
        }

        // init lead from id or create new
        if (id.length() > 0) {
            GetObjectDetailsTask objDetailsTask = new GetObjectDetailsTask(this, Constants.Template.TYPE_LEAD, id);
            objDetailsTask.SetListener(this);
            objDetailsTask.execute();
            RlDialog.Show(new Dialog.Waiting("Getting details..."));
        } else {
            lead = new ObjLead();
        }
    }

    // Method to inti UI with details from lead object
    private void InitLeadDetailsUi() {
        // Ignore if lead is not available
        if (lead == null) {
            return;
        }

        // Init toolbar title
        ui.toolbar.SetTitle(lead.name);

        // Init text field view for name
        ui.tfvname.SetValue(FieldValue.newInstance(nameField, lead.name));

        // Init lead name and location
        ui.lcvLocation.Set(lead.place);

        // Set currently selected template
        ui.ddTemplate.SetSelectionNoCallback(templates.indexOf(lead.template));

        // Init fields
        ResetFieldViews();
    }

    // Methods to reset all field editors being displayed as per current cache data
    private void ResetFieldViews() {
        // Remove all field views from UI & cache
        for (FieldView fieldView : ui.fields) {
            ui.llRoot.removeView(fieldView);
        }
        ui.fields.clear();

        // Add new field editor views as per current data to UI and cache
        for (FieldValue value : lead.values) {
            FieldView view = FieldView.newInstance(this, ui.llRoot, value);
            ui.fields.add(view);
        }
    }
}
