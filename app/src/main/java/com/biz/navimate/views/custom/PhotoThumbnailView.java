package com.biz.navimate.views.custom;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.biz.navimate.activities.BaseActivity;
import com.biz.navimate.activities.ViewPhotoActivity;
import com.biz.navimate.application.App;
import com.biz.navimate.debug.Dbg;
import com.biz.navimate.objects.Statics;

import java.io.File;

public class PhotoThumbnailView extends NvmImageButton implements View.OnClickListener {
    // ----------------------- Constants ----------------------- //
    private static final String TAG = "NVM_IMAGE_BUTTON";

    // ----------------------- Interfaces ----------------------- //
    // ----------------------- Globals ----------------------- //
    private String filename = "";
    private Bitmap bitmap = null;

    // ----------------------- Constructors ----------------------- //
    public PhotoThumbnailView(Context context) {
        super(context);
        Init(context, null);
    }

    public PhotoThumbnailView(Context context, AttributeSet attrs) {
        super(context, attrs);
        Init(context, attrs);
    }

    public PhotoThumbnailView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        Init(context, attrs);
    }

    // ----------------------- Overrides ----------------------- //
    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();

        // Set height / width
        ViewGroup.LayoutParams lp = getLayoutParams();
        lp.height = Statics.GetPxFromDip(100);
        lp.width = Statics.GetPxFromDip(100);
        setLayoutParams(lp);
    }

    @Override
    public void onClick(View view) {
        // Launch Photo Viewer
        LaunchPhotoViewer();
    }

    // ----------------------- Public APIs ----------------------- //
    public void Set(String filename) {
        // Get file object and validate
        String absolutePath = Statics.GetAbsolutePath(getContext(), filename, Environment.DIRECTORY_PICTURES);
        File imageFile = new File(absolutePath);
        if (!imageFile.exists()) {
            Dbg.error(TAG, "Photo file not found...");
            return;
        }

        // Cache filename
        this.filename = filename;

        // Set bitmap from file
        if (bitmap != null) { bitmap.recycle(); }
        bitmap = BitmapFactory.decodeFile(absolutePath);
        setImageBitmap(bitmap);
    }

    public String Get() {
        return filename;
    }

    // ----------------------- Private APIs ----------------------- //
    // Method to initialize view
    private void Init(Context ctx, AttributeSet a) {
        // Set Scaletype to fit center
        setScaleType(ScaleType.FIT_CENTER);

        // set click listener
        setOnClickListener(this);
    }

    // Method to launch photo viewer activity
    private void LaunchPhotoViewer() {
        // Get current activity
        BaseActivity currentActivity = App.GetCurrentActivity();
        if (currentActivity == null) {
            Dbg.Toast(getContext(), "Could not launch photo viewer...", Toast.LENGTH_SHORT);
            return;
        }

        // Trigger Photo Editor Activity
        ViewPhotoActivity.Start(currentActivity, filename);
    }
}
