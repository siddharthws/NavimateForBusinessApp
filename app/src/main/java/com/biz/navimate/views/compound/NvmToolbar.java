package com.biz.navimate.views.compound;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.biz.navimate.R;
import com.biz.navimate.viewholders.CustomViewHolder;
import com.biz.navimate.views.custom.NvmImageButton;

public class NvmToolbar     extends     LinearLayout
                            implements  View.OnClickListener {
    // ----------------------- Constants ----------------------- //
    private static final String TAG = "NVM_TOOLBAR";

    // ----------------------- Interfaces ----------------------- //
    public interface IfaceToolbar {
        void onToolbarButtonClick(int id);
    }
    private IfaceToolbar listener = null;
    public void SetListener(IfaceToolbar listener) {
        this.listener = listener;
    }

    // ----------------------- Globals ----------------------- //
    private Context ctx = null;
    private CustomViewHolder.NvmToolbar ui = new CustomViewHolder.NvmToolbar();

    // ----------------------- Constructors ----------------------- //
    public NvmToolbar(Context context) {
        super(context);
        Init(context, null);
    }

    public NvmToolbar(Context context, AttributeSet attrs) {
        super(context, attrs);
        Init(context, attrs);
    }

    public NvmToolbar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        Init(context, attrs);
    }

    // ----------------------- Overrides ----------------------- //
    @Override
    public void onClick(View view) {
        if (listener != null) {
            listener.onToolbarButtonClick(view.getId());
        }
    }

    // ----------------------- Public APIs ----------------------- //
    // Methods to set properties
    public void SetTitle(String text) {
        ui.tvText.setText(text);
    }

    public void ShowButton(int id, boolean bShow) {
        View view = null;

        // Get button reference to update visibility
        switch (id) {
            case R.id.ib_tb_back:
                view = ui.ibBack;
                break;
            case R.id.ib_tb_save:
                view = ui.ibSave;
                break;
        }

        // Set visibility as per flag
        if (bShow) { view.setVisibility(VISIBLE); }
        else { view.setVisibility(GONE); }
    }

    // ----------------------- Private APIs ----------------------- //
    // Method to initialize view
    private void Init(Context ctx, AttributeSet attrs) {
        this.ctx = ctx;

        // Inflate Layout
        LayoutInflater inflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.custom_nvm_toolbar, this, true);

        // Init UI
        ui.tvText   = (TextView)        findViewById(R.id.tv_tb_text);
        ui.ibBack   = (NvmImageButton)  findViewById(R.id.ib_tb_back);
        ui.ibSave   = (NvmImageButton)  findViewById(R.id.ib_tb_save);

        // Set UI properties
        SetUiAttributes(attrs);

        // Set listeners
        ui.ibBack.setOnClickListener(this);
        ui.ibSave.setOnClickListener(this);
    }

    // Method to set any extra attributes
    private void SetUiAttributes(AttributeSet attrs) {
        // Set orientation
        setOrientation(LinearLayout.HORIZONTAL);

        // Set Padding
        int marginS = (int) getResources().getDimension(R.dimen.margin_s);
        setPadding(marginS, marginS, marginS, marginS);

        // Set Background
        setBackground(ContextCompat.getDrawable(ctx, R.drawable.bg_primary_shadow_down));

        // Parse attributes
        if (attrs != null) {
            // Get attributes as array
            TypedArray a = ctx.getTheme().obtainStyledAttributes(attrs, R.styleable.NvmToolbar, 0, 0);

            // Try reading attributes
            try {
                // Show buttons based on input
                if(a.getBoolean(R.styleable.NvmToolbar_backBtn, false)) { ShowButton(R.id.ib_tb_back, true); }
                if(a.getBoolean(R.styleable.NvmToolbar_saveBtn, false)) { ShowButton(R.id.ib_tb_save, true); }

                // Set Title in toolbar
                String title = a.getString(R.styleable.NvmToolbar_title);
                if (title != null) { SetTitle(title); }
            } finally {
                // Recycle attributes after use
                a.recycle();
            }
        }
    }
}
