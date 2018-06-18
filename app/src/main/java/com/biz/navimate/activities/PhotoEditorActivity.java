package com.biz.navimate.activities;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.biz.navimate.R;
import com.biz.navimate.application.App;
import com.biz.navimate.constants.Constants;
import com.biz.navimate.interfaces.IfaceResult;
import com.biz.navimate.objects.FormEntry;
import com.biz.navimate.objects.Statics;
import com.biz.navimate.viewholders.ActivityHolder;

import java.io.File;
import java.util.List;

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
    private File imageFile = null;

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
            imageFile = new File(absPath);
            if (imageFile.exists()) {
                // Set image view to visible
                ui.ivImage.setVisibility(View.VISIBLE);

                // Set bitmap to image view
                Bitmap bitmap = BitmapFactory.decodeFile(absPath);
                ui.ivImage.setImageBitmap(bitmap);
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
        // Send file path as result
        Intent intent = new Intent();
        intent.putExtra(Constants.Extras.IMAGE_NAME, imageFile.getName());
        setResult(RESULT_OK, intent);

        // Finish this activity
        finish();
    }

    public void ButtonClickCrop(View view)
    {
        BaseActivity activity = App.GetCurrentActivity();

        // Ignore if activity is not available
        if (activity == null) {
            return;
        }

        Intent cropPictureIntent = new Intent("com.android.camera.action.CROP");
        //get Image URI
        File photoFile = new File(absPath);
        if (photoFile != null) {
            Uri photoURI = FileProvider.getUriForFile(  this,"com.biz.navimate.fileprovider", photoFile);
            cropPictureIntent.setDataAndType(photoURI, "image/*");
            cropPictureIntent.putExtra("crop", "true");
            cropPictureIntent.putExtra("scaleUpIfNeeded", true);
            cropPictureIntent.putExtra("return-data", false);
            cropPictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
            cropPictureIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            cropPictureIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            List<ResolveInfo> resInfoList = getPackageManager().queryIntentActivities(cropPictureIntent, PackageManager.MATCH_DEFAULT_ONLY);
            for (ResolveInfo resolveInfo : resInfoList) {
                String packageName = resolveInfo.activityInfo.packageName;
                grantUriPermission(packageName, photoURI, Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            }
            activity.SetPhotoCropListener(new IfaceResult.Crop() {
                @Override
                public void onPhotoCrop()
                {
                    ui.ivImage.setImageBitmap(BitmapFactory.decodeFile(absPath));
                }
            });
            activity.startActivityForResult(cropPictureIntent, Constants.RequestCodes.PHOTO_CROP);
        }
    }

    public void ButtonClickDraw(View view)
    {
        BaseActivity activity = App.GetCurrentActivity();
        //set photo editor activity listener
        activity.SetPhotoDrawListener(new IfaceResult.PhotoDraw() {
            @Override
            public void onPhotoDraw() {
                System.out.println("Reached the photo draw");
            }
        });
        PhotoDrawActivity.Start(activity);
    }

    // ----------------------- Private APIs ----------------------- //
}
