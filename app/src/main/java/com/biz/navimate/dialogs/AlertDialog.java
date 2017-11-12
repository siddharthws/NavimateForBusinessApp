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
 * Created by Siddharth on 27-09-2017.
 */

public class AlertDialog    extends     BaseDialog
                            implements  View.OnClickListener
{
    // ----------------------- Constants ----------------------- //
    private static final String TAG = "ALERT_DIALOG";

    // ----------------------- Classes ---------------------------//
    // ----------------------- Interfaces ----------------------- //
    // ----------------------- Globals ----------------------- //
    private DialogHolder.Alert ui = null;

    // ----------------------- Constructor ----------------------- //
    public AlertDialog(Context context)
    {
        super(context);
    }


    // ----------------------- Overrides ----------------------- //
    @Override
    protected void SetViewHolder(LayoutInflater inflater, ViewGroup container)
    {
        // Init Holder
        ui = new DialogHolder.Alert();
        holder = ui;

        // Inflate View
        ui.dialogView = inflater.inflate(R.layout.dialog_alert, container);

        // Find Views
        ui.tvMessage = (TextView)   ui.dialogView.findViewById(R.id.tv_message_alert);
        ui.btn       = (Button)     ui.dialogView.findViewById(R.id.btn_alert);
    }

    @Override
    protected void SetContentView()
    {
        // Get current data
        Dialog.Alert currentData = (Dialog.Alert) data;

        // Set Text
        ui.tvMessage.setText(currentData.message);

        // Set Listeners
        ui.btn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.btn_alert:
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
