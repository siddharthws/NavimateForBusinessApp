package com.biz.navimate.views.templating.fields;

import android.content.Context;
import android.text.InputType;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.biz.navimate.R;
import com.biz.navimate.objects.templating.fieldvalues.FieldValue;
import com.biz.navimate.objects.templating.fieldvalues.NumberFieldValue;
import com.biz.navimate.viewholders.FieldViewHolder;
import com.biz.navimate.views.compound.EtClearable;
import com.biz.navimate.views.custom.NvmEditText;

public class NumberFieldView    extends     FieldView
                                implements  NvmEditText.IfaceEditText {
    // ----------------------- Constants ----------------------- //
    private static final String TAG = "NUMBER_FIELD_VIEW";

    // ----------------------- Classes ---------------------------//
    // ----------------------- Interfaces ----------------------- //
    public interface IfaceNumberField {
        void onNumberFieldChanged(String text);
        void onNumberFieldChangedDebounced(String text);
    }
    private IfaceNumberField listener = null;
    public void SetListener(IfaceNumberField listener) { this.listener = listener; }

    // ----------------------- Globals ----------------------- //
    // ----------------------- Constructor ----------------------- //
    // For creating view programatically
    public NumberFieldView(Context ctx, ViewGroup root, FieldValue value) {
        super(ctx, null, root, R.layout.field_view_number, value, new FieldViewHolder.Number());
    }

    // For adding view through XML
    public NumberFieldView(Context ctx, AttributeSet attrs) {
        super(ctx, attrs, null, R.layout.field_view_number, null, new FieldViewHolder.Number());
    }

    // ----------------------- Abstracts ----------------------- //
    // ----------------------- Overrides ----------------------- //
    @Override
    protected void FindViews() {
        // Get UI
        FieldViewHolder.Number ui = (FieldViewHolder.Number) holder;

        // Find views
        ui.etcNumber  = (EtClearable) findViewById(R.id.etc_number);
        ui.tvNumber   = (TextView) findViewById(R.id.tv_number);
    }

    @Override
    protected void SetViews() {
        // Ignore if value is not set
        if (value == null) { return; }

        // Get UI and value
        FieldViewHolder.Number ui = (FieldViewHolder.Number) holder;
        NumberFieldValue val = (NumberFieldValue) value;

        // Set edittext to type number
        ui.etcNumber.SetInputType(InputType.TYPE_CLASS_NUMBER);

        // Set text value
        ui.etcNumber.SetText(String.valueOf(val.number));
        ui.tvNumber.setText(String.valueOf(val.number));

        // Set listener
        ui.etcNumber.SetListener(this);
    }

    @Override
    protected void ResetEditability() {
        FieldViewHolder.Number ui = (FieldViewHolder.Number) holder;

        if (bEditable) {
            ui.tvNumber.setVisibility(View.GONE);
            ui.etcNumber.setVisibility(View.VISIBLE);
        } else {
            ui.etcNumber.setVisibility(View.GONE);
            ui.tvNumber.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void RefreshValue() {
        // Get UI and value
        FieldViewHolder.Number ui = (FieldViewHolder.Number) holder;
        NumberFieldValue val = (NumberFieldValue) value;

        // Set value
        String numText = ui.etcNumber.GetText().toString();
        if (numText == null || numText.length() == 0) {
            numText = "0";
        }
        val.number = Double.parseDouble(numText);
    }

    @Override
    public void onTextChanged(String text) {
        // Update Text View
        FieldViewHolder.Text ui = (FieldViewHolder.Text) holder;
        ui.tvText.setText(text);

        // Trigger listener
        if (listener != null) {
            listener.onNumberFieldChanged(text);
        }
    }

    @Override
    public void onTextChangedDebounced(String text) {
        // Trigger listener
        if (listener != null) {
            listener.onNumberFieldChangedDebounced(text);
        }
    }

    // ----------------------- Public methods ----------------------- //
    // ----------------------- Private methods ----------------------- //
    // ----------------------- Static methods ----------------------- //
}
