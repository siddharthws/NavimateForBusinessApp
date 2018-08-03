package com.biz.navimate.dialogs;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.biz.navimate.R;
import com.biz.navimate.debug.Dbg;
import com.biz.navimate.objects.Dialog;
import com.biz.navimate.objects.FormEntry;
import com.biz.navimate.viewholders.DialogHolder;
import com.biz.navimate.views.RlDialog;
import com.biz.navimate.views.RlFormField;
import com.biz.navimate.views.TvCalibri;

import java.util.ArrayList;

public class ProductViewerDialog extends BaseDialog implements View.OnClickListener {
    // ----------------------- Constants ----------------------- //
    private static final String TAG = "PRODUCT_VIEWER_DIALOG";

    // ----------------------- Classes ---------------------------//
    // ----------------------- Interfaces ----------------------- //
    // ----------------------- Globals ----------------------- //
    private DialogHolder.ProductViewer ui = null;

    // ----------------------- Constructor ----------------------- //
    public ProductViewerDialog(Context context) {
        super(context);
    }

    // ----------------------- Overrides ----------------------- //
    @Override
    protected void SetViewHolder(LayoutInflater inflater, ViewGroup container) {
        // Init Holder
        ui = new DialogHolder.ProductViewer();
        holder = ui;

        // Inflate View
        ui.dialogView = inflater.inflate(R.layout.dialog_product_viewer, container);

        // Find Views
        ui.tvName       = (TvCalibri) ui.dialogView.findViewById(R.id.tv_product_name);
        ui.tvProductId  = (TvCalibri) ui.dialogView.findViewById(R.id.tv_product_id);
        ui.llFields     = (LinearLayout)   ui.dialogView.findViewById(R.id.ll_fields_pv);
        ui.fields       = new ArrayList<>();
        ui.btnClose     = (Button) ui.dialogView.findViewById(R.id.btn_close);
    }

    @Override
    protected void SetContentView() {
        // Get current data
        Dialog.ProductViewer currentData = (Dialog.ProductViewer) data;

        // Set Text
        ui.tvName.setText(currentData.product.name);
        ui.tvProductId.setText(currentData.product.productId);

        // Set Form Fields
        for (FormEntry.Base value : currentData.product.values) {
            RlFormField fieldUi = new RlFormField(context, value, true);
            ui.llFields.addView(fieldUi);
            ui.fields.add(fieldUi);
        }

        // Set Listeners
        ui.btnClose.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_close: {
                // Hide this dialog box
                RlDialog.Hide();
                break;
            }
        }
    }
    // ----------------------- Public APIs ----------------------- //
    // ----------------------- Private APIs ----------------------- //
}
