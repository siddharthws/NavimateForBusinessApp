package com.biz.navimate.dialogs;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.biz.navimate.R;
import com.biz.navimate.constants.Constants;
import com.biz.navimate.database.DbHelper;
import com.biz.navimate.objects.Data;
import com.biz.navimate.objects.Lead;
import com.biz.navimate.objects.Template;
import com.biz.navimate.objects.Value;
import com.biz.navimate.viewholders.DialogHolder;
import com.biz.navimate.views.RlDialog;
import com.biz.navimate.views.RlFormField;

import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by Sai_Kameswari on 25-12-2017.
 */

public class AddTaskDialog    extends     BaseDialog
                              implements  View.OnClickListener, AdapterView.OnItemSelectedListener
{
    // ----------------------- Constants ----------------------- //
    private static final String TAG = "ADD_TASK_DIALOG";
    CopyOnWriteArrayList<Lead> leadList = null;
    CopyOnWriteArrayList<Template> formTaskList = null;

    // ----------------------- Classes ---------------------------//
    // ----------------------- Interfaces ----------------------- //
    // ----------------------- Globals ----------------------- //
    private DialogHolder.AddTask ui = null;

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
        ui.dialogView = inflater.inflate(R.layout.dialog_add_task, container);

        // Find Views
        ui.leadSelectSpinner  = (Spinner)   ui.dialogView.findViewById(R.id.lead_select_spinner);
        ui.taskSelectSpinner  = (Spinner)   ui.dialogView.findViewById(R.id.task_select_spinner);
        ui.formSelectSpinner  = (Spinner)   ui.dialogView.findViewById(R.id.form_select_spinner);
        ui.llFields      = (LinearLayout)   ui.dialogView.findViewById(R.id.ll_fields_task);
        ui.fields       = new ArrayList<>();
        ui.btnSave   = (Button)     ui.dialogView.findViewById(R.id.btn_save);
        ui.btnCancel = (Button)     ui.dialogView.findViewById(R.id.btn_cancel);
    }

    @Override
    protected void SetContentView()
    {
        //Get Data
        leadList = (CopyOnWriteArrayList<Lead>) DbHelper.leadTable.GetAll();
        formTaskList = (CopyOnWriteArrayList<Template>) DbHelper.templateTable.GetAll();


        ArrayList<String> leads = new ArrayList<>();
        ArrayList<String> forms = new ArrayList<>();
        ArrayList<String> tasks = new ArrayList<>();
        leads.add("Select Lead");
        forms.add("Select Form Template");
        tasks.add("Select Task Template");


        for(Lead leadObject : leadList)
        {
            leads.add(leadObject.title);
        }
        for(Template templateObject : formTaskList)
        {
           switch (templateObject.type)
           {
               case Constants.Template.TYPE_FORM:
               {
                   forms.add(templateObject.name);

                   break;
               }
               case Constants.Template.TYPE_TASK:
               {
                   tasks.add(templateObject.name);
                   break;
               }
           }
        }

        ArrayAdapter<String> leadAdapter,formAdapter,taskAdapter;

        // Create an ArrayAdapter using the string array and a default spinner layout
         leadAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, leads){
                 @Override
                 public boolean isEnabled(int position){
                     if(position == 0)
                     {
                         // Disable the first item from Spinner
                         // First item will be use for hint
                         return false;
                     }
                     else
                     {
                         return true;
                     }
                 }
                 @Override
                 public View getDropDownView(int position, View convertView,
                                             ViewGroup parent) {
                     View view = super.getDropDownView(position, convertView, parent);
                     TextView tv = (TextView) view;
                     if(position == 0){
                         // Set the hint text color gray
                         tv.setTextColor(Color.GRAY);
                     }
                     else {
                         tv.setTextColor(Color.BLACK);
                     }
                     return view;
                 }
             };

         formAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, forms){
                 @Override
                 public boolean isEnabled(int position){
                     if(position == 0)
                     {
                         // Disable the first item from Spinner
                         // First item will be use for hint
                         return false;
                     }
                     else
                     {
                         return true;
                     }
                 }
                 @Override
                 public View getDropDownView(int position, View convertView,
                                             ViewGroup parent) {
                     View view = super.getDropDownView(position, convertView, parent);
                     TextView tv = (TextView) view;
                     if(position == 0){
                         // Set the hint text color gray
                         tv.setTextColor(Color.GRAY);
                     }
                     else {
                         tv.setTextColor(Color.BLACK);
                     }
                     return view;
                 }
             };

         taskAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, tasks){
                @Override
                public boolean isEnabled(int position){
                    if(position == 0)
                    {
                        // Disable the first item from Spinner
                        // First item will be use for hint
                        return false;
                    }
                    else
                    {
                        return true;
                    }
                }
                @Override
                public View getDropDownView(int position, View convertView,
                        ViewGroup parent) {
                    View view = super.getDropDownView(position, convertView, parent);
                    TextView tv = (TextView) view;
                    if(position == 0){
                        // Set the hint text color gray
                        tv.setTextColor(Color.GRAY);
                    }
                    else {
                        tv.setTextColor(Color.BLACK);
                    }
                    return view;
                }
            };

        // Specify the layout to use when the list of choices appears
        leadAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        formAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        taskAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        ui.leadSelectSpinner.setAdapter(leadAdapter);
        ui.formSelectSpinner.setAdapter(formAdapter);
        ui.taskSelectSpinner.setAdapter(taskAdapter);

        ui.leadSelectSpinner.setOnItemSelectedListener(this);
        ui.formSelectSpinner.setOnItemSelectedListener(this);
        ui.taskSelectSpinner.setOnItemSelectedListener(this);




        // Set Listeners
        ui.btnSave.setOnClickListener(this);
        ui.btnCancel.setOnClickListener(this);
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

        switch (adapterView.getId())
        {
            case R.id.lead_select_spinner:
            {
                if(i>0) {
                    String selectedLead = adapterView.getItemAtPosition(i).toString();
                    Toast.makeText(context,"Lead: "+selectedLead, Toast.LENGTH_SHORT).show();
                }
                break;
            }
            case R.id.form_select_spinner:
            {
                if(i>0) {
                    String selectedForm = adapterView.getItemAtPosition(i).toString();
                    Toast.makeText(context,"Form: "+selectedForm, Toast.LENGTH_SHORT).show();
                }
                break;
            }
            case R.id.task_select_spinner:
            {
                if(i>0) {
                    String selectedTask = adapterView.getItemAtPosition(i).toString();
                    populateFields(selectedTask);
                }
                break;
            }
        }
    }
    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    void populateFields(String selectedTask)
    {
        for(Template templateObject : formTaskList)
        {
            switch (templateObject.type)
            {
                case Constants.Template.TYPE_TASK:
                {
                    if (templateObject.name.equals(selectedTask))
                    {

                        Data data= (Data) DbHelper.dataTable.GetById(templateObject.defaultDataId);
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
                    break;
                }
            }
        }

    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.btn_save:
            {
                // Hide this dialog box
                RlDialog.Hide();
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

