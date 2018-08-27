package com.biz.navimate.views.compound;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.biz.navimate.R;
import com.biz.navimate.viewholders.CustomViewHolder;

public class LabelBox extends LinearLayout {
    // ----------------------- Constants ----------------------- //
    private static final String TAG = "LABEL_BOX";

    // ----------------------- Interfaces ----------------------- //
    // ----------------------- Globals ----------------------- //
    private Context ctx = null;
    public CustomViewHolder.LabelBox ui = new CustomViewHolder.LabelBox();

    // ----------------------- Constructors ----------------------- //
    public LabelBox(Context context) {
        super(context);
        Init(context, null);
    }

    public LabelBox(Context context, AttributeSet attrs) {
        super(context, attrs);
        Init(context, attrs);
    }

    public LabelBox(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        Init(context, attrs);
    }

    // ----------------------- Overrides ----------------------- //
    @Override
    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        if(ui.rlContainer == null){
            super.addView(child, index, params);
        } else {
            //Forward these calls to the content view
            ui.rlContainer.addView(child, index, params);
        }
    }

    // ----------------------- Public APIs ----------------------- //
    // Method to set label
    public void SetLabel (String label) {
        ui.tvLabel.setText(label);
    }

    // ----------------------- Private APIs ----------------------- //
    // Method to initialize view
    private void Init(Context ctx, AttributeSet attrs) {
        this.ctx = ctx;

        // Inflate Layout
        LayoutInflater inflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.custom_label_box, this, true);

        // Init UI
        ui.tvLabel      = (TextView)        findViewById(R.id.tv_lbl_label);
        ui.rlContainer  = (RelativeLayout)  findViewById(R.id.rl_lbl_child_root);

        // Set UI properties
        SetUiAttributes(attrs);
    }

    // Method to set any extra attributes
    private void SetUiAttributes(AttributeSet attrs) {
        // Set orientation
        setOrientation(LinearLayout.VERTICAL);

        // Parse attributes
        if (attrs != null) {
            // Get attributes as array
            TypedArray a = ctx.getTheme().obtainStyledAttributes(attrs, R.styleable.LabelBox, 0, 0);

            // Try reading attributes
            try {
                // Set Label
                String label = a.getString(R.styleable.LabelBox_label);
                if (label != null) { SetLabel(label); }
            } finally {
                // Recycle attributes after use
                a.recycle();
            }
        }
    }
}
