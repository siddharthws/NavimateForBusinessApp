package com.biz.navimate.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

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
        ui.vwPhotoDraw = (DrawableImageView) findViewById(R.id.vw_photoDraw);
        ui.toolbar     = (Toolbar) findViewById(R.id.toolbar);

        //call method to Set toolbar
        setToolbar();
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.photodrawtools, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // action with ID action_refresh was selected
            case R.id.action_save:
            {
                ButtonClickSave();
                break;
            }
            case R.id.action_clear:
            {
                ButtonClickClear();
                break;
            }
        }
        return true;
    }

    // ----------------------- Public APIs ----------------------- //
    public static void Start(BaseActivity activity, String absPath) {
        Bundle extras = new Bundle();
        extras.putString(Constants.Extras.IMAGE_PATH, absPath);
        BaseActivity.Start(activity, PhotoDrawActivity.class, -1, extras, Constants.RequestCodes.PHOTO_DRAW, null);
    }

    public void ButtonClickBack() {
        finish();
    }

    public void ButtonClickClear() {
        // Clear existing Bitmap and write a new bitmap
        bitmap.recycle();
        bitmap = BitmapFactory.decodeFile(absPath).copy(Bitmap.Config.ARGB_8888, true);
        ui.vwPhotoDraw.setNewImage(bitmap);
    }

    public void ButtonClickSave() {
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
    private void setToolbar()
    {
        setSupportActionBar(ui.toolbar);
        getSupportActionBar().setTitle("Photo Draw");
        ui.toolbar.setNavigationIcon(R.drawable.ic_back_white);

        ui.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ButtonClickBack();
            }
        });
    }
}