package com.biz.navimate.activities;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import com.biz.navimate.R;
import com.biz.navimate.constants.Constants;
import com.biz.navimate.debug.Dbg;
import com.biz.navimate.objects.Statics;
import com.biz.navimate.viewholders.ActivityHolder;

import java.io.File;

/**
 * Created by Sai_Kameswari on 12-03-2018.
 */

public class PhotoEditorActivity extends BaseActivity {
    // ----------------------- Constants ----------------------- //
    private static final String TAG = "PHOTO_EDITOR_ACTIVITY";

    // ----------------------- Interfaces ----------------------- //
    // ----------------------- Globals ---------------------- //
    private ActivityHolder.PhotoEditor  ui          = null;
    private String absPath     = "";

    // ----------------------- Constructor ------------------------ //
    // ----------------------- Overrides ----------------------- //
    @Override
    protected void InflateLayout() {
        // Set content view
        setContentView(R.layout.activity_photo_editor);
    }

    @Override
    protected void FindViews() {
        // Init view holder
        ui = new ActivityHolder.PhotoEditor();
        holder = ui;

        // Activity View
        ui.ivImage              = (ImageView)    findViewById(R.id.iv_image);
        ui.btnSave              = (Button)       findViewById(R.id.btn_save);
        ui.btnBack              = (Button)       findViewById(R.id.btn_back);
    }

    @Override
    protected void SetViews() {
        // Extract image name from extra
        String imageName = "";
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            imageName = extras.getString(Constants.Extras.IMAGE_NAME);
        }

        // Add to imageview if file is existing
        if (imageName.length() > 0) {
            absPath = Statics.GetAbsolutePath(this, imageName);
            File imageFile = new File(absPath);
            if (imageFile.exists()) {
                // Set image view to visible
                ui.ivImage.setVisibility(View.VISIBLE);

                // Set bitmap to image view
                ui.ivImage.setImageBitmap(BitmapFactory.decodeFile(absPath));
            }
        }
    }

    // ----------------------- Public APIs ----------------------- //
    public static void Start(BaseActivity parentActivity, String filename)
    {
        Bundle extras = new Bundle();
        extras.putString(Constants.Extras.IMAGE_NAME, filename);
        BaseActivity.Start(parentActivity, PhotoEditorActivity.class, -1, extras, Constants.RequestCodes.PHOTO_EDITOR, null);
    }

    // Button Click APIs
    public void ButtonClickBack(View view)
    {
        super.onBackPressed();
    }

    public void ButtonClickSave(View view)
    {
        // Scale Image File after cropping
        String compressedFile = Statics.ScaleImageFile(this, absPath);

        // Send file path as result
        Intent intent = new Intent();
        intent.putExtra(Constants.Extras.IMAGE_NAME, compressedFile);
        setResult(RESULT_OK, intent);

        // Finish this activity
        finish();
    }

    // ----------------------- Private APIs ----------------------- //
}
