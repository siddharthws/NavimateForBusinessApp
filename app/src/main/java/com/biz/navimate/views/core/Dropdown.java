package com.biz.navimate.views.core;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.widget.AppCompatSpinner;
import android.util.AttributeSet;

import com.biz.navimate.R;
import com.biz.navimate.adapters.spinner.BaseSpinnerAdapter;
import com.biz.navimate.adapters.spinner.DropdownAdapter;
import com.biz.navimate.objects.ObjSpinner;

import java.util.ArrayList;

public class Dropdown extends AppCompatSpinner {
    // ----------------------- Constants ----------------------- //
    // ----------------------- Interfaces ----------------------- //
    public void SetListener(BaseSpinnerAdapter.IfaceSpinner listener) {
        adapter.SetListener(listener);
    }

    // ----------------------- Globals ----------------------- //
    private DropdownAdapter adapter = null;
    private ArrayList<String> mList = new ArrayList<>();

    // ----------------------- Constructors ----------------------- //
    public Dropdown(Context context) {
        super(context);
        Init(context, null);
    }

    public Dropdown(Context context, AttributeSet attrs) {
        super(context, attrs);
        Init(context, attrs);
    }

    public Dropdown(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        Init(context, attrs);
    }

    // ----------------------- Public APIs ----------------------- //
    public void SetList(ArrayList<String> list) {
        // Set cache
        mList = list;

        // Reset spinner
        Reset();
    }

    // ----------------------- Private APIs ----------------------- //
    // Method to reset data in a spinner
    private void Reset() {
        // Clear adapter
        adapter.Clear();

        // Add all strings to adapter
        for (String item : mList) {
            adapter.Add(new ObjSpinner.Dropdown(item));
        }
    }

    // Method to initialize view
    private void Init(Context ctx, AttributeSet attrs) {
        // Read attributes
        ReadAttributes(ctx, attrs);

        // Initialize Adapter
        adapter = new DropdownAdapter(ctx, this);
    }

    // Method read attributes and populate global variables
    private void ReadAttributes(Context ctx, AttributeSet attrs) {
        // Ignore if no attributes supplied
        if (attrs == null) {
            return;
        }

        // Get attributes as array
        TypedArray a = ctx.getTheme().obtainStyledAttributes(attrs, R.styleable.Dropdown, 0, 0);

        // Try reading attributes
        try {

        } finally {
            // Recycle attributes after use
            a.recycle();
        }
    }
}
