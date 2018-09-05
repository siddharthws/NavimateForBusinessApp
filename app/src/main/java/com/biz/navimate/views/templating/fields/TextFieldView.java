package com.biz.navimate.views.templating.fields;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.biz.navimate.R;
import com.biz.navimate.objects.templating.fieldvalues.FieldValue;
import com.biz.navimate.objects.templating.fieldvalues.TextFieldValue;
import com.biz.navimate.viewholders.FieldViewHolder;
import com.biz.navimate.views.compound.EtClearable;
import com.biz.navimate.views.custom.NvmEditText;

public class TextFieldView  extends     FieldView
                            implements  NvmEditText.IfaceEditText {
    // ----------------------- Constants ----------------------- //
    private static final String TAG = "TEXT_FIELD_VIEW";

    // ----------------------- Classes ---------------------------//
    // ----------------------- Interfaces ----------------------- //
    public interface IfaceTextField {
        void onTextFieldChanged(String text);
        void onTextFieldChangedDebounced(String text);
    }
    private IfaceTextField listener = null;
    public void SetListener(IfaceTextField listener) { this.listener = listener; }

    // ----------------------- Globals ----------------------- //
    // ----------------------- Constructor ----------------------- //
    // For creating view programatically
    public TextFieldView(Context ctx, ViewGroup root, FieldValue value) {
        super(ctx, null, root, R.layout.field_view_text, value, new FieldViewHolder.Text());
    }

    // For adding view through XML
    public TextFieldView(Context ctx, AttributeSet attrs) {
        super(ctx, attrs, null, R.layout.field_view_text, null, new FieldViewHolder.Text());
    }

    // ----------------------- Abstracts ----------------------- //
    // ----------------------- Overrides ----------------------- //
    @Override
    protected void FindViews() {
        // Get UI
        FieldViewHolder.Text ui = (FieldViewHolder.Text) holder;

        // Find views
        ui.etcText  = (EtClearable) findViewById(R.id.etc_text);
        ui.tvText   = (TextView) findViewById(R.id.tv_text);
    }

    @Override
    protected void SetViews() {
        // Ignore if value is not set
        if (value == null) { return; }

        // Get UI and value
        FieldViewHolder.Text ui = (FieldViewHolder.Text) holder;
        TextFieldValue val = (TextFieldValue) value;

        // Set text value
        ui.etcText.SetText(val.text);
        ui.tvText.setText(val.text);

        // Set listener
        ui.etcText.SetListener(this);
    }

    @Override
    protected void ResetEditability() {
        FieldViewHolder.Text ui = (FieldViewHolder.Text) holder;

        if (bEditable) {
            ui.tvText.setVisibility(View.GONE);
            ui.etcText.setVisibility(View.VISIBLE);
        } else {
            ui.etcText.setVisibility(View.GONE);
            ui.tvText.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void RefreshValue() {
        // Get UI and value
        FieldViewHolder.Text ui = (FieldViewHolder.Text) holder;
        TextFieldValue val = (TextFieldValue) value;

        // Set text value
        val.text = ui.etcText.GetText().toString();
    }

    @Override
    public void onTextChanged(String text) {
        // Update Text View
        FieldViewHolder.Text ui = (FieldViewHolder.Text) holder;
        ui.tvText.setText(text);

        // Trigger listener
        if (listener != null) {
            listener.onTextFieldChanged(text);
        }
    }

    @Override
    public void onTextChangedDebounced(String text) {
        // Trigger listener
        if (listener != null) {
            listener.onTextFieldChangedDebounced(text);
        }
    }

    // ----------------------- Public methods ----------------------- //
    // ----------------------- Private methods ----------------------- //
    // ----------------------- Static methods ----------------------- //
}
