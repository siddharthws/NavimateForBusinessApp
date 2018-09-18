package com.biz.navimate.views.compound;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.biz.navimate.R;
import com.biz.navimate.activities.BaseActivity;
import com.biz.navimate.activities.PhotoEditorActivity;
import com.biz.navimate.application.App;
import com.biz.navimate.constants.Constants;
import com.biz.navimate.debug.Dbg;
import com.biz.navimate.interfaces.IfaceResult;
import com.biz.navimate.objects.Statics;
import com.biz.navimate.viewholders.CustomViewHolder;
import com.biz.navimate.views.custom.NvmImageButton;
import com.biz.navimate.views.custom.PhotoThumbnailView;

import java.io.File;

public class PhotoEditorView    extends     LinearLayout
                                implements  View.OnClickListener,
                                            IfaceResult.Photo,
                                            IfaceResult.PhotoEditor {
    // ----------------------- Constants ----------------------- //
    private static final String TAG = "PHOTO_EDITOR_VIEW";

    // ----------------------- Interfaces ----------------------- //
    public interface IfacePhotoEditor {
        void onPhotoChanged(String name);
    }
    private IfacePhotoEditor listener = null;
    public void SetListener(IfacePhotoEditor listener) {
        this.listener = listener;
    }

    // ----------------------- Globals ----------------------- //
    private Context ctx = null;

    private CustomViewHolder.PhotoEditor ui = new CustomViewHolder.PhotoEditor();
    private String filename = "";

    private File tempPhotoFile = null;

    private boolean bEditable = true;

    // ----------------------- Constructors ----------------------- //
    public PhotoEditorView(Context context) {
        super(context);
        Init(context, null);
    }

    public PhotoEditorView(Context context, AttributeSet attrs) {
        super(context, attrs);
        Init(context, attrs);
    }

    public PhotoEditorView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        Init(context, attrs);
    }

    // ----------------------- Overrides ----------------------- //
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ib_clear:
                // Reset current photo
                Set("");
                break;
            case R.id.ib_edit:
                // Start Camera to capture photo
                LaunchCamera();
                break;
        }
    }

    @Override
    public void onPhotoResult() {
        // Check if photo file exists
        if (tempPhotoFile == null || !tempPhotoFile.exists()) {
            Dbg.Toast(ctx, "Could not get photo from camera...", Toast.LENGTH_SHORT);
            return;
        }

        // Scale Image
        String scaledImageName = Statics.ScaleImageFile(getContext(), tempPhotoFile.getAbsolutePath());

        // Trigger Photo Editor
        LaunchPhotoEditor(scaledImageName);
    }

    @Override
    public void onPhotoEditorResult(String fileName) {
        Set(fileName);
    }

    // ----------------------- Public APIs ----------------------- //
    // Methods to set and get date
    public void Set(String name) {
        // Set cache
        this.filename = name;

        // Update Image
        ui.ptThumbnail.Set(filename);

        // Update Clear Icon visibility
        RefreshClear();
    }

    public String Get() {
        return filename;
    }

    // method to set the view as read only or in editing mode
    public void SetEditable(boolean bEditable) {
        this.bEditable = bEditable;

        // Show / Hide editing buttons
        if (bEditable) {
            ui.ibEdit.setVisibility(VISIBLE);
            RefreshClear();
        } else {
            ui.ibEdit.setVisibility(GONE);
            ui.ibClear.setVisibility(GONE);
        }
    }

    public boolean IsEditable() {
        return bEditable;
    }

    // ----------------------- Private APIs ----------------------- //
    private void RefreshClear() {
        if (filename.length() == 0) {
            ui.ibClear.setVisibility(GONE);
        } else {
            ui.ibClear.setVisibility(VISIBLE);
        }
    }

    // Methods to launch camera to capture photo
    private void LaunchCamera() {
        // Get current activity
        BaseActivity currentActivity = App.GetCurrentActivity();
        if (currentActivity == null) {
            Dbg.Toast(getContext(), "Could not launch camera...", Toast.LENGTH_SHORT);
            return;
        }

        // Ensure camera is present
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(currentActivity.getPackageManager()) == null) {
            Dbg.Toast(getContext(), "Camera App not found...", Toast.LENGTH_SHORT);
            return;
        }

        // Create the File where the photo should go
        tempPhotoFile = Statics.CreatePicture(getContext());
        if (tempPhotoFile == null) {
            Dbg.Toast(getContext(), "Cannot create photo in storage...", Toast.LENGTH_SHORT);
            return;
        }

        // Add target URI to camera intent
        Uri photoURI = FileProvider.getUriForFile(getContext(), "com.biz.navimate.fileprovider", tempPhotoFile);
        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);

        // Set result listener
        currentActivity.SetPhotoResultListener(this);

        // Trigger Camera intent
        currentActivity.startActivityForResult(takePictureIntent, Constants.RequestCodes.PHOTO);
    }

    private void LaunchPhotoEditor(String filename) {
        // Get current activity
        BaseActivity currentActivity = App.GetCurrentActivity();
        if (currentActivity == null) {
            Dbg.Toast(getContext(), "Could not launch photo editor...", Toast.LENGTH_SHORT);
            return;
        }

        // Set photo editor listener
        currentActivity.SetPhotoEditorListener(this);

        // Trigger Photo Editor Activity
        PhotoEditorActivity.Start(currentActivity, filename);
    }

    // Method to initialize view
    private void Init(Context ctx, AttributeSet attrs) {
        this.ctx = ctx;

        // Inflate Layout
        LayoutInflater inflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.custom_photo_editor, this, true);

        // Set UI properties
        SetUiAttributes();

        // Init UI
        ui.ptThumbnail  = (PhotoThumbnailView)  findViewById(R.id.pt_thumbnail);
        ui.ibClear      = (NvmImageButton)  findViewById(R.id.ib_clear);
        ui.ibEdit       = (NvmImageButton)  findViewById(R.id.ib_edit);

        // Set listeners
        ui.ibClear.setOnClickListener(this);
        ui.ibEdit.setOnClickListener(this);
    }

    // Method to set any extra attributes
    private void SetUiAttributes() {
        // Set Layout Params
        LinearLayout.LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER_VERTICAL;
        setLayoutParams(params);

        // Set orientation
        setOrientation(LinearLayout.HORIZONTAL);
    }
}
