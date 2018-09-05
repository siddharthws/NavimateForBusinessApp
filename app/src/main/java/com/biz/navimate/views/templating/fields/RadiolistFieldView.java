package com.biz.navimate.views.templating.fields;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;

import com.biz.navimate.R;
import com.biz.navimate.objects.templating.fieldvalues.FieldValue;
import com.biz.navimate.objects.templating.fieldvalues.RadiolistFieldValue;
import com.biz.navimate.viewholders.FieldViewHolder;
import com.biz.navimate.views.custom.Dropdown;

public class RadiolistFieldView extends FieldView {
    // ----------------------- Constants ----------------------- //
    private static final String TAG = "RADIOLIST_FIELD_VIEW";

    // ----------------------- Classes ---------------------------//
    // ----------------------- Interfaces ----------------------- //
    // ----------------------- Globals ----------------------- //
    // ----------------------- Constructor ----------------------- //
    // For creating view programatically
    public RadiolistFieldView(Context ctx, ViewGroup root, FieldValue value) {
        super(ctx, null, root, R.layout.field_view_radiolist, value, new FieldViewHolder.Radiolist());
    }

    // For adding view through XML
    public RadiolistFieldView(Context ctx, AttributeSet attrs) {
        super(ctx, attrs, null, R.layout.field_view_radiolist, null, new FieldViewHolder.Radiolist());
    }

    // ----------------------- Abstracts ----------------------- //
    // ----------------------- Overrides ----------------------- //
    @Override
    protected void FindViews() {
        // Get UI
        FieldViewHolder.Radiolist ui = (FieldViewHolder.Radiolist) holder;

        // Find views
        ui.ddRadiolist = (Dropdown) findViewById(R.id.dd_radioList);
    }

    @Override
    protected void SetViews() {
        // Ignore if value is not set
        if (value == null) { return; }

        // Get UI and value
        FieldViewHolder.Radiolist ui = (FieldViewHolder.Radiolist) holder;
        RadiolistFieldValue val = (RadiolistFieldValue) value;

        // Set UI properties
        ui.ddRadiolist.SetList(val.options);
        ui.ddRadiolist.SetSelectionNoCallback(val.selection);
    }

    @Override
    protected void ResetEditability() {
        FieldViewHolder.Radiolist ui = (FieldViewHolder.Radiolist) holder;

        ui.ddRadiolist.setEnabled(bEditable);
    }

    @Override
    protected void RefreshValue() {
        // Get UI and value
        FieldViewHolder.Radiolist ui = (FieldViewHolder.Radiolist) holder;
        RadiolistFieldValue val = (RadiolistFieldValue) value;

        // get current selection
        val.selection = ui.ddRadiolist.getSelectedItemPosition();
    }

    // ----------------------- Public methods ----------------------- //
    // ----------------------- Private methods ----------------------- //
    // ----------------------- Static methods ----------------------- //
}
