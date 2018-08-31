package com.biz.navimate.views.custom;

import android.content.Context;
import android.support.v7.widget.AppCompatSpinner;
import android.util.AttributeSet;

import com.biz.navimate.adapters.spinner.BaseSpinnerAdapter;

public abstract class NvmSpinner extends AppCompatSpinner {
    // ----------------------- Constants ----------------------- //
    private static final String TAG = "NVM_SPINNER";

    // ----------------------- Interfaces ----------------------- //
    public void SetListener(BaseSpinnerAdapter.IfaceSpinner listener) {
        adapter.SetListener(listener);
    }

    // ----------------------- Globals ----------------------- //
    protected BaseSpinnerAdapter adapter = null;

    // ----------------------- Abstracts ----------------------- //
    protected abstract void Init(Context ctx, AttributeSet attrs);

    // ----------------------- Constructors ----------------------- //
    public NvmSpinner(Context context) {
        super(context);
        Init(context, null);
    }

    public NvmSpinner(Context context, AttributeSet attrs) {
        super(context, attrs);
        Init(context, attrs);
    }

    public NvmSpinner(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        Init(context, attrs);
    }

    // ----------------------- Overrides ----------------------- //
    // ----------------------- Public APIs ----------------------- //
    // Method to set selection without triggering callback
    public void SetSelectionNoCallback(int position) {
        // Update cache
        adapter.SetSelectedIdx(position);

        // set selection
        setSelection(position);
    }

    // ----------------------- Private APIs ----------------------- //
}
