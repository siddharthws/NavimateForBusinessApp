package com.biz.navimate.views.templating.fields;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.biz.navimate.R;
import com.biz.navimate.objects.templating.fieldvalues.ChecklistFieldValue;
import com.biz.navimate.objects.templating.fieldvalues.FieldValue;
import com.biz.navimate.viewholders.FieldViewHolder;
import com.biz.navimate.views.CbCustom;

import java.util.ArrayList;

public class ChecklistFieldView extends FieldView {
    // ----------------------- Constants ----------------------- //
    private static final String TAG = "CHECKLIST_FIELD_VIEW";

    // ----------------------- Classes ---------------------------//
    // ----------------------- Interfaces ----------------------- //
    // ----------------------- Globals ----------------------- //
    // ----------------------- Constructor ----------------------- //
    // For creating view programatically
    public ChecklistFieldView(Context ctx, ViewGroup root, FieldValue value) {
        super(ctx, null, root, R.layout.field_view_checklist, value, new FieldViewHolder.Checklist());
    }

    // For adding view through XML
    public ChecklistFieldView(Context ctx, AttributeSet attrs) {
        super(ctx, attrs, null, R.layout.field_view_checklist, null, new FieldViewHolder.Checklist());
    }

    // ----------------------- Abstracts ----------------------- //
    // ----------------------- Overrides ----------------------- //
    @Override
    protected void FindViews() {
        // Get UI
        FieldViewHolder.Checklist ui = (FieldViewHolder.Checklist) holder;

        // Find views
        ui.llChecklist = (LinearLayout) findViewById(R.id.ll_checkList);
        ui.cbList = new ArrayList<>();
    }

    @Override
    protected void SetViews() {
        // Ignore if value is not set
        if (value == null) { return; }

        // Get UI and value
        FieldViewHolder.Checklist ui = (FieldViewHolder.Checklist) holder;
        ChecklistFieldValue val = (ChecklistFieldValue) value;

        // Add checkbox for every option
        for (int i = 0; i < val.options.size(); i++) {
            CbCustom cb = new CbCustom(ctx);
            cb.setText(val.options.get(i));

            // Add to ui & cache
            ui.llChecklist.addView(cb);
            ui.cbList.add(cb);

            // Set checked depending on data
            cb.setChecked(val.selection.get(i));
        }
    }

    @Override
    protected void ResetEditability() {
        FieldViewHolder.Checklist ui = (FieldViewHolder.Checklist) holder;

        for (int i = 0; i < ui.cbList.size(); i++) {
            ui.cbList.get(i).setEnabled(bEditable);
        }
    }

    @Override
    protected void RefreshValue() {
        // Get UI and value
        FieldViewHolder.Checklist ui = (FieldViewHolder.Checklist) holder;
        ChecklistFieldValue val = (ChecklistFieldValue) value;

        // Set selection based on checkboxes
        for (int i = 0; i < ui.cbList.size(); i++) {
            val.selection.set(i, ui.cbList.get(i).isSelected());
        }
    }

    // ----------------------- Public methods ----------------------- //
    // ----------------------- Private methods ----------------------- //
    // ----------------------- Static methods ----------------------- //
}
