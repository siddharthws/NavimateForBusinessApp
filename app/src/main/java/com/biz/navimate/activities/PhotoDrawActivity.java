package com.biz.navimate.activities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.widget.Button;

import com.biz.navimate.R;
import com.biz.navimate.constants.Constants;
import com.biz.navimate.viewholders.ActivityHolder;
import com.biz.navimate.views.DrawableImageView;
import com.biz.navimate.views.VwPhotoDraw;

public class PhotoDrawActivity extends BaseActivity {

    // ----------------------- Constants ----------------------- //
    private static final String TAG = "PHOTO_DRAW_ACTIVITY";


    // ----------------------- Classes ---------------------------//
    // ----------------------- Interfaces ----------------------- //
    // ----------------------- Globals ----------------------- //
    private ActivityHolder.PhotoDraw ui = null;
    Bitmap bitmap = null;

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
        ui.vwPhotoDraw = (DrawableImageView) findViewById(R.id.vw_photoDraw);
    }

    @Override
    protected void SetViews() {
        // Get bitmap
        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.test2).copy(Bitmap.Config.ARGB_8888, true);

        // Set bitmap
        ui.vwPhotoDraw.setNewImage(bitmap);
    }

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
        ui.vwPhotoDraw.setNewImage(bitmap);
    }

    public void ButtonClickSave(View view) {
        // Finish this activity
        finish();
    }

    // ----------------------- Private APIs ----------------------- //
}
