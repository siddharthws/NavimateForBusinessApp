package com.biz.navimate.dialogs;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import com.biz.navimate.R;
import com.biz.navimate.objects.Dialog;
import com.biz.navimate.viewholders.DialogHolder;

/**
 * Created by Siddharth on 27-09-2017.
 */

public class WaitingDialog extends BaseDialog
{
    // ----------------------- Constants ----------------------- //
    private static final String TAG = "WAITING_DIALOG";

    // ----------------------- Classes ---------------------------//
    // ----------------------- Interfaces ----------------------- //
    // ----------------------- Globals ----------------------- //
    private DialogHolder.Waiting ui = null;

    // ----------------------- Constructor ----------------------- //
    public WaitingDialog(Context context)
    {
        super(context);
    }

    // ----------------------- Overrides ----------------------- //
    @Override
    protected void SetViewHolder(LayoutInflater inflater, ViewGroup container)
    {
        // Init Holder
        ui = new DialogHolder.Waiting();
        holder = ui;

        // Inflate View
        ui.dialogView = inflater.inflate(R.layout.dialog_waiting, container);

        // Find Views
        ui.tvDialogMessage = (TextView) ui.dialogView.findViewById(R.id.tv_message);
    }

    @Override
    protected void SetContentView()
    {
        // Get current data
        Dialog.Waiting currentData = (Dialog.Waiting) data;

        // Set Data
        ui.tvDialogMessage.setText(currentData.message);
    }

    // ----------------------- Public APIs ----------------------- //
    // ----------------------- Private APIs ----------------------- //
}
