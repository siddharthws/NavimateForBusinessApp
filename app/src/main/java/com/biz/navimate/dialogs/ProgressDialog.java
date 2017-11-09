package com.biz.navimate.dialogs;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.biz.navimate.R;
import com.biz.navimate.objects.Dialog;
import com.biz.navimate.viewholders.DialogHolder;
import com.biz.navimate.views.TvCalibri;

/**
 * Created by Siddharth on 09-11-2017.
 */

public class ProgressDialog extends BaseDialog {
    // ----------------------- Constants ----------------------- //
    private static final String TAG = "PROGRESS_DIALOG";

    // ----------------------- Classes ---------------------------//
    // ----------------------- Interfaces ----------------------- //
    // ----------------------- Globals ----------------------- //
    private DialogHolder.Progress ui = null;

    // ----------------------- Constructor ----------------------- //
    public ProgressDialog(Context context)
    {
        super(context);
    }

    // ----------------------- Overrides ----------------------- //
    @Override
    protected void SetViewHolder(LayoutInflater inflater, ViewGroup container)
    {
        // Init Holder
        ui = new DialogHolder.Progress();
        holder = ui;

        // Inflate View
        ui.dialogView = inflater.inflate(R.layout.dialog_progress, container);

        // Find Views
        ui.tvMessage    = (TvCalibri)   ui.dialogView.findViewById(R.id.tv_message);
        ui.tvProgress   = (TvCalibri)   ui.dialogView.findViewById(R.id.tv_progress);
        ui.pbProgress   = (ProgressBar) ui.dialogView.findViewById(R.id.pb_progress);
    }

    @Override
    protected void SetContentView()
    {
        // Get current data
        Dialog.Progress currentData = (Dialog.Progress) data;

        // Set Text
        ui.tvMessage.setText(currentData.message);
        ui.tvProgress.setText(String.valueOf(currentData.progress));

        // Set Progress properties
        ui.pbProgress.setProgress(currentData.progress);
        ui.pbProgress.setMax(100);
    }

    // ----------------------- Public APIs ----------------------- //
    // ----------------------- Private APIs ----------------------- //
}
