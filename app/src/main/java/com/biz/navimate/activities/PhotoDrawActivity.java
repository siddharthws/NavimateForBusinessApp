package com.biz.navimate.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.biz.navimate.R;
import com.biz.navimate.constants.Constants;
import com.biz.navimate.objects.Statics;
import com.biz.navimate.viewholders.ActivityHolder;
import com.biz.navimate.views.DrawableImageView;

import java.io.File;

public class PhotoDrawActivity extends BaseActivity {

    // ----------------------- Constants ----------------------- //
    private static final String TAG = "PHOTO_DRAW_ACTIVITY";

    // ----------------------- Classes ---------------------------//
    // ----------------------- Interfaces ----------------------- //
    // ----------------------- Globals ----------------------- //
    private ActivityHolder.PhotoDraw ui = null;
    private String absPath     = "";
    private Bitmap bitmap = null;

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
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            absPath = extras.getString(Constants.Extras.IMAGE_PATH);
        }

        // Add to imageview if file is existing
        if (absPath.length() > 0) {
            File imageFile = new File(absPath);
            if (imageFile.exists())
            {
                bitmap = BitmapFactory.decodeFile(absPath).copy(Bitmap.Config.ARGB_8888, true);
            }
        }

        // Set bitmap
        ui.vwPhotoDraw.setNewImage(bitmap);
    }

    // ----------------------- Public APIs ----------------------- //
    public static void Start(BaseActivity activity, String absPath) {
        Bundle extras = new Bundle();
        extras.putString(Constants.Extras.IMAGE_PATH, absPath);
        BaseActivity.Start(activity, PhotoDrawActivity.class, -1, extras, Constants.RequestCodes.PHOTO_DRAW, null);
    }

    public void ButtonClickBack(View view) {
        finish();
    }

    public void ButtonClickClear(View view) {
        // Clear existing Bitmap and write a new bitmap
        bitmap.recycle();
        bitmap = BitmapFactory.decodeFile(absPath).copy(Bitmap.Config.ARGB_8888, true);
        ui.vwPhotoDraw.setNewImage(bitmap);
    }

    public void ButtonClickSave(View view) {
        // Get image file from Signature View contents
        String compressedFileName = Statics.GetFileFromView(ui.vwPhotoDraw);

        // Send file path as result
        Intent intent = new Intent();
        intent.putExtra(Constants.Extras.IMAGE_NAME, compressedFileName);
        setResult(RESULT_OK, intent);

        // Finish this activity
        finish();
    }

    // ----------------------- Private APIs ----------------------- //
}