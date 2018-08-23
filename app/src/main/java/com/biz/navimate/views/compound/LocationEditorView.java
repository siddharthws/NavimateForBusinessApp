package com.biz.navimate.views.compound;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.biz.navimate.R;
import com.biz.navimate.activities.BaseActivity;
import com.biz.navimate.application.App;
import com.biz.navimate.constants.Constants;
import com.biz.navimate.debug.Dbg;
import com.biz.navimate.interfaces.IfaceResult;
import com.biz.navimate.objects.ObjPlace;
import com.biz.navimate.viewholders.CustomViewHolder;
import com.biz.navimate.views.custom.NvmImageButton;
import com.google.android.gms.location.places.ui.PlacePicker;

public class LocationEditorView     extends     LinearLayout
                                    implements  View.OnClickListener,
                                                IfaceResult.PlacePicker {
    // ----------------------- Constants ----------------------- //
    private static final String TAG = "LOCATION_PICKER_VIEW";

    // ----------------------- Interfaces ----------------------- //
    public interface IfaceLocationEditor {
        void onLocationChanged(ObjPlace place);
    }
    private IfaceLocationEditor listener = null;
    public void SetListener(IfaceLocationEditor listener) {
        this.listener = listener;
    }

    // ----------------------- Globals ----------------------- //
    private Context ctx = null;
    private CustomViewHolder.LocationEditor ui = new CustomViewHolder.LocationEditor();
    private ObjPlace place = null;

    // ----------------------- Constructors ----------------------- //
    public LocationEditorView(Context context) {
        super(context);
        Init(context, null);
    }

    public LocationEditorView(Context context, AttributeSet attrs) {
        super(context, attrs);
        Init(context, attrs);
    }

    public LocationEditorView(Context context, AttributeSet attrs, int defStyleAttr) {
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
                LaunchPlacePicker();
                break;
        }
    }

    @Override
    public void onPlacePicked(ObjPlace place) {
        // Set Place
        Set(place);

        // Trigger listener
        if (listener != null) {
            listener.onLocationChanged(place);
        }
    }

    // ----------------------- Public APIs ----------------------- //
    // Methods to set and get date
    public void Set(ObjPlace newPlace) {
        // Set cache
        this.place = newPlace;

        // Update Text
        RefreshText();

        // Update Clear Icon visibility
        RefreshClear();
    }

    public ObjPlace Get() {
        return place;
    }

    // method to set the view as read only or in editing mode
    public void SetEditable(boolean bEditable) {
        // Show / Hide editing buttons
        if (bEditable) {
            ui.ibEdit.setVisibility(VISIBLE);
            ui.ibClear.setVisibility(VISIBLE);
        } else {
            ui.ibEdit.setVisibility(GONE);
            ui.ibClear.setVisibility(GONE);
        }
    }

    // ----------------------- Private APIs ----------------------- //
    // Method to refresh the text view based on selected date
    private void RefreshText() {
        if (place == null) {
            // Set blank text
            ui.tvText.setText("Location not set...");
        } else {
            // Set place address as text
            ui.tvText.setText(place.GetAddress());
        }
    }

    private void RefreshClear() {
        if (place == null) {
            ui.ibClear.setVisibility(GONE);
        } else {
            ui.ibClear.setVisibility(VISIBLE);
        }
    }

    // Method to launch place picker activity
    private void LaunchPlacePicker() {
        // Get current activity
        BaseActivity currentActivity = App.GetCurrentActivity();
        if (currentActivity == null) {
            Dbg.Toast(getContext(), "Could not launch place picker", Toast.LENGTH_SHORT);
            return;
        }

        // Set place picker listener
        currentActivity.SetPlacePickerResultListener(this);

        try {
            // Create place picker intent
            PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
            Intent intent = builder.build(currentActivity);

            // Start activity using intent
            currentActivity.startActivityForResult(intent, Constants.RequestCodes.PLACE_PICKER);
        } catch (Exception e) {
            Dbg.Toast(getContext(), "Could not launch place picker", Toast.LENGTH_SHORT);
        }
    }

    // Method to initialize view
    private void Init(Context ctx, AttributeSet attrs) {
        this.ctx = ctx;

        // Inflate Layout
        LayoutInflater inflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.custom_location_editor, this, true);

        // Init UI
        ui.tvText   = (TextView)        findViewById(R.id.tv_text);
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
        // Set Layout Params
        LinearLayout.LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER_VERTICAL;
        setLayoutParams(params);

        // Set orientation
        setOrientation(LinearLayout.HORIZONTAL);
    }
}
