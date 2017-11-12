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

public class ConfirmDialog  extends     BaseDialog
                            implements  View.OnClickListener {
    // ----------------------- Constants ----------------------- //
    private static final String TAG = "CONFIRM_DIALOG";

    // ----------------------- Classes ---------------------------//
    // ----------------------- Interfaces ----------------------- //
    // ----------------------- Globals ----------------------- //
    private DialogHolder.Confirm ui = null;

    // ----------------------- Constructor ----------------------- //
    public ConfirmDialog(Context context) {
        super(context);
    }


    // ----------------------- Overrides ----------------------- //
    @Override
    protected void SetViewHolder(LayoutInflater inflater, ViewGroup container) {
        // Init Holder
        ui = new DialogHolder.Confirm();
        holder = ui;

        // Inflate View
        ui.dialogView = inflater.inflate(R.layout.dialog_confirm, container);

        // Find Views
        ui.tvMessage = (TextView) ui.dialogView.findViewById(R.id.tv_message_confirm);
        ui.btnYes = (Button) ui.dialogView.findViewById(R.id.btn_yes);
        ui.btnNo = (Button) ui.dialogView.findViewById(R.id.btn_no);
    }

    @Override
    protected void SetContentView() {
        // Get current data
        Dialog.Confirm currentData = (Dialog.Confirm) data;

        // Set Text
        ui.tvMessage.setText(currentData.message);

        // Set Listeners
        ui.btnYes.setOnClickListener(this);
        ui.btnNo.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        // Get current data
        Dialog.Confirm currentData = (Dialog.Confirm) data;

        switch (v.getId()) {
            case R.id.btn_yes: {
                // Hide this dialog box
                RlDialog.Hide();

                // Call Listener
                if (currentData.listener != null) {
                    currentData.listener.onConfirmYesClick();
                }
                break;
            }
            case R.id.btn_no: {
                // Hide this dialog box
                RlDialog.Hide();

                // Call Listener
                if (currentData.listener != null) {
                    currentData.listener.onConfirmNoClick();
                }
                break;
            }
        }
    }
// ----------------------- Public APIs ----------------------- //
// ----------------------- Private APIs ----------------------- //
}