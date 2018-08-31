package com.biz.navimate.views.custom;

import android.content.Context;
import android.util.AttributeSet;

import com.biz.navimate.adapters.spinner.DropdownAdapter;
import com.biz.navimate.objects.ObjSpinner;

import java.util.ArrayList;

public class Dropdown extends NvmSpinner {
    // ----------------------- Constants ----------------------- //
    private static final String TAG = "DROPDOWN";

    // ----------------------- Globals ----------------------- //
    private ArrayList<String> mList = new ArrayList<>();

    // ----------------------- Constructors ----------------------- //
    public Dropdown(Context context) {
        super(context);
    }

    public Dropdown(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public Dropdown(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    // ----------------------- Overrides ----------------------- //
    @Override
    protected void Init(Context ctx, AttributeSet attrs) {
        // Initialize Adapter
        adapter = new DropdownAdapter(ctx, this);
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
}
