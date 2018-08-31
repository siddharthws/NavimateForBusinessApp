package com.biz.navimate.views.compound;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

import com.biz.navimate.R;
import com.biz.navimate.viewholders.CustomViewHolder;

public class NvmScrollView extends RelativeLayout {
    // ----------------------- Constants ----------------------- //
    private static final String TAG = "NVM_SCROLL_VIEW";

    // ----------------------- Interfaces ----------------------- //
    // ----------------------- Globals ----------------------- //
    public CustomViewHolder.NvmScrollView ui = new CustomViewHolder.NvmScrollView();

    // ----------------------- Constructors ----------------------- //
    public NvmScrollView(Context context) {
        super(context);
        Init(context, null);
    }

    public NvmScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        Init(context, attrs);
    }

    public NvmScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        Init(context, attrs);
    }

    // ----------------------- Overrides ----------------------- //
    @Override
    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        if(ui.svContainer == null){
            super.addView(child, index, params);
        } else {
            //Forward these calls to the content view
            ui.svContainer.addView(child, index, params);
        }
    }

    // ----------------------- Public APIs ----------------------- //
    // ----------------------- Private APIs ----------------------- //
    // Method to initialize view
    private void Init(Context ctx, AttributeSet attrs) {
        // Inflate Layout
        LayoutInflater inflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.custom_nvm_scrollview, this, true);

        // Init UI
        ui.svContainer  = (ScrollView)        findViewById(R.id.sv_container);
    }
}
