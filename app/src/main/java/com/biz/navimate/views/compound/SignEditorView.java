package com.biz.navimate.views.compound;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.biz.navimate.R;
import com.biz.navimate.activities.BaseActivity;
import com.biz.navimate.activities.SignatureActivity;
import com.biz.navimate.application.App;
import com.biz.navimate.debug.Dbg;
import com.biz.navimate.interfaces.IfaceResult;
import com.biz.navimate.viewholders.CustomViewHolder;
import com.biz.navimate.views.custom.NvmImageButton;
import com.biz.navimate.views.custom.PhotoThumbnailView;

public class SignEditorView     extends     LinearLayout
                                implements  View.OnClickListener,
                                            IfaceResult.Signature {
    // ----------------------- Constants ----------------------- //
    private static final String TAG = "SIGN_EDITOR_VIEW";

    // ----------------------- Interfaces ----------------------- //
    public interface IfaceSignEditor {
        void onSignChanged(String name);
    }
    private IfaceSignEditor listener = null;
    public void SetListener(IfaceSignEditor listener) {
        this.listener = listener;
    }

    // ----------------------- Globals ----------------------- //
    private Context ctx = null;

    private CustomViewHolder.SignEditor ui = new CustomViewHolder.SignEditor();
    private String filename = "";

    private boolean bEditable = true;

    // ----------------------- Constructors ----------------------- //
    public SignEditorView(Context context) {
        super(context);
        Init(context, null);
    }

    public SignEditorView(Context context, AttributeSet attrs) {
        super(context, attrs);
        Init(context, attrs);
    }

    public SignEditorView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        Init(context, attrs);
    }

    // ----------------------- Overrides ----------------------- //
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ib_clear:
                // Reset
                Set("");
                break;
            case R.id.ib_edit:
                // Start activity to capture signature
                LaunchSignatureActivity();
                break;
        }
    }

    @Override
    public void onSignatureResult(String fileName) {
        Set(fileName);
    }

    // ----------------------- Public APIs ----------------------- //
    // Methods to set and get date
    public void Set(String name) {
        // Set cache
        this.filename = name;

        // Update Image
        ui.ptThumbnail.Set(filename);

        // Update Clear Icon visibility
        RefreshClear();
    }

    public String Get() {
        return filename;
    }

    // method to set the view as read only or in editing mode
    public void SetEditable(boolean bEditable) {
        this.bEditable = bEditable;

        // Show / Hide editing buttons
        if (bEditable) {
            ui.ibEdit.setVisibility(VISIBLE);
            RefreshClear();
        } else {
            ui.ibEdit.setVisibility(GONE);
            ui.ibClear.setVisibility(GONE);
        }
    }

    public boolean IsEditable() {
        return bEditable;
    }

    // ----------------------- Private APIs ----------------------- //
    private void RefreshClear() {
        if (filename.length() == 0) {
            ui.ibClear.setVisibility(GONE);
        } else {
            ui.ibClear.setVisibility(VISIBLE);
        }
    }

    // Methods to launch camera to capture photo
    private void LaunchSignatureActivity() {
        // Get current activity
        BaseActivity currentActivity = App.GetCurrentActivity();
        if (currentActivity == null) {
            Dbg.Toast(getContext(), "Could not launch signature activity...", Toast.LENGTH_SHORT);
            return;
        }

        // Set result listener
        currentActivity.SetSignResultListener(this);

        // Trigger Camera intent
        SignatureActivity.Start(currentActivity);
    }

    // Method to initialize view
    private void Init(Context ctx, AttributeSet attrs) {
        this.ctx = ctx;

        // Inflate Layout
        LayoutInflater inflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.custom_sign_editor, this, true);

        // Set UI properties
        SetUiAttributes();

        // Init UI
        ui.ptThumbnail  = (PhotoThumbnailView)  findViewById(R.id.pt_thumbnail);
        ui.ibClear      = (NvmImageButton)  findViewById(R.id.ib_clear);
        ui.ibEdit       = (NvmImageButton)  findViewById(R.id.ib_edit);

        // Set listeners
        ui.ibClear.setOnClickListener(this);
        ui.ibEdit.setOnClickListener(this);
    }

    // Method to set any extra attributes
    private void SetUiAttributes() {
        // Set Layout Params
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER_VERTICAL;
        setLayoutParams(params);

        // Set orientation
        setOrientation(LinearLayout.HORIZONTAL);
    }
}
