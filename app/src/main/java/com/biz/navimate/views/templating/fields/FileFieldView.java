package com.biz.navimate.views.templating.fields;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;

import com.biz.navimate.R;
import com.biz.navimate.objects.templating.fieldvalues.FieldValue;
import com.biz.navimate.objects.templating.fieldvalues.FileFieldValue;
import com.biz.navimate.viewholders.FieldViewHolder;
import com.biz.navimate.views.compound.FileEditorView;

public class FileFieldView extends FieldView {
    // ----------------------- Constants ----------------------- //
    private static final String TAG = "FILE_FIELD_VIEW";

    // ----------------------- Classes ---------------------------//
    // ----------------------- Interfaces ----------------------- //
    // ----------------------- Globals ----------------------- //
    // ----------------------- Constructor ----------------------- //
    // For creating view programatically
    public FileFieldView(Context ctx, ViewGroup root, FieldValue value) {
        super(ctx, null, root, R.layout.field_view_file, value, new FieldViewHolder.File());
    }

    // For adding view through XML
    public FileFieldView(Context ctx, AttributeSet attrs) {
        super(ctx, attrs, null, R.layout.field_view_file, null, new FieldViewHolder.File());
    }

    // ----------------------- Abstracts ----------------------- //
    // ----------------------- Overrides ----------------------- //
    @Override
    protected void FindViews() {
        // Get UI
        FieldViewHolder.File ui = (FieldViewHolder.File) holder;

        // Find views
        ui.feEditor = (FileEditorView) findViewById(R.id.fe_file_field);
    }

    @Override
    protected void SetViews() {
        // Ignore if value is not set
        if (value == null) { return; }

        // Get UI and value
        FieldViewHolder.File ui = (FieldViewHolder.File) holder;
        FileFieldValue val = (FileFieldValue) value;

        // Set value
        ui.feEditor.Set(val.filename);
    }

    @Override
    protected void ResetEditability() {
        FieldViewHolder.File ui = (FieldViewHolder.File) holder;

        ui.feEditor.SetEditable(bEditable);
    }

    @Override
    protected void RefreshValue() {
        FieldViewHolder.File ui = (FieldViewHolder.File) holder;
        FileFieldValue val = (FileFieldValue) value;

        val.filename = ui.feEditor.Get();
    }

    // ----------------------- Public methods ----------------------- //
    // ----------------------- Private methods ----------------------- //
    // ----------------------- Static methods ----------------------- //
}
