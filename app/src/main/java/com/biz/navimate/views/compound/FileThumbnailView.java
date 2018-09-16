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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.biz.navimate.R;
import com.biz.navimate.activities.BaseActivity;
import com.biz.navimate.application.App;
import com.biz.navimate.debug.Dbg;
import com.biz.navimate.objects.Statics;
import com.biz.navimate.viewholders.CustomViewHolder;

import java.io.File;

public class FileThumbnailView      extends     RelativeLayout
                                    implements  View.OnClickListener {
    // ----------------------- Constants ----------------------- //
    private static final String TAG = "FILE_THUMBNAIL_VIEW";

    // ----------------------- Interfaces ----------------------- //
    // ----------------------- Globals ----------------------- //
    private Context ctx = null;
    public CustomViewHolder.FileThumbnail ui = new CustomViewHolder.FileThumbnail();

    private String filename = "";

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

    // ----------------------- Public APIs ----------------------- //
    // Method to set file
    public void Set(String filename) {
        // Get file object and validate
        String absolutePath = Statics.GetAbsolutePath(getContext(), filename, Environment.DIRECTORY_DOCUMENTS);
        File file = new File(absolutePath);
        if (!file.exists()) {
            Dbg.error(TAG, "File not found...");
            return;
        }

        // Cache filename
        this.filename = filename;

        // Set filetype text
        ui.tvFiletype.setText(getExt(absolutePath).toUpperCase());
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
        ui.tvFiletype      = (TextView)        findViewById(R.id.tv_filetype);

        // Set listeners
        setOnClickListener(this);
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
            Dbg.error(TAG, "File not found...");
            return;
        }

        // get mimetype & URI of file
        String ext = getExt(absolutePath);
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

    private String getExt(String absPath) {
        if (absPath.indexOf("?") > -1) {
            absPath = absPath.substring(0, absPath.indexOf("?"));
        }
        if (absPath.lastIndexOf(".") == -1) {
            return null;
        } else {
            String ext = absPath.substring(absPath.lastIndexOf(".") + 1);
            if (ext.indexOf("%") > -1) {
                ext = ext.substring(0, ext.indexOf("%"));
            }
            if (ext.indexOf("/") > -1) {
                ext = ext.substring(0, ext.indexOf("/"));
            }
            return ext.toLowerCase();
        }
    }
}
