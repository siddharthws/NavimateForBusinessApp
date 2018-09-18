package com.biz.navimate.views.custom;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.biz.navimate.R;
import com.biz.navimate.activities.BaseActivity;
import com.biz.navimate.activities.ViewPhotoActivity;
import com.biz.navimate.application.App;
import com.biz.navimate.debug.Dbg;
import com.biz.navimate.interfaces.IfaceServer;
import com.biz.navimate.objects.Statics;
import com.biz.navimate.server.GetFileTask;
import com.biz.navimate.viewholders.CustomViewHolder;

public class PhotoThumbnailView     extends     RelativeLayout
                                    implements  View.OnClickListener,
                                                IfaceServer.GetFile {
    // ----------------------- Constants ----------------------- //
    private static final String TAG = "PHOTO_THUMBNAIL_VIEW";

    // ----------------------- Interfaces ----------------------- //
    // ----------------------- Globals ----------------------- //
    private Context ctx = null;
    private CustomViewHolder.PhotoThumbnail ui = new CustomViewHolder.PhotoThumbnail();

    private String filename = "";
    private Bitmap bitmap = null;

    private GetFileTask fileTask = null;

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
    public void onClick(View view) {
        // Launch Photo Viewer
        LaunchPhotoViewer();
    }

    @Override
    public void onFileSuccess() {
        // Show image only
        ui.ibPhoto.setVisibility(VISIBLE);
        ui.pbWaiting.setVisibility(GONE);
        ui.tvStatus.setVisibility(GONE);

        // Recycle Bitmap
        if (bitmap != null) { bitmap.recycle(); }

        // Set bitmap from file
        String absolutePath = Statics.GetAbsolutePath(getContext(), filename, Environment.DIRECTORY_PICTURES);
        bitmap = BitmapFactory.decodeFile(absolutePath);
        ui.ibPhoto.setImageBitmap(bitmap);
    }

    @Override
    public void onFileFailed() {
        // Show status text view only
        ui.tvStatus.setVisibility(VISIBLE);
        ui.pbWaiting.setVisibility(GONE);
        ui.ibPhoto.setVisibility(GONE);

        // Show error
        ui.tvStatus.setText("Not available...");
    }

    // ----------------------- Public APIs ----------------------- //
    public void Set(String filename) {
        // Cache filename
        this.filename = filename;

        // Update Text
        RefreshUI();
    }

    public String Get() {
        return filename;
    }

    // ----------------------- Private APIs ----------------------- //
    // Method to initialize view
    private void Init(Context ctx, AttributeSet a) {
        this.ctx = ctx;

        // Inflate Layout
        LayoutInflater inflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.custom_photo_thumbnail, this, true);

        // Init UI
        ui.tvStatus     = (TextView)        findViewById(R.id.tv_status);
        ui.pbWaiting    = (ProgressBar)     findViewById(R.id.pb_waiting);
        ui.ibPhoto      = (NvmImageButton)  findViewById(R.id.ib_photo);

        // Set listeners
        ui.ibPhoto.setOnClickListener(this);
    }

    // Method to refresh UI components
    private void RefreshUI() {
        if (filename.length() == 0) {
            // Show blank status
            ui.tvStatus.setVisibility(VISIBLE);
            ui.pbWaiting.setVisibility(GONE);
            ui.ibPhoto.setVisibility(GONE);

            ui.tvStatus.setText("Not set...");
        } else {
            // Show waiting sttaus
            ui.pbWaiting.setVisibility(VISIBLE);
            ui.tvStatus.setVisibility(GONE);
            ui.ibPhoto.setVisibility(GONE);

            // Try getting file using task
            fileTask = new GetFileTask(ctx, Environment.DIRECTORY_PICTURES, filename);
            fileTask.SetListener(this);
            fileTask.execute();
        }
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
