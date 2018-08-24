package com.biz.navimate.activities;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.biz.navimate.R;
import com.biz.navimate.application.App;
import com.biz.navimate.constants.Constants;
import com.biz.navimate.interfaces.IfaceResult;
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
        ui.toolbar              = (Toolbar)       findViewById(R.id.toolbar);

        //call method to Set toolbar
        setToolbar();
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
            absPath = Statics.GetAbsolutePath(this, imageName, Environment.DIRECTORY_PICTURES);
            File imageFile = new File(absPath);
            if (imageFile.exists()) {
                // Set image view to visible
                ui.ivImage.setVisibility(View.VISIBLE);

                // Set bitmap to image view
                Bitmap bitmap = BitmapFactory.decodeFile(absPath);
                ui.ivImage.setImageBitmap(bitmap);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.photoeditortools, menu);
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
            case R.id.action_crop:
            {
                ButtonClickCrop();
                break;
            }
            case R.id.action_draw:
            {
                ButtonClickDraw();
                break;
            }
        }
        return true;
    }

    // ----------------------- Public APIs ----------------------- //
    public static void Start(BaseActivity parentActivity, String filename)
    {
        Bundle extras = new Bundle();
        extras.putString(Constants.Extras.IMAGE_NAME, filename);
        BaseActivity.Start(parentActivity, PhotoEditorActivity.class, -1, extras, Constants.RequestCodes.PHOTO_EDITOR, null);
    }

    // Menu Button Click APIs
    public void ButtonClickBack()
    {
        finish();
    }

    public void ButtonClickSave()
    {
        // Send file path as result
        Intent intent = new Intent();
        File imageFile = new File(absPath);
        intent.putExtra(Constants.Extras.IMAGE_NAME, imageFile.getName());
        setResult(RESULT_OK, intent);

        // Finish this activity
        finish();
    }

    public void ButtonClickCrop()
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

    public void ButtonClickDraw()
    {
        BaseActivity activity = App.GetCurrentActivity();
        //set photo editor activity listener
        activity.SetPhotoDrawListener(new IfaceResult.PhotoDraw() {
            @Override
            public void onPhotoDraw(String drawnImageName)
            {
                absPath = Statics.GetAbsolutePath(getApplicationContext(), drawnImageName, Environment.DIRECTORY_PICTURES);

                // Set bitmap to image view
                Bitmap bitmap = BitmapFactory.decodeFile(absPath);
                ui.ivImage.setImageBitmap(bitmap);
            }
        });
        PhotoDrawActivity.Start(activity, absPath);
    }

    // ----------------------- Private APIs ----------------------- //
    private void setToolbar()
    {
        setSupportActionBar(ui.toolbar);
        getSupportActionBar().setTitle("Photo Editor");
        ui.toolbar.setNavigationIcon(R.drawable.ic_back_white);

        ui.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ButtonClickBack();
            }
        });
    }
}