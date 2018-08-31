package com.biz.navimate.views.compound;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.InputType;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.biz.navimate.R;
import com.biz.navimate.viewholders.CustomViewHolder;
import com.biz.navimate.views.custom.NvmImageButton;
import com.biz.navimate.views.custom.NvmEditText;

public class EtClearable    extends     LinearLayout
                            implements  View.OnClickListener {
    // ----------------------- Constants ----------------------- //
    private static final String TAG = "ET_CLEARABLE";

    // ----------------------- Interfaces ----------------------- //
    public void SetListener(NvmEditText.IfaceEditText listener) { ui.etText.SetListener(listener); }
    public void SetImmediateListener(NvmEditText.IfaceEditTextImmediate listener) { ui.etText.SetImmediateListener(listener); }

    // ----------------------- Globals ----------------------- //
    private Context ctx = null;
    private CustomViewHolder.EtClearable ui = new CustomViewHolder.EtClearable();

    // ----------------------- Constructors ----------------------- //
    public EtClearable(Context context) {
        super(context);
        Init(context, null);
    }

    public EtClearable(Context context, AttributeSet attrs) {
        super(context, attrs);
        Init(context, attrs);
    }

    public EtClearable(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        Init(context, attrs);
    }

    // ----------------------- Overrides ----------------------- //
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ib_etc_clear:
                // Set the current date to null
                ui.etText.setText("");
                break;
        }
    }

    // ----------------------- Public APIs ----------------------- //
    // Method to get current text
    public void SetText(String text) {
        ui.etText.setText(text);
    }

    public String GetText() {
        return ui.etText.getText().toString();
    }

    // method to set the view as read only or in editing mode
    public void SetEditable(boolean bEditable) {
        // Show / Hide editing buttons
        if (bEditable) {
            ui.etText.setEnabled(true);
            ui.ibClear.setVisibility(VISIBLE);
        } else {
            ui.etText.setEnabled(false);
            ui.ibClear.setVisibility(GONE);
        }
    }

    // Method to change input type of edit text
    public void SetInputType(int type) {
        ui.etText.setInputType(type);
    }

    // ----------------------- Private APIs ----------------------- //
    // Method to initialize view
    private void Init(Context ctx, AttributeSet attrs) {
        this.ctx = ctx;

        // Inflate Layout
        LayoutInflater inflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.custom_et_clearable, this, true);

        // Init UI
        ui.etText   = (NvmEditText)     findViewById(R.id.et_etc_text);
        ui.ibClear  = (NvmImageButton)  findViewById(R.id.ib_etc_clear);

        // Set UI properties from attributes
        SetUiAttributes(attrs);

        // Set listeners
        ui.ibClear.setOnClickListener(this);
    }

    // Method to set XML attributes programatically & add other layout related properties
    private void SetUiAttributes(AttributeSet attrs) {
        // Set Layout Params
        LinearLayout.LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        setLayoutParams(params);

        // Set orientation
        setOrientation(LinearLayout.HORIZONTAL);

        // Set Attrbutes passed from XML
        if (attrs != null) {
            // Get attributes as array
            TypedArray a = ctx.getTheme().obtainStyledAttributes(attrs, R.styleable.EtClearable, 0, 0);

            // Try reading attributes
            try {
                // Apply input type to edit text
                int inputType = a.getInt(R.styleable.EtClearable_android_inputType, InputType.TYPE_CLASS_TEXT);
                SetInputType(inputType);
            } finally {
                // Recycle attributes after use
                a.recycle();
            }
        }
    }
}
