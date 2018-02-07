package com.biz.navimate.dialogs;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.biz.navimate.R;
import com.biz.navimate.constants.Constants;
import com.biz.navimate.database.DbHelper;
import com.biz.navimate.debug.Dbg;
import com.biz.navimate.lists.SpinnerAdapter;
import com.biz.navimate.objects.Data;
import com.biz.navimate.objects.Lead;
import com.biz.navimate.objects.Template;
import com.biz.navimate.objects.Value;
import com.biz.navimate.server.AddTaskTask;
import com.biz.navimate.viewholders.DialogHolder;
import com.biz.navimate.views.RlDialog;
import com.biz.navimate.views.RlFormField;

import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by Sai_Kameswari on 25-12-2017.
 */

public class AddTaskDialog    extends     BaseDialog
                              implements  View.OnClickListener
{
    // ----------------------- Constants ----------------------- //
    private static final String TAG = "ADD_TASK_DIALOG";

    // ----------------------- Classes ---------------------------//
    // ----------------------- Interfaces ----------------------- //
    // ----------------------- Globals ----------------------- //
    private DialogHolder.AddTask ui = null;
    private Lead selectedLead;
    private Template selectedFormTemplate, selectedTaskTemplate;
    private SpinnerAdapter leadAdapter, formTemplateAdapter, taskTemplateAdapter;

    // ----------------------- Constructor ----------------------- //
    public AddTaskDialog(Context context)
    {
        super(context);
    }

    // ----------------------- Overrides ----------------------- //
    @Override
    protected void SetViewHolder(LayoutInflater inflater, ViewGroup container)
    {
        // Init Holder
        ui = new DialogHolder.AddTask();
        holder = ui;

        // Inflate View
        ui.dialogView          = inflater.inflate(R.layout.dialog_add_task, container);

        // Find Views
        ui.leadSelectSpinner  = (Spinner)   ui.dialogView.findViewById(R.id.lead_select_spinner);
        ui.taskSelectSpinner  = (Spinner)   ui.dialogView.findViewById(R.id.task_select_spinner);
        ui.formSelectSpinner  = (Spinner)   ui.dialogView.findViewById(R.id.form_select_spinner);
        ui.llFields           = (LinearLayout)   ui.dialogView.findViewById(R.id.ll_fields_task);
        ui.fields             = new ArrayList<>();
        ui.btnCreate          = (Button)     ui.dialogView.findViewById(R.id.btn_create);
        ui.btnCancel          = (Button)     ui.dialogView.findViewById(R.id.btn_cancel);
    }

    @Override
    protected void SetContentView()
    {
        //Init Adapters
        leadAdapter = new SpinnerAdapter(context, ui.leadSelectSpinner);
        formTemplateAdapter = new SpinnerAdapter(context, ui.formSelectSpinner);
        taskTemplateAdapter = new SpinnerAdapter(context, ui.taskSelectSpinner);

        //Add Descriptive Items To adapters
        leadAdapter.AddItem("Select Lead", Constants.Misc.ID_INVALID);
        formTemplateAdapter.AddItem("Select Form Template", Constants.Misc.ID_INVALID);
        taskTemplateAdapter.AddItem("Select Task Template", Constants.Misc.ID_INVALID);

        //Add Items to Adapters
        for(Lead leadObject : (CopyOnWriteArrayList<Lead>) DbHelper.leadTable.GetAll())
        {
            leadAdapter.AddItem(leadObject.title, leadObject.dbId);
        }
        for(Template templateObject : (CopyOnWriteArrayList<Template>) DbHelper.templateTable.GetAll())
        {
            switch (templateObject.type)
            {
                case Constants.Template.TYPE_FORM:
                {
                    formTemplateAdapter.AddItem(templateObject.name, templateObject.dbId);
                    break;
                }
                case Constants.Template.TYPE_TASK:
                {
                    taskTemplateAdapter.AddItem(templateObject.name, templateObject.dbId);
                    break;
                }
            }
        }

        //Set listeners for all Adapters
        leadAdapter.SetListener(new SpinnerAdapter.IfaceSpinner() {
            @Override
            public void onItemSelected(long id) {
                selectedLead = (Lead) DbHelper.leadTable.GetById(id);
            }
        });
        formTemplateAdapter.SetListener(new SpinnerAdapter.IfaceSpinner() {
            @Override
            public void onItemSelected(long id) {
                selectedFormTemplate = (Template) DbHelper.templateTable.GetById(id);
            }
        });
        taskTemplateAdapter.SetListener(new SpinnerAdapter.IfaceSpinner() {
            @Override
            public void onItemSelected(long id) {
                selectedTaskTemplate = (Template) DbHelper.templateTable.GetById(id);
                InitTaskFields();
            }
        });

        // Set Listeners for Buttons
        ui.btnCreate.setOnClickListener(this);
        ui.btnCancel.setOnClickListener(this);
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.btn_create:
            {
                ButtonClickAddTask();
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
    private void InitTaskFields()
    {
        Data data= (Data) DbHelper.dataTable.GetById(selectedTaskTemplate.defaultDataId);

        //clear existing fields
        ui.llFields.removeAllViews();
        ui.fields.clear();

        // Set Form Fields
        for (Long valueId  : data.valueIds) {
            Value value = (Value) DbHelper.valueTable.GetById(valueId);
            RlFormField fieldUi = new RlFormField(context, value, false);
            ui.llFields.addView(fieldUi);
            ui.fields.add(fieldUi);
        }
    }

    private void ButtonClickAddTask()
    {
        // Validate entered data
        if(selectedLead == null || selectedFormTemplate == null || selectedTaskTemplate == null)
        {
            Dbg.Toast(context, "Please select some data..", Toast.LENGTH_SHORT);
            return;
        }

        // Get Value object in each form field
        ArrayList<Value> values = new ArrayList<>();
        for (RlFormField rlField : ui.fields) {
            // Get Value from UI and save in table
            Value value = rlField.GetValue();
            values.add(value);
        }

        AddTaskTask addTaskTask = new AddTaskTask(context, selectedLead.serverId, selectedFormTemplate.serverId, selectedTaskTemplate.serverId, values);
        addTaskTask.execute();
    }
}

