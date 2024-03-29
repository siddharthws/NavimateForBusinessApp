package com.biz.navimate.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.PermissionChecker;
import android.support.v7.app.AppCompatActivity;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import com.biz.navimate.R;
import com.biz.navimate.application.App;
import com.biz.navimate.constants.Constants;
import com.biz.navimate.debug.Dbg;
import com.biz.navimate.interfaces.IfacePermission;
import com.biz.navimate.interfaces.IfaceResult;
import com.biz.navimate.objects.ObjPlace;
import com.biz.navimate.objects.Statics;
import com.biz.navimate.objects.core.ObjNvmCompact;
import com.biz.navimate.viewholders.ActivityHolder;
import com.biz.navimate.views.RlDialog;
import com.biz.navimate.views.compound.NvmToolbar;
import com.biz.navimate.zxing.IntentIntegrator;
import com.biz.navimate.zxing.IntentResult;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;

import java.io.File;

/**
 * Created by Siddharth on 22-09-2017.
 */

public abstract class BaseActivity  extends     AppCompatActivity
                                    implements  Runnable,
                                                NvmToolbar.IfaceToolbar {
    // ----------------------- Constants ----------------------- //
    private static final String TAG = "BASE_ACTIVITY";

    // ----------------------- Classes ---------------------------//
    // ----------------------- Interfaces ----------------------- //
    // ----------------------- Globals ----------------------- //
    // View Holder
    protected ActivityHolder.Base holder     = null;

    // permission related initData
    private boolean bResumeFromPermission = false;
    private String[] permissions = null;
    private int[] grantResults = null;

    // Permission Listeners
    private IfacePermission.Location   locationPermissionListener  = null;
    private IfacePermission.Sms        smsPermissionListener       = null;
    private IfacePermission.Call       callPermissionListener      = null;

    // Activity result related initData
    private int resumeRequestCode = 0, resumeResultCode = 0;
    private Intent resumeResultIntent = null;

    // Result Listeners
    private IfaceResult.Registration  registerListener          = null;
    private IfaceResult.ResultGps     gpsListener               = null;
    private IfaceResult.LeadPicker    leadPickerListener        = null;
    private IfaceResult.Zxing         zxingListener             = null;
    private IfaceResult.Photo         photoListener             = null;
    private IfaceResult.Crop          photoCropListener         = null;
    private IfaceResult.PhotoEditor   photoEditorListener       = null;
    private IfaceResult.Signature     signListener              = null;
    private IfaceResult.PhotoDraw     photoDrawlistener         = null;
    private IfaceResult.FilePicker    filePickerListener        = null;
    private IfaceResult.PlacePicker   placePickerListener       = null;
    private IfaceResult.ObjectPicker  objPickerListener       = null;

    // Periodic callback handler
    private Handler refreshHandler = null;

    // ----------------------- Constructor ----------------------- //
    // ----------------------- Abstracts ----------------------- //
    // Manadatory overrides to initialize activitiy views
    protected abstract void InflateLayout();
    protected abstract void FindViews();
    protected abstract void SetViews();

    // Optional Overrides
    protected void Refresh(){};

    // ----------------------- Overrides ----------------------- //
    // Lifecycle overrides
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        // Call super
        super.onCreate(savedInstanceState);

        // Load App if not initialized
        if (!App.IsInitialized()) {
            // Start app loading activity
            AppLoadActivity.Start(this);

            // Finish this activity
            finish();

            // Stop execution
            return;
        }

        // Inflate Layout
        InflateLayout();

        // Find Views
        FindViews();

        // Find Toolbar
        holder.toolbar       = (NvmToolbar) findViewById(R.id.nvm_toolbar);
        if (holder.toolbar != null) {
            holder.toolbar.SetListener(this);
        }

        // Set Dialog View to this activity's layout
        holder.rlDialog       = (RlDialog) findViewById(R.id.rl_dialog);
        RlDialog.Set(holder.rlDialog);

        // Init View Data
        SetViews();

        // Init refresh callback hanlder
        refreshHandler = new Handler();
    }

    @Override
    public void onRestart()
    {
        // Call Super
        super.onRestart();
    }

    @Override
    public void onStart()
    {
        // Call Super
        super.onStart();

        // Set Dialog View to this activity's layotu
        RlDialog.Set(holder.rlDialog);
    }

    @Override
    public void onResume()
    {
        // Call Super
        super.onResume();

        // Set this to current activity
        App.SetCurrentActivity(this);

        // Set Dialog View to this activity's layotu
        RlDialog.Set(holder.rlDialog);

        // Check for permission resume
        if (bResumeFromPermission)
        {
            // Call current listeners
            CallPermissionListeners();

            // Reset initData
            bResumeFromPermission = false;
            grantResults = null;
            permissions = null;
        }
        // Check for result resume
        else if (resumeRequestCode != Constants.RequestCodes.INVALID)
        {
            // Call current listeners
            CallResultListeners();

            // Reset initData
            this.resumeRequestCode = Constants.RequestCodes.REGISTRATION;
            this.resumeResultCode = 0;
            this.resumeResultIntent = null;
        }

        // Start periodic callbacks
        refreshHandler.post(this);
    }

    @Override
    public void onPause()
    {
        // Stop periodic updates
        refreshHandler.removeCallbacks(this);

        // Clear this as current activity
        App.ClearCurrentActivity(this);

        // Call Super
        super.onPause();
    }

    @Override
    public void onStop()
    {
        // Call Super
        super.onStop();
    }

    @Override
    public void onDestroy()
    {
        // Call Super
        super.onDestroy();
    }

    @Override
    public void onNewIntent(Intent intent)
    {
        super.onNewIntent(intent);
        setIntent(intent);
    }

    @Override
    public void onBackPressed()
    {
        if (holder.rlDialog.IsShowing())
        {
            RlDialog.Hide();
        }
        else
        {
            super.onBackPressed();
        }
    }

    // Result overrides
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        // Set resume data
        this.resumeRequestCode = requestCode;
        this.resumeResultCode = resultCode;
        this.resumeResultIntent = data;

        // Call Super
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults)
    {
        // Set resume initData
        this.permissions = permissions;
        this.grantResults = grantResults;
        bResumeFromPermission = true;

        // Call Super
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    // Toolbar button click overrides
    @Override
    public void onToolbarButtonClick(int id) {
        switch (id) {
            case R.id.ib_tb_back:
                onBackPressed();
                break;
        }
    }

    // Activity Refresh Runnable
    @Override
    public void run() {
        // Trigger refresh callback
        Refresh();

        // Schedule next callback
        refreshHandler.postDelayed(this, Constants.Date.TIME_5_SEC);
    }

    // ----------------------- Public APIs ----------------------- //
    // General APi to start any activity with custom params
    public static void Start(BaseActivity activity, Class<?> cls, int flags, Bundle extras, int requestCode, String action)
    {
        // SetAlarm Intent
        Intent intent = new Intent(activity, cls);

        // Set Extras
        if (extras != null)
        {
            intent.putExtras(extras);
        }

        // Set Action
        if (action != null)
        {
            intent.setAction(action);
        }

        // Set Flags
        if (flags != -1)
        {
            intent.setFlags(flags);
        }

        // Launch Activity
        if (requestCode == Constants.RequestCodes.INVALID)
        {
            // No result expected
            activity.startActivity(intent);
        }
        else
        {
            // Result expected
            activity.startActivityForResult(intent, requestCode);
        }
    }

    // APIs to set event listeners
    public void SetRegistrationResultListener(IfaceResult.Registration listener)
    {
        this.registerListener = listener;
    }

    public void SetGpsResultListener(IfaceResult.ResultGps listener)
    {
        this.gpsListener = listener;
    }

    public void SetLeadPickerResultListener(IfaceResult.LeadPicker listener)
    {
        this.leadPickerListener = listener;
    }

    public void SetZxingResultListener(IfaceResult.Zxing listener)
    {
        this.zxingListener = listener;
    }

    public void SetPhotoResultListener(IfaceResult.Photo listener)
    {
        this.photoListener = listener;
    }

    public void SetPhotoCropListener(IfaceResult.Crop listener)
    {
        this.photoCropListener = listener;
    }

    public void SetPhotoEditorListener(IfaceResult.PhotoEditor listener)
    {
        this.photoEditorListener = listener;
    }

    public void SetPhotoDrawListener(IfaceResult.PhotoDraw listener)
    {
        this.photoDrawlistener = listener;
    }

    public void SetSignResultListener(IfaceResult.Signature listener)
    {
        this.signListener = listener;
    }

    public void SetFilePickerResultListener(IfaceResult.FilePicker listener)
    {
        this.filePickerListener = listener;
    }

    public void SetPlacePickerResultListener(IfaceResult.PlacePicker listener)
    {
        this.placePickerListener = listener;
    }

    public void SetObjectPickerResultListener(IfaceResult.ObjectPicker listener)
    {
        this.objPickerListener = listener;
    }

    public void SetLocationPermissionListener(IfacePermission.Location listener)
    {
        this.locationPermissionListener = listener;
    }

    public void SetSmsPermissionListener(IfacePermission.Sms listener)
    {
        this.smsPermissionListener = listener;
    }

    public void SetCallPermissionListener(IfacePermission.Call listener)
    {
        this.callPermissionListener = listener;
    }

    // API to Request permission
    public void RequestPermission(String[] permissions)
    {
        ActivityCompat.requestPermissions(this, permissions, 0);
    }

    // ----------------------- Private APIs ----------------------- //
    // API to check the activity result type and call listener
    private void CallResultListeners()
    {
        switch (resumeRequestCode)
        {
            case Constants.RequestCodes.REGISTRATION:
            {
                if (registerListener != null)
                {
                    if (resumeResultCode == Activity.RESULT_OK)
                    {
                        registerListener.onRegisterSuccess();
                    }
                    else
                    {
                        registerListener.onRegisterFailure();
                    }
                }
                break;
            }
            case Constants.RequestCodes.GPS:
            {
                if (gpsListener != null)
                {
                    if (resumeResultCode == Activity.RESULT_OK)
                    {
                        gpsListener.onGpsEnableSuccess();
                    }
                    else
                    {
                        gpsListener.onGpsEnableFailure();
                    }
                }
                break;
            }
            case Constants.RequestCodes.LEAD_PICKER:
            {
                if (leadPickerListener != null)
                {
                    if (resumeResultCode == Activity.RESULT_OK)
                    {
                        // Get Compact Nvm Object from extras
                        ObjNvmCompact obj = (ObjNvmCompact) resumeResultIntent.getExtras().getSerializable(Constants.Extras.PICKED_OBJECT);

                        // Trigger listener
                        leadPickerListener.onLeadPicked(obj);
                    }
                }
                break;
            }
            case Constants.RequestCodes.ZXING:
            {
                if (zxingListener != null)
                {
                    if (resumeResultCode == Activity.RESULT_OK)
                    {
                        IntentResult scanningResult = IntentIntegrator.parseActivityResult(resumeRequestCode, resumeResultCode, resumeResultIntent);
                        if (scanningResult != null) {
                            String data = scanningResult.getContents();
                            zxingListener.onScanResult(data);
                        }
                    }
                }
                break;
            }
            case Constants.RequestCodes.PHOTO:
            {
                if (photoListener != null)
                {
                    if (resumeResultCode == Activity.RESULT_OK)
                    {
                        photoListener.onPhotoResult();
                    }
                }
                break;
            }
            case Constants.RequestCodes.PHOTO_CROP:
            {
                if (photoCropListener != null)
                {
                    if (resumeResultCode == Activity.RESULT_OK)
                    {
                        photoCropListener.onPhotoCrop();
                    }
                }
                break;
            }
            case Constants.RequestCodes.PHOTO_EDITOR:
            {
                if (photoEditorListener != null)
                {
                    if (resumeResultCode == Activity.RESULT_OK)
                    {
                        Bundle extras = resumeResultIntent.getExtras();
                        if (extras != null) {
                            String imagePath = extras.getString(Constants.Extras.IMAGE_NAME);
                            photoEditorListener.onPhotoEditorResult(imagePath);
                        }
                    }
                }
                break;
            }
            case Constants.RequestCodes.SIGNATURE:
            {
                if (signListener != null)
                {
                    if (resumeResultCode == Activity.RESULT_OK)
                    {
                        Bundle extras = resumeResultIntent.getExtras();
                        if (extras != null) {
                            String imagePath = extras.getString(Constants.Extras.SIGNATURE_IMAGE_PATH);
                            signListener.onSignatureResult(imagePath);
                        }
                    }
                }
                break;
            }
            case Constants.RequestCodes.PHOTO_DRAW:
            {
                if (photoDrawlistener != null)
                {
                    if (resumeResultCode == Activity.RESULT_OK)
                    {
                        Bundle extras = resumeResultIntent.getExtras();
                        if (extras != null) {
                            String imageName = extras.getString(Constants.Extras.IMAGE_NAME);
                            photoDrawlistener.onPhotoDraw(imageName);
                        }
                    }
                }
                break;
            }
            case Constants.RequestCodes.FILE_PICKER:
            {
                if (filePickerListener != null)
                {
                    if (resumeResultCode == Activity.RESULT_OK)
                    {
                        // Get file extension from URI
                        Uri fileUri = resumeResultIntent.getData();
                        String mimetype = getContentResolver().getType(fileUri);
                        String extension = MimeTypeMap.getSingleton().getExtensionFromMimeType(mimetype);

                        // Create a temp file with selected extension
                        File file = Statics.CreateDocument(this, extension);

                        // Fill file using input stream from content resolver
                        Statics.CopyFileFromUri(this, file, fileUri);

                        // Check if file size is greater than 2 MB
                        if (file.length() > 2000000) {
                            Dbg.Toast(this, "File is larger than 2 MB", Toast.LENGTH_SHORT);
                            file.delete();
                        } else {
                            // Trigger file picker listener
                            filePickerListener.onFilePicked(file.getName());
                        }
                    }
                }
                break;
            }
            case Constants.RequestCodes.PLACE_PICKER:
            {
                if (placePickerListener != null)
                {
                    if (resumeResultCode == Activity.RESULT_OK)
                    {
                        Place place = PlacePicker.getPlace(this, resumeResultIntent);
                        ObjPlace objPlace = new ObjPlace(   place.getLatLng().latitude,
                                                            place.getLatLng().longitude,
                                                            place.getAddress().toString());
                        placePickerListener.onPlacePicked(objPlace);
                    }
                }
                break;
            }
            case Constants.RequestCodes.OBJECT_PICKER:
            {
                if (objPickerListener != null)
                {
                    if (resumeResultCode == Activity.RESULT_OK)
                    {
                        // Get Compact Nvm Object from extras
                        ObjNvmCompact obj = (ObjNvmCompact) resumeResultIntent.getExtras().getSerializable(Constants.Extras.PICKED_OBJECT);

                        // Trigger listener
                        objPickerListener.onObjectPicked(obj);
                    }
                }
                break;
            }
        }
    }

    private void CallPermissionListeners()
    {
        if ((permissions == null) || (grantResults == null))
        {
            return;
        }

        // Check result and call appropraite listener
        for (int i = 0; i < permissions.length; i++)
        {
            if (permissions[i].equals(Manifest.permission.ACCESS_FINE_LOCATION))
            {
                if (locationPermissionListener != null)
                {
                    if (grantResults[i] == PermissionChecker.PERMISSION_GRANTED)
                    {
                        locationPermissionListener.onLocationPermissionSuccess();
                    }
                    else
                    {
                        locationPermissionListener.onLocationPermissionFailure();
                    }
                }
            }
            else if (permissions[i].equals(Manifest.permission.RECEIVE_SMS))
            {
                if (smsPermissionListener != null)
                {
                    if (grantResults[i] == PermissionChecker.PERMISSION_GRANTED)
                    {
                        smsPermissionListener.onSmsPermissionSuccess();
                    }
                    else
                    {
                        smsPermissionListener.onSmsPermissionFailure();
                    }
                }
            }
            else if (permissions[i].equals(Manifest.permission.CALL_PHONE))
            {
                if (callPermissionListener != null)
                {
                    if (grantResults[i] == PermissionChecker.PERMISSION_GRANTED)
                    {
                        callPermissionListener.onCallPermissionSuccess();
                    }
                    else
                    {
                        callPermissionListener.onCallPermissionFailure();
                    }
                }
            }
        }
    }
}
