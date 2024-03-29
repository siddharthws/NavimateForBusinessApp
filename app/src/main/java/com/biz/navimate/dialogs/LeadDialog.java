package com.biz.navimate.dialogs;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.biz.navimate.R;
import com.biz.navimate.objects.Dialog;
import com.biz.navimate.objects.FormEntry;
import com.biz.navimate.objects.templating.fieldvalues.FieldValue;
import com.biz.navimate.viewholders.DialogHolder;
import com.biz.navimate.views.RlDialog;
import com.biz.navimate.views.RlFormField;

import java.util.ArrayList;

/**
 * Created by Siddharth on 18-01-2018.
 */

public class LeadDialog extends BaseDialog implements View.OnClickListener {
    // ----------------------- Constants ----------------------- //
    private static final String TAG = "LEAD_DIALOG";

    // ----------------------- Classes ---------------------------//
    // ----------------------- Interfaces ----------------------- //
    // ----------------------- Globals ----------------------- //
    private DialogHolder.Lead ui = null;

    // ----------------------- Constructor ----------------------- //
    public LeadDialog(Context context) {
        super(context);
    }

    // ----------------------- Overrides ----------------------- //
    @Override
    protected void SetViewHolder(LayoutInflater inflater, ViewGroup container) {
        // Init Holder
        ui = new DialogHolder.Lead();
        holder = ui;

        // Inflate View
        ui.dialogView = inflater.inflate(R.layout.dialog_lead, container);

        // Find Views
        ui.tvTitle      = (TextView) ui.dialogView.findViewById(R.id.tv_lead_title);
        ui.tvAddress    = (TextView) ui.dialogView.findViewById(R.id.tv_lead_address);
        ui.llFields     = (LinearLayout)   ui.dialogView.findViewById(R.id.ll_fields_lead);
        ui.fields       = new ArrayList<>();
        ui.btnCancel    = (Button) ui.dialogView.findViewById(R.id.btn_cancel);
    }

    @Override
    protected void SetContentView() {
        // Get current data
        Dialog.Lead currentData = (Dialog.Lead) data;

        // Set Text
        ui.tvTitle.setText(currentData.lead.name);
        ui.tvAddress.setText(currentData.lead.place.address);

        // Set Form Fields
        for (FieldValue value : currentData.lead.values) {
            FormEntry.Base entry = FormEntry.Parse(value.field, value.toString());
            RlFormField fieldUi = new RlFormField(context, entry, true);
            ui.llFields.addView(fieldUi);
            ui.fields.add(fieldUi);
        }

        // Set Listeners
        ui.btnCancel.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_cancel: {
                // Hide this dialog box
                RlDialog.Hide();
                break;
            }
        }
    }
    // ----------------------- Public APIs ----------------------- //
    // ----------------------- Private APIs ----------------------- //
}
