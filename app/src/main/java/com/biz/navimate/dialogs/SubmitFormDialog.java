package com.biz.navimate.dialogs;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;

import com.biz.navimate.R;
import com.biz.navimate.objects.Dialog;
import com.biz.navimate.objects.Form;
import com.biz.navimate.server.SubmitFormTask;
import com.biz.navimate.viewholders.DialogHolder;
import com.biz.navimate.views.RlDialog;

/**
 * Created by Siddharth on 29-09-2017.
 */

public class SubmitFormDialog extends BaseDialog implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {
    // ----------------------- Constants ----------------------- //
    private static final String TAG = "SUBMIT_FORM_DIALOG";

    // ----------------------- Classes ---------------------------//
    // ----------------------- Interfaces ----------------------- //
    // ----------------------- Globals ----------------------- //
    private DialogHolder.SubmitForm ui = null;

    // ----------------------- Constructor ----------------------- //
    public SubmitFormDialog(Context context)
    {
        super(context);
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
        ui.etNotes      = (EditText)   ui.dialogView.findViewById(R.id.et_notes);
        ui.etSales      = (EditText)   ui.dialogView.findViewById(R.id.et_sales);
        ui.rbDone       = (RadioButton)   ui.dialogView.findViewById(R.id.rb_done);
        ui.rbFailed     = (RadioButton)   ui.dialogView.findViewById(R.id.rb_failed);
        ui.rbWaiting    = (RadioButton)   ui.dialogView.findViewById(R.id.rb_waiting);
        ui.cbCloseTask  = (CheckBox)   ui.dialogView.findViewById(R.id.cbCloseTask);
        ui.btnSubmit    = (Button)     ui.dialogView.findViewById(R.id.btn_submit);
        ui.btnCancel    = (Button)     ui.dialogView.findViewById(R.id.btn_cancel);
    }

    @Override
    protected void SetContentView()
    {
        // Get current data
        Dialog.SubmitForm currentData = (Dialog.SubmitForm) data;

        // Set Text
        ui.etNotes.setText(currentData.form.notes);
        ui.etSales.setText(String.valueOf(currentData.form.sales));

        if (currentData.form.bDone) {
            ui.rbDone.setChecked(true);
        } else if (currentData.form.bFailed) {
            ui.rbFailed.setChecked(true);
        } else if (currentData.form.bWaiting) {
            ui.rbWaiting.setChecked(true);
        }

        ui.cbCloseTask.setChecked(currentData.bCloseTask);

        // Set Listeners
        ui.btnSubmit.setOnClickListener(this);
        ui.btnCancel.setOnClickListener(this);
        ui.rbDone.setOnCheckedChangeListener(this);
        ui.rbFailed.setOnCheckedChangeListener(this);
        ui.rbWaiting.setOnCheckedChangeListener(this);
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.btn_submit:
            {
                // Get current data
                Dialog.SubmitForm currentData = (Dialog.SubmitForm) data;
                currentData.form.notes = ui.etNotes.getText().toString();
                currentData.form.sales = Integer.parseInt(ui.etSales.getText().toString());
                currentData.bCloseTask = ui.cbCloseTask.isChecked();

                // Start submit task
                SubmitFormTask submitTask = new SubmitFormTask(context, currentData.form, currentData.taskId, currentData.bCloseTask);
                submitTask.execute();
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
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        // Get current data
        Dialog.SubmitForm currentData = (Dialog.SubmitForm) data;

        // Skip for unchecked change
        if (!isChecked) {
            return;
        }

        // Reset all booleans
        switch (buttonView.getId()) {
            case R.id.rb_done : {
                currentData.form.bDone = true;
                currentData.form.bFailed = false;
                currentData.form.bWaiting = false;
                ui.cbCloseTask.setChecked(true);
                break;
            }
            case R.id.rb_waiting : {
                currentData.form.bDone = false;
                currentData.form.bFailed = false;
                currentData.form.bWaiting = true;
                break;
            }
            case R.id.rb_failed : {
                currentData.form.bDone = false;
                currentData.form.bFailed = true;
                currentData.form.bWaiting = false;
                break;
            }
        }
    }
    // ----------------------- Public APIs ----------------------- //
    // ----------------------- Private APIs ----------------------- //
}
