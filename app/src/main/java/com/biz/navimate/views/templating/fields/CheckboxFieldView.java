package com.biz.navimate.views.templating.fields;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;

import com.biz.navimate.R;
import com.biz.navimate.objects.templating.fieldvalues.CheckboxFieldValue;
import com.biz.navimate.objects.templating.fieldvalues.FieldValue;
import com.biz.navimate.viewholders.FieldViewHolder;
import com.biz.navimate.views.CbCustom;

public class CheckboxFieldView extends FieldView {
    // ----------------------- Constants ----------------------- //
    private static final String TAG = "CHECKBOX_FIELD_VIEW";

    // ----------------------- Classes ---------------------------//
    // ----------------------- Interfaces ----------------------- //
    // ----------------------- Globals ----------------------- //
    // ----------------------- Constructor ----------------------- //
    // For creating view programatically
    public CheckboxFieldView(Context ctx, ViewGroup root, FieldValue value) {
        super(ctx, null, root, R.layout.field_view_checkbox, value, new FieldViewHolder.Checkbox());
    }

    // For adding view through XML
    public CheckboxFieldView(Context ctx, AttributeSet attrs) {
        super(ctx, attrs, null, R.layout.field_view_checkbox, null, new FieldViewHolder.Checkbox());
    }

    // ----------------------- Abstracts ----------------------- //
    // ----------------------- Overrides ----------------------- //
    @Override
    protected void FindViews() {
        // Get UI
        FieldViewHolder.Checkbox ui = (FieldViewHolder.Checkbox) holder;

        // Find views
        ui.cbCheckbox = (CbCustom) findViewById(R.id.cb_checkbox);
    }

    @Override
    protected void SetViews() {
        // Ignore if value is not set
        if (value == null) { return; }

        // Get UI and value
        FieldViewHolder.Checkbox ui = (FieldViewHolder.Checkbox) holder;
        CheckboxFieldValue val = (CheckboxFieldValue) value;

        // Set text value
        ui.cbCheckbox.setSelected(val.bChecked);
    }

    @Override
    protected void ResetEditability() {
        FieldViewHolder.Checkbox ui = (FieldViewHolder.Checkbox) holder;

        ui.cbCheckbox.setEnabled(bEditable);
    }

    @Override
    protected void RefreshValue() {
        FieldViewHolder.Checkbox ui = (FieldViewHolder.Checkbox) holder;
        CheckboxFieldValue val = (CheckboxFieldValue) value;

        // Set text value
        val.bChecked = ui.cbCheckbox.isSelected();
    }

    // ----------------------- Public methods ----------------------- //
    // ----------------------- Private methods ----------------------- //
    // ----------------------- Static methods ----------------------- //
}
