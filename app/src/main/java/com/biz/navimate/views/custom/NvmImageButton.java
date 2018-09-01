package com.biz.navimate.views.custom;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.widget.AppCompatImageButton;
import android.util.AttributeSet;

import com.biz.navimate.R;

public class NvmImageButton extends AppCompatImageButton {
    // ----------------------- Constants ----------------------- //
    private static final String TAG = "NVM_IMAGE_BUTTON";

    // ----------------------- Interfaces ----------------------- //
    // ----------------------- Globals ----------------------- //
    // ----------------------- Constructors ----------------------- //
    public NvmImageButton(Context context) {
        super(context);
        Init(context, null);
    }

    public NvmImageButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        Init(context, attrs);
    }

    public NvmImageButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        Init(context, attrs);
    }

    // ----------------------- Overrides ----------------------- //
    // ----------------------- Public APIs ----------------------- //
    // ----------------------- Private APIs ----------------------- //
    // Method to initialize view
    private void Init(Context ctx, AttributeSet a) {}
}
