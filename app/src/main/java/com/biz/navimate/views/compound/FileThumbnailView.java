package com.biz.navimate.views.compound;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.content.FileProvider;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.biz.navimate.R;
import com.biz.navimate.activities.BaseActivity;
import com.biz.navimate.application.App;
import com.biz.navimate.debug.Dbg;
import com.biz.navimate.interfaces.IfaceServer;
import com.biz.navimate.objects.Statics;
import com.biz.navimate.server.GetFileTask;
import com.biz.navimate.viewholders.CustomViewHolder;

import java.io.File;

public class FileThumbnailView      extends     RelativeLayout
                                    implements  View.OnClickListener,
                                                IfaceServer.GetFile {
    // ----------------------- Constants ----------------------- //
    private static final String TAG = "FILE_THUMBNAIL_VIEW";

    // ----------------------- Interfaces ----------------------- //
    // ----------------------- Globals ----------------------- //
    private Context ctx = null;
    public CustomViewHolder.FileThumbnail ui = new CustomViewHolder.FileThumbnail();

    private String filename = "";

    private GetFileTask fileTask = null;

    // ----------------------- Constructors ----------------------- //
    public FileThumbnailView(Context context) {
        super(context);
        Init(context, null);
    }

    public FileThumbnailView(Context context, AttributeSet attrs) {
        super(context, attrs);
        Init(context, attrs);
    }

    public FileThumbnailView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        Init(context, attrs);
    }

    // ----------------------- Overrides ----------------------- //
    @Override
    public void onClick(View view) {
        OpenFile();
    }

    @Override
    public void onFileSuccess() {
        // Show image only
        ui.rlIcon.setVisibility(VISIBLE);
        ui.pbWaiting.setVisibility(GONE);
        ui.tvStatus.setVisibility(GONE);

        // Ste filetype
        String absolutePath = Statics.GetAbsolutePath(getContext(), filename, Environment.DIRECTORY_DOCUMENTS);
        ui.tvFiletype.setText(Statics.GetFileExt(absolutePath).toUpperCase());
    }

    @Override
    public void onFileFailed() {
        // Show status text view only
        ui.tvStatus.setVisibility(VISIBLE);
        ui.pbWaiting.setVisibility(GONE);
        ui.rlIcon.setVisibility(GONE);

        // Show error
        ui.tvStatus.setText("Not available...");
    }

    // ----------------------- Public APIs ----------------------- //
    // Method to set file
    public void Set(String filename) {
        // Cache filename
        this.filename = filename;

        // Refresh UI
        RefreshUI();
    }

    public String Get() {
        return filename;
    }

    // ----------------------- Private APIs ----------------------- //
    // Method to initialize view
    private void Init(Context ctx, AttributeSet attrs) {
        this.ctx = ctx;

        // Inflate Layout
        LayoutInflater inflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.custom_file_thumbnail, this, true);

        // Init UI
        ui.tvFiletype       = (TextView)        findViewById(R.id.tv_filetype);
        ui.tvStatus         = (TextView)        findViewById(R.id.tv_status);
        ui.pbWaiting        = (ProgressBar)     findViewById(R.id.pb_waiting);
        ui.rlIcon           = (RelativeLayout)  findViewById(R.id.rl_icon_container);

        // Set listeners
        ui.rlIcon.setOnClickListener(this);
    }

    // Method to refresh UI components
    private void RefreshUI() {
        if (filename.length() == 0) {
            // Show blank status
            ui.tvStatus.setVisibility(VISIBLE);
            ui.pbWaiting.setVisibility(GONE);
            ui.rlIcon.setVisibility(GONE);

            ui.tvStatus.setText("Not set...");
        } else {
            // Show waiting sttaus
            ui.pbWaiting.setVisibility(VISIBLE);
            ui.tvStatus.setVisibility(GONE);
            ui.rlIcon.setVisibility(GONE);

            // Try getting file using task
            fileTask = new GetFileTask(ctx, Environment.DIRECTORY_DOCUMENTS, filename);
            fileTask.SetListener(this);
            fileTask.execute();
        }
    }

    // Method to open file on click
    private void OpenFile() {
        // Get current activity
        BaseActivity currentActivity = App.GetCurrentActivity();
        if (currentActivity == null) {
            Dbg.Toast(getContext(), "Could not launch file viewer...", Toast.LENGTH_SHORT);
            return;
        }

        // Get file object and validate
        String absolutePath = Statics.GetAbsolutePath(getContext(), filename, Environment.DIRECTORY_DOCUMENTS);
        File file = new File(absolutePath);
        if (!file.exists()) {
            Dbg.Toast(getContext(), "File not found...", Toast.LENGTH_SHORT);
            return;
        }

        // get mimetype & URI of file
        String ext = Statics.GetFileExt(absolutePath);
        String mimetype = MimeTypeMap.getSingleton().getMimeTypeFromExtension(ext);
        Uri uri = FileProvider.getUriForFile(getContext(), "com.biz.navimate.fileprovider", file);

        // Create File Viewer Intent
        Intent fileViewerIntent = new Intent(Intent.ACTION_VIEW);
        fileViewerIntent.setDataAndType(uri, mimetype);
        fileViewerIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        fileViewerIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        // Launch Activity
        currentActivity.startActivity(fileViewerIntent);
    }
}
