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
import com.biz.navimate.views.custom.NvmEditText;
import com.biz.navimate.views.custom.NvmImageButton;

public class NvmToolbar     extends     LinearLayout
                            implements  View.OnClickListener,
                                        NvmEditText.IfaceEditText {
    // ----------------------- Constants ----------------------- //
    private static final String TAG = "NVM_TOOLBAR";

    // ----------------------- Interfaces ----------------------- //
    public interface IfaceToolbar { void onToolbarButtonClick(int id);}
    private IfaceToolbar listener = null;
    public void SetListener(IfaceToolbar listener) { this.listener = listener; }

    public interface IfaceToolbarSearch { void onToolbarSearch(String text); }
    private IfaceToolbarSearch searchListener = null;
    public void SetSearchListener(IfaceToolbarSearch listener) { this.searchListener = listener; }

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
        switch (view.getId()) {
            case R.id.ib_tb_search:
                ui.etcSearch.setVisibility(VISIBLE);
                ui.tvText.setVisibility(GONE);
                ui.llButtons.setVisibility(GONE);
                break;
            case R.id.ib_tb_back:
                if (ui.etcSearch.getVisibility() == VISIBLE) {
                    ui.etcSearch.setVisibility(GONE);
                    ui.tvText.setVisibility(VISIBLE);
                    ui.llButtons.setVisibility(VISIBLE);
                } else if (listener != null) {
                    listener.onToolbarButtonClick(view.getId());
                }
                break;
            default:
                if (listener != null) {
                    listener.onToolbarButtonClick(view.getId());
                }
        }
    }

    @Override
    public void onTextChanged(String text) { }

    @Override
    public void onTextChangedDebounced(String text) {
        if (searchListener != null) {
            searchListener.onToolbarSearch(text);
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
            case R.id.ib_tb_search:
                view = ui.ibSearch;
                break;
            case R.id.ib_tb_edit:
                view = ui.ibEdit;
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
        ui.tvText       = (TextView)        findViewById(R.id.tv_tb_text);
        ui.etcSearch    = (EtClearable)     findViewById(R.id.etc_tb_search);
        ui.llButtons    = (LinearLayout)    findViewById(R.id.ll_tb_buttons);
        ui.ibBack       = (NvmImageButton)  findViewById(R.id.ib_tb_back);
        ui.ibSearch     = (NvmImageButton)  findViewById(R.id.ib_tb_search);
        ui.ibEdit       = (NvmImageButton)  findViewById(R.id.ib_tb_edit);
        ui.ibSave       = (NvmImageButton)  findViewById(R.id.ib_tb_save);

        // Set UI properties
        SetUiAttributes(attrs);

        // Set listeners
        ui.etcSearch.SetListener(this);
        ui.ibBack.setOnClickListener(this);
        ui.ibSearch.setOnClickListener(this);
        ui.ibEdit.setOnClickListener(this);
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
                if(a.getBoolean(R.styleable.NvmToolbar_searchBtn, false)) { ShowButton(R.id.ib_tb_search, true); }
                if(a.getBoolean(R.styleable.NvmToolbar_editBtn, false)) { ShowButton(R.id.ib_tb_edit, true); }
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
