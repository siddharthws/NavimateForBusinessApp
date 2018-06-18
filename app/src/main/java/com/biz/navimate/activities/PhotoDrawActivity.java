package com.biz.navimate.activities;

import android.view.View;
import android.widget.Button;

import com.biz.navimate.R;
import com.biz.navimate.constants.Constants;
import com.biz.navimate.viewholders.ActivityHolder;
import com.biz.navimate.views.VwPhotoDraw;

public class PhotoDrawActivity extends BaseActivity {

    // ----------------------- Constants ----------------------- //
    private static final String TAG = "PHOTO_DRAW_ACTIVITY";


    // ----------------------- Classes ---------------------------//
    // ----------------------- Interfaces ----------------------- //
    // ----------------------- Globals ----------------------- //
    private ActivityHolder.PhotoDraw ui = null;

    // ----------------------- Constructor ----------------------- //
    // ----------------------- Abstracts ----------------------- //
    // ----------------------- Overrides ----------------------- //
    // Init Overrides
    @Override
    protected void InflateLayout() {
        // Set content view
        setContentView(R.layout.activity_photo_draw);
    }

    @Override
    protected void FindViews() {
        // Init view holder
        ui = new ActivityHolder.PhotoDraw();
        holder = ui;

        // Fins Views by ID
        ui.btnBack  = (Button) findViewById(R.id.btn_back);
        ui.btnClear = (Button) findViewById(R.id.btn_clear);
        ui.btnSave  = (Button) findViewById(R.id.btn_save);
        ui.vwPhotoDraw = (VwPhotoDraw) findViewById(R.id.vw_photoDraw);
    }

    @Override
    protected void SetViews() {}

    // ----------------------- Public APIs ----------------------- //
    public static void Start(BaseActivity activity) {
        BaseActivity.Start(activity, PhotoDrawActivity.class, -1, null, Constants.RequestCodes.PHOTO_DRAW, null);
    }

    public void ButtonClickBack(View view) {
        // Finish this activity
        finish();
    }

    public void ButtonClickClear(View view) {
        // Clear Signature View
        ui.vwPhotoDraw.startNew();
    }

    public void ButtonClickSave(View view) {
        // Finish this activity
        finish();
    }

    // ----------------------- Private APIs ----------------------- //
}
