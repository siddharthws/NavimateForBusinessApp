package com.biz.navimate.views.custom;

import android.content.Context;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;

import com.biz.navimate.R;

public class NvmImageView extends AppCompatImageView {
    // ----------------------- Constants ----------------------- //
    private static final String TAG = "NVM_IMAGE_VIEW";

    // ----------------------- Interfaces ----------------------- //
    // ----------------------- Globals ----------------------- //
    // ----------------------- Constructors ----------------------- //
    public NvmImageView(Context context) {
        super(context);
        Init(context, null);
    }

    public NvmImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        Init(context, attrs);
    }

    public NvmImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        Init(context, attrs);
    }

    // ----------------------- Overrides ----------------------- //
    // ----------------------- Public APIs ----------------------- //
    // ----------------------- Private APIs ----------------------- //
    // Method to initialize view
    private void Init(Context ctx, AttributeSet a) {
        // Set padding
        int padding = (int) getResources().getDimension(R.dimen.margin_s);
        setPadding(padding, padding, padding, padding);
    }
}
