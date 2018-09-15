package com.biz.navimate.views.templating.fields;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;

import com.biz.navimate.R;
import com.biz.navimate.objects.templating.fieldvalues.FieldValue;
import com.biz.navimate.objects.templating.fieldvalues.PhotoFieldValue;
import com.biz.navimate.viewholders.FieldViewHolder;
import com.biz.navimate.views.compound.PhotoEditorView;

public class PhotoFieldView extends FieldView {
    // ----------------------- Constants ----------------------- //
    private static final String TAG = "PHOTO_FIELD_VIEW";

    // ----------------------- Classes ---------------------------//
    // ----------------------- Interfaces ----------------------- //
    // ----------------------- Globals ----------------------- //
    // ----------------------- Constructor ----------------------- //
    // For creating view programatically
    public PhotoFieldView(Context ctx, ViewGroup root, FieldValue value) {
        super(ctx, null, root, R.layout.field_view_photo, value, new FieldViewHolder.Photo());
    }

    // For adding view through XML
    public PhotoFieldView(Context ctx, AttributeSet attrs) {
        super(ctx, attrs, null, R.layout.field_view_photo, null, new FieldViewHolder.Photo());
    }

    // ----------------------- Abstracts ----------------------- //
    // ----------------------- Overrides ----------------------- //
    @Override
    protected void FindViews() {
        // Get UI
        FieldViewHolder.Photo ui = (FieldViewHolder.Photo) holder;

        // Find views
        ui.peEditor = (PhotoEditorView) findViewById(R.id.pe_photo_field);
    }

    @Override
    protected void SetViews() {
        // Ignore if value is not set
        if (value == null) { return; }

        // Get UI and value
        FieldViewHolder.Photo ui = (FieldViewHolder.Photo) holder;
        PhotoFieldValue val = (PhotoFieldValue) value;

        // Set value
        ui.peEditor.Set(val.filename);
    }

    @Override
    protected void ResetEditability() {
        FieldViewHolder.Photo ui = (FieldViewHolder.Photo) holder;

        ui.peEditor.SetEditable(bEditable);
    }

    @Override
    protected void RefreshValue() {
        FieldViewHolder.Photo ui = (FieldViewHolder.Photo) holder;
        PhotoFieldValue val = (PhotoFieldValue) value;

        val.filename = ui.peEditor.Get();
    }

    // ----------------------- Public methods ----------------------- //
    // ----------------------- Private methods ----------------------- //
    // ----------------------- Static methods ----------------------- //
}
