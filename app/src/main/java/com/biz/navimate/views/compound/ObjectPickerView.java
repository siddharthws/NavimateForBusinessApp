package com.biz.navimate.views.compound;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.biz.navimate.R;
import com.biz.navimate.activities.BaseActivity;
import com.biz.navimate.activities.ObjectPickerActivity;
import com.biz.navimate.application.App;
import com.biz.navimate.constants.Constants;
import com.biz.navimate.debug.Dbg;
import com.biz.navimate.interfaces.IfaceResult;
import com.biz.navimate.objects.core.ObjNvmCompact;
import com.biz.navimate.viewholders.CustomViewHolder;
import com.biz.navimate.views.custom.NvmImageButton;
import com.biz.navimate.views.custom.NvmImageView;

public class ObjectPickerView   extends     LinearLayout
                                implements  View.OnClickListener,
                                            IfaceResult.ObjectPicker {
    // ----------------------- Constants ----------------------- //
    private static final String TAG = "OBJECT_PICKER_VIEW";

    // ----------------------- Interfaces ----------------------- //
    public interface IfaceObjectPicker {
        void onObjectPicked(ObjNvmCompact obj);
    }
    private IfaceObjectPicker listener = null;
    public void SetListener(IfaceObjectPicker listener) {
        this.listener = listener;
    }

    // ----------------------- Globals ----------------------- //
    private Context ctx = null;
    private CustomViewHolder.ObjectPicker ui = new CustomViewHolder.ObjectPicker();
    private int type = Constants.Template.TYPE_INVALID;
    private ObjNvmCompact obj = null;

    // ----------------------- Constructors ----------------------- //
    public ObjectPickerView(Context context) {
        super(context);
        Init(context, null);
    }

    public ObjectPickerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        Init(context, attrs);
    }

    public ObjectPickerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        Init(context, attrs);
    }

    // ----------------------- Overrides ----------------------- //
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ib_clear:
                // Set the current location to null
                Set(null);
                break;
            case R.id.ib_edit:
                // Show place picker picker dialog
                LaunchObjectPicker();
                break;
        }
    }

    @Override
    public void onObjectPicked(ObjNvmCompact newObj) {
        // Set Place
        Set(newObj);

        // Trigger listener
        if (listener != null) {
            listener.onObjectPicked(newObj);
        }
    }

    // ----------------------- Public APIs ----------------------- //
    // Method to set type of object
    public void SetType(int type) {
        // Cache type
        this.type = type;

        // Set icon
        int iconId = 0;
        switch (type) {
            case Constants.Template.TYPE_PRODUCT:
                iconId = R.drawable.ic_inventory_grey;
                break;
        }

        if (iconId != 0) {
            ui.ivIcon.setBackground(ContextCompat.getDrawable(getContext(), iconId));
        }
    }

    // Methods to set and get data
    public void Set(ObjNvmCompact newObj) {
        // Set cache
        this.obj = newObj;

        // Update Text
        RefreshText();

        // Update Clear Icon visibility
        RefreshClear();
    }

    public ObjNvmCompact Get() {
        return obj;
    }

    // method to set the view as read only or in editing mode
    public void SetEditable(boolean bEditable) {
        // Show / Hide editing buttons
        if (bEditable) {
            ui.ibEdit.setVisibility(VISIBLE);
            RefreshClear();
        } else {
            ui.ibEdit.setVisibility(GONE);
            ui.ibClear.setVisibility(GONE);
        }
    }

    // ----------------------- Private APIs ----------------------- //
    // Method to refresh the text view based on selected object
    private void RefreshText() {
        if (obj == null) {
            // Set blank text
            ui.tvText.setText("Not set...");
        } else {
            // Set obj name as text
            ui.tvText.setText(obj.name);
        }
    }

    private void RefreshClear() {
        if (obj == null) {
            ui.ibClear.setVisibility(GONE);
        } else {
            ui.ibClear.setVisibility(VISIBLE);
        }
    }

    // Method to launch place picker activity
    private void LaunchObjectPicker() {
        // Get current activity
        BaseActivity currentActivity = App.GetCurrentActivity();
        if (currentActivity == null) {
            Dbg.Toast(getContext(), "Could not launch picker", Toast.LENGTH_SHORT);
            return;
        }

        // Set picker listener & launch activity
        currentActivity.SetObjectPickerResultListener(this);

        // Launch Activity
        ObjectPickerActivity.Start(currentActivity, type);
    }

    // Method to initialize view
    private void Init(Context ctx, AttributeSet attrs) {
        this.ctx = ctx;

        // Inflate Layout
        LayoutInflater inflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.custom_object_picker, this, true);

        // Init UI
        ui.ivIcon   = (NvmImageView)    findViewById(R.id.iv_icon);
        ui.tvText   = (TextView)        findViewById(R.id.tv_name);
        ui.ibClear  = (NvmImageButton)  findViewById(R.id.ib_clear);
        ui.ibEdit   = (NvmImageButton)  findViewById(R.id.ib_edit);

        // Set UI properties
        SetUiAttributes();

        // Set listeners
        ui.ibClear.setOnClickListener(this);
        ui.ibEdit.setOnClickListener(this);
    }

    // Method to set any extra attributes
    private void SetUiAttributes() {
        // Set orientation
        setOrientation(LinearLayout.HORIZONTAL);
    }
}
