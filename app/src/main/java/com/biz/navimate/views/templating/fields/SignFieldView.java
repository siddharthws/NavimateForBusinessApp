package com.biz.navimate.views.templating.fields;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;

import com.biz.navimate.R;
import com.biz.navimate.objects.templating.fieldvalues.FieldValue;
import com.biz.navimate.objects.templating.fieldvalues.SignFieldValue;
import com.biz.navimate.viewholders.FieldViewHolder;
import com.biz.navimate.views.compound.SignEditorView;

public class SignFieldView extends FieldView {
    // ----------------------- Constants ----------------------- //
    private static final String TAG = "SIGN_FIELD_VIEW";

    // ----------------------- Classes ---------------------------//
    // ----------------------- Interfaces ----------------------- //
    // ----------------------- Globals ----------------------- //
    // ----------------------- Constructor ----------------------- //
    // For creating view programatically
    public SignFieldView(Context ctx, ViewGroup root, FieldValue value) {
        super(ctx, null, root, R.layout.field_view_sign, value, new FieldViewHolder.Sign());
    }

    // For adding view through XML
    public SignFieldView(Context ctx, AttributeSet attrs) {
        super(ctx, attrs, null, R.layout.field_view_sign, null, new FieldViewHolder.Sign());
    }

    // ----------------------- Abstracts ----------------------- //
    // ----------------------- Overrides ----------------------- //
    @Override
    protected void FindViews() {
        // Get UI
        FieldViewHolder.Sign ui = (FieldViewHolder.Sign) holder;

        // Find views
        ui.seEditor = (SignEditorView) findViewById(R.id.se_sign_field);
    }

    @Override
    protected void SetViews() {
        // Ignore if value is not set
        if (value == null) { return; }

        // Get UI and value
        FieldViewHolder.Sign ui = (FieldViewHolder.Sign) holder;
        SignFieldValue val = (SignFieldValue) value;

        // Set value
        ui.seEditor.Set(val.filename);
    }

    @Override
    protected void ResetEditability() {
        FieldViewHolder.Sign ui = (FieldViewHolder.Sign) holder;

        ui.seEditor.SetEditable(bEditable);
    }

    @Override
    protected void RefreshValue() {
        FieldViewHolder.Sign ui = (FieldViewHolder.Sign) holder;
        SignFieldValue val = (SignFieldValue) value;

        val.filename = ui.seEditor.Get();
    }

    // ----------------------- Public methods ----------------------- //
    // ----------------------- Private methods ----------------------- //
    // ----------------------- Static methods ----------------------- //
}
