package com.biz.navimate.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
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
    private String absPath     = "";

    // ----------------------- Classes ---------------------------//
    // ----------------------- Interfaces ----------------------- //
    // ----------------------- Globals ----------------------- //
    private ActivityHolder.PhotoDraw ui = null;
    Bitmap bitmap = null;
    Bitmap reduced_bitmap = null;

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
        System.out.println("ImagepATH: "+absPath);
        // Add to imageview if file is existing
        if (absPath.length() > 0) {
            File imageFile = new File(absPath);
            if (imageFile.exists())
            {
                bitmap = BitmapFactory.decodeFile(absPath).copy(Bitmap.Config.ARGB_8888, true);
                reduced_bitmap = getResizedBitmap(bitmap, 512, 1024);
            }
        }
        System.out.println("setViews Called");
        // Set bitmap
        ui.vwPhotoDraw.setNewImage(reduced_bitmap);
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
        // Clear Draw View
        ui.vwPhotoDraw.setNewImage(bitmap);
    }

    public void ButtonClickSave(View view) {
        // Finish this activity
        finish();
    }

    // ----------------------- Private APIs ----------------------- //
    private static Bitmap getResizedBitmap(Bitmap image, int newHeight, int newWidth) {
        int width = image.getWidth();
        int height = image.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // create a matrix for the manipulation
        Matrix matrix = new Matrix();
        // resize the bit map
        matrix.postScale(scaleWidth, scaleHeight);
        // recreate the new Bitmap
        Bitmap resizedBitmap = Bitmap.createBitmap(image, 0, 0, width, height,
                matrix, false);
        return resizedBitmap;
    }
}
