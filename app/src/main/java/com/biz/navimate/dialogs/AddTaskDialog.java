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
        ui.btnSave   = (Button)     ui.dialogView.findViewById(R.id.btn_save);
        ui.btnCancel = (Button)     ui.dialogView.findViewById(R.id.btn_cancel);
    }

    @Override
    protected void SetContentView()
    {
        // Set Listeners
        ui.btnSave.setOnClickListener(this);
        ui.btnCancel.setOnClickListener(this);
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

