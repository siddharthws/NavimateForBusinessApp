package com.biz.navimate.views.templating.fields;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.biz.navimate.constants.Constants;
import com.biz.navimate.objects.templating.fieldvalues.FieldValue;
import com.biz.navimate.viewholders.FieldViewHolder;
import com.biz.navimate.views.compound.LabelBox;

public abstract class FieldView extends LabelBox {
    // ----------------------- Constants ----------------------- //
    private static final String TAG = "FIELD_VIEW";

    // ----------------------- Interfaces ----------------------- //
    // ----------------------- Globals ----------------------- //
    protected Context ctx = null;
    protected ViewGroup root = null;
    protected int layoutId = 0;
    protected FieldValue value = null;
    protected FieldViewHolder.Base holder = null;

    // Flags
    protected boolean bEditable = true;

    // ----------------------- Abstracts ----------------------- //
    // Method to find individual views in UI
    protected abstract void FindViews();

    // Method to set individual field view properties
    protected abstract void SetViews();

    // Method to refresh the value object as per current UI
    protected abstract void RefreshValue();

    // Method to set editability of the view
    protected abstract void ResetEditability();

    // ----------------------- Constructors ----------------------- //
    public FieldView(Context ctx,
                     AttributeSet attrs,
                     ViewGroup root,
                     int layoutId,
                     FieldValue value,
                     FieldViewHolder.Base holder) {
        super(ctx, attrs);

        // Assign properties
        this.ctx = ctx;
        this.root = root;
        this.layoutId = layoutId;
        this.holder = holder;
        this.value = value;

        // Initialize Layout
        Init();
    }

    // ----------------------- Overrides ----------------------- //
    // ----------------------- Public APIs ----------------------- //
    // Method to set & get the current value
    public void SetValue(FieldValue value) {
        // Set cache
        this.value = value;

        // Set label
        if (value != null) {
            SetLabel(value.field.title);
        }

        // Set Child View properties
        SetViews();
    }

    public FieldValue GetValue() {
        // Refresh value
        RefreshValue();

        // Return updated value
        return value;
    }

    // Method to update view editability
    public void SetEditable(boolean bEditable) {
        // Set edit flag
        this.bEditable = bEditable;

        // Reset view editability
        ResetEditability();
    }

    // Method to validate entered data as per field settings
    public boolean Validate() {
        // Refresh value
        RefreshValue();

        // Get error message from value
        String errMsg = value.getError();
        boolean bErr = errMsg.length() > 0;

        // Show / hide error
        if (bErr) {
            ShowError(errMsg);
        } else {
            HideError();
        }

        // Return error flag
        return bErr;
    }

    // ----------------------- Private APIs ----------------------- //
    // Methods to inflate and initialize the view
    private void Init() {
        // Inflate field Layout
        LayoutInflater inflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(layoutId, this, true);

        // Add to root
        if (root != null) {
            root.addView(this);
        }

        // Find child views
        FindViews();

        // Init editability
        SetEditable(bEditable);

        // Set current value
        SetValue(value);
    }

    // ----------------------- Static Methods ----------------------- //
    // Static method to get a new instance based on field values
    public static FieldView newInstance(Context ctx,
                                        ViewGroup root,
                                        FieldValue value) {
        switch (value.field.type) {
            case Constants.Template.FIELD_TYPE_TEXT:
                return new TextFieldView(ctx, root, value);
            case Constants.Template.FIELD_TYPE_NUMBER:
                return new NumberFieldView(ctx, root, value);
        }

        return null;
    }
}
