package com.biz.navimate.views.templating.fields;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;

import com.biz.navimate.R;
import com.biz.navimate.objects.templating.fieldvalues.FieldValue;
import com.biz.navimate.objects.templating.fieldvalues.ObjectFieldValue;
import com.biz.navimate.viewholders.FieldViewHolder;
import com.biz.navimate.views.compound.ObjectPickerView;

public class ObjFieldView extends FieldView {
    // ----------------------- Constants ----------------------- //
    private static final String TAG = "DATE_FIELD_VIEW";

    // ----------------------- Classes ---------------------------//
    // ----------------------- Interfaces ----------------------- //
    // ----------------------- Globals ----------------------- //
    // ----------------------- Constructor ----------------------- //
    // For creating view programatically
    public ObjFieldView(Context ctx, ViewGroup root, FieldValue value) {
        super(ctx, null, root, R.layout.field_view_object, value, new FieldViewHolder.Object());
    }

    // For adding view through XML
    public ObjFieldView(Context ctx, AttributeSet attrs) {
        super(ctx, attrs, null, R.layout.field_view_object, null, new FieldViewHolder.Object());
    }

    // ----------------------- Abstracts ----------------------- //
    // ----------------------- Overrides ----------------------- //
    @Override
    protected void FindViews() {
        // Get UI
        FieldViewHolder.Object ui = (FieldViewHolder.Object) holder;

        // Find views
        ui.objPicker = (ObjectPickerView) findViewById(R.id.obj_picker_field);
    }

    @Override
    protected void SetViews() {
        // Ignore if value is not set
        if (value == null) { return; }

        // Get UI and value
        FieldViewHolder.Object ui = (FieldViewHolder.Object) holder;
        ObjectFieldValue val = (ObjectFieldValue) value;

        // Set Object Picker type depending on field type
        switch (val.field.type) {
            // Placeholder
        }

        // Set text value
        ui.objPicker.Set(val.obj);
    }

    @Override
    protected void ResetEditability() {
        FieldViewHolder.Object ui = (FieldViewHolder.Object) holder;

        ui.objPicker.SetEditable(bEditable);
    }

    @Override
    protected void RefreshValue() {
        FieldViewHolder.Object ui = (FieldViewHolder.Object) holder;
        ObjectFieldValue val = (ObjectFieldValue) value;

        val.obj = ui.objPicker.Get();
    }

    // ----------------------- Public methods ----------------------- //
    // ----------------------- Private methods ----------------------- //
    // ----------------------- Static methods ----------------------- //
}
