package com.biz.navimate.views.templating.fields;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;

import com.biz.navimate.R;
import com.biz.navimate.objects.templating.fieldvalues.DateFieldValue;
import com.biz.navimate.objects.templating.fieldvalues.FieldValue;
import com.biz.navimate.viewholders.FieldViewHolder;
import com.biz.navimate.views.compound.DateEditorView;

public class DateFieldView extends FieldView {
    // ----------------------- Constants ----------------------- //
    private static final String TAG = "DATE_FIELD_VIEW";

    // ----------------------- Classes ---------------------------//
    // ----------------------- Interfaces ----------------------- //
    // ----------------------- Globals ----------------------- //
    // ----------------------- Constructor ----------------------- //
    // For creating view programatically
    public DateFieldView(Context ctx, ViewGroup root, FieldValue value) {
        super(ctx, null, root, R.layout.field_view_date, value, new FieldViewHolder.Date());
    }

    // For adding view through XML
    public DateFieldView(Context ctx, AttributeSet attrs) {
        super(ctx, attrs, null, R.layout.field_view_date, null, new FieldViewHolder.Date());
    }

    // ----------------------- Abstracts ----------------------- //
    // ----------------------- Overrides ----------------------- //
    @Override
    protected void FindViews() {
        // Get UI
        FieldViewHolder.Date ui = (FieldViewHolder.Date) holder;

        // Find views
        ui.dtEditor = (DateEditorView) findViewById(R.id.dt_date_field);
    }

    @Override
    protected void SetViews() {
        // Ignore if value is not set
        if (value == null) { return; }

        // Get UI and value
        FieldViewHolder.Date ui = (FieldViewHolder.Date) holder;
        DateFieldValue val = (DateFieldValue) value;

        // Set text value
        ui.dtEditor.Set(val.cal);
    }

    @Override
    protected void ResetEditability() {
        FieldViewHolder.Date ui = (FieldViewHolder.Date) holder;

        ui.dtEditor.SetEditable(bEditable);
    }

    @Override
    protected void RefreshValue() {
        FieldViewHolder.Date ui = (FieldViewHolder.Date) holder;
        DateFieldValue val = (DateFieldValue) value;

        val.cal = ui.dtEditor.Get();
    }

    // ----------------------- Public methods ----------------------- //
    // ----------------------- Private methods ----------------------- //
    // ----------------------- Static methods ----------------------- //
}
