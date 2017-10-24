package com.biz.navimate.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;

import com.biz.navimate.R;
import com.biz.navimate.objects.FormField;

/**
 * Created by Siddharth on 23-10-2017.
 */

public class RlFormField extends RelativeLayout {
    // ----------------------- Constants ----------------------- //
    public static final String TAG = "RL_FORM_FIELD";

    // ----------------------- Classes ---------------------------//
    // ----------------------- Interfaces ----------------------- //
    // ----------------------- Globals ----------------------- //
    // UI
    private TvCalibri tvTitle;
    private EditText etNumber, etText = null;
    private RadioGroup rgRadioList = null;

    private FormField.Base field = null;

    // ----------------------- Constructor ----------------------- //

    public RlFormField(Context context)
    {
        super(context);
        InitView(context);
    }

    public RlFormField(Context context, FormField.Base field)
    {
        super(context);
        this.field = field;
        InitView(context);
    }

    public RlFormField(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        InitView(context);
    }

    public RlFormField(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        InitView(context);
    }

    // ----------------------- Overrides ----------------------- //
    // ----------------------- Public APIs ----------------------- //
    public FormField.Base GetField() {
        if (field.type.equals(FormField.TYPE_TEXT)) {
            ((FormField.Text) field).data = etText.getText().toString();
        } else if (field.type.equals(FormField.TYPE_NUMBER)) {
            ((FormField.Number) field).data = Integer.parseInt(etNumber.getText().toString());
        } else if (field.type.equals(FormField.TYPE_RADIO_LIST)) {
            for (int i = 0; i < rgRadioList.getChildCount(); i++) {
                RbCustom rb = (RbCustom) rgRadioList.getChildAt(i);
                if (rb.isChecked()) {
                    ((FormField.RadioList) field).selection = rb.getText().toString();
                    break;
                }
            }
        }

        return field;
    }

    // ----------------------- Private APIs ----------------------- //
    private void InitView(Context context)
    {
        // Inflate Layout
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.rl_form_field, this, true);

        // Init UI
        tvTitle = (TvCalibri) view.findViewById(R.id.tv_title);
        etNumber = (EditText) view.findViewById(R.id.et_number);
        etText = (EditText) view.findViewById(R.id.et_text);
        rgRadioList = (RadioGroup) view.findViewById(R.id.rg_radioList);

        // Populate title
        tvTitle.setText(field.title);

        // Parse data from Field into UI
        if (field.type.equals(FormField.TYPE_TEXT)) {
            etText.setVisibility(VISIBLE);
            etText.setText(((FormField.Text) field).data);
        } else if (field.type.equals(FormField.TYPE_NUMBER)) {
            etNumber.setVisibility(VISIBLE);
            etNumber.setText(String.valueOf(((FormField.Number) field).data));
        } else if (field.type.equals(FormField.TYPE_RADIO_LIST)) {
            rgRadioList.setVisibility(VISIBLE);

            // Add radio options
            for (String option : ((FormField.RadioList) field).options) {
                RbCustom rb = new RbCustom(context);
                rb.setText(option);

                // Add to group
                rgRadioList.addView(rb);

                // Check selected
                if (option.equals(((FormField.RadioList) field).selection)) {
                    rb.setChecked(true);
                }
            }
        }
    }
}
