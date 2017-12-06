package com.biz.navimate.dialogs;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.biz.navimate.R;
import com.biz.navimate.constants.Constants;
import com.biz.navimate.debug.Dbg;
import com.biz.navimate.interfaces.IfaceServer;
import com.biz.navimate.misc.LocationCache;
import com.biz.navimate.misc.LocationUpdateHelper;
import com.biz.navimate.objects.Dialog;
import com.biz.navimate.objects.Form;
import com.biz.navimate.objects.FormField;
import com.biz.navimate.objects.LocationObj;
import com.biz.navimate.runnables.LocationUpdateRunnable;
import com.biz.navimate.server.SubmitFormTask;
import com.biz.navimate.server.UploadPhotoTask;
import com.biz.navimate.viewholders.DialogHolder;
import com.biz.navimate.views.RlDialog;
import com.biz.navimate.views.RlFormField;
import com.google.android.gms.maps.model.LatLng;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Siddharth on 29-09-2017.
 */

public class SubmitFormDialog   extends     BaseDialog
                                implements  View.OnClickListener, IfaceServer.UploadPhoto,
                                            LocationUpdateRunnable.IfaceRunnableLocationInit {
    // ----------------------- Constants ----------------------- //
    private static final String TAG = "SUBMIT_FORM_DIALOG";

    // ----------------------- Classes ---------------------------//
    private class ImageUpload {
        public String path;
        public String waitingText;
        public FormField.Base field;

        public ImageUpload(String path, String waitingText, FormField.Base field) {
            this.path = path;
            this.waitingText = waitingText;
            this.field = field;
        }
    }

    // ----------------------- Interfaces ----------------------- //
    // ----------------------- Globals ----------------------- //
    private DialogHolder.SubmitForm ui = null;
    private Form template = null;
    private Form submitData = null;
    private LatLng submitLocation = null;
    private ArrayList<ImageUpload> imageUploads = null;
    private int imageUploadIndex = 0;
    private LocationUpdateRunnable locUpdateRunnable = null;

    // ----------------------- Constructor ----------------------- //
    public SubmitFormDialog(Context context)
    {
        super(context);
        locUpdateRunnable = new LocationUpdateRunnable(context);
        locUpdateRunnable.SetInitListener(this);
    }

    // ----------------------- Overrides ----------------------- //
    @Override
    protected void SetViewHolder(LayoutInflater inflater, ViewGroup container)
    {
        // Init Holder
        ui = new DialogHolder.SubmitForm();
        holder = ui;

        // Inflate View
        ui.dialogView = inflater.inflate(R.layout.dialog_submit_form, container);

        // Find Views
        ui.llFields      = (LinearLayout)   ui.dialogView.findViewById(R.id.ll_fields);
        ui.fields       = new ArrayList<>();
        ui.cbCloseTask  = (CheckBox)   ui.dialogView.findViewById(R.id.cbCloseTask);
        ui.btnSubmit    = (Button)     ui.dialogView.findViewById(R.id.btn_submit);
        ui.btnCancel    = (Button)     ui.dialogView.findViewById(R.id.btn_cancel);
    }

    @Override
    protected void SetContentView()
    {
        // Get current data
        Dialog.SubmitForm currentData = (Dialog.SubmitForm) data;

        // init form template
        template = currentData.form;

        // Set Fields
        for (FormField.Base field : template.fields) {
            RlFormField fieldUi = new RlFormField(context, field);
            ui.llFields.addView(fieldUi);
            ui.fields.add(fieldUi);
        }
        ui.cbCloseTask.setChecked(currentData.bCloseTask);

        // Set Listeners
        ui.btnSubmit.setOnClickListener(this);
        ui.btnCancel.setOnClickListener(this);
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.btn_submit:
            {
                ButtonClickSubmit();
                break;
            }
            case R.id.btn_cancel:
            {
                // Hide this dialog box
                RlDialog.Hide();
                break;
            }
        }
    }
    // ----------------------- Public APIs ----------------------- //
    // ----------------------- Private APIs ----------------------- //

    private void ButtonClickSubmit(){
        // Get field data
        imageUploads = new ArrayList<>();
        imageUploadIndex = 0;
        Dialog.SubmitForm currentData = (Dialog.SubmitForm) data;
        ArrayList<FormField.Base> fields = new ArrayList<>();
        for (RlFormField rlField : ui.fields) {
            // Add to fields array
            FormField.Base field = rlField.GetField();
            fields.add(field);

            // Check for images
            if (field.type.equals(FormField.TYPE_PHOTO)) {
                FormField.Photo photoField = (FormField.Photo) field;

                if ((photoField.imagePath.length() > 0) && (new File(photoField.imagePath).exists())) {
                    imageUploads.add(new ImageUpload(   photoField.imagePath,
                                                        "Uploading " + photoField.title + "...",
                                                        photoField));
                }
            } else if (field.type.equals(FormField.TYPE_SIGNATURE)) {
                FormField.Signature signField = (FormField.Signature) field;

                if ((signField.imagePath.length() > 0) && (new File(signField.imagePath).exists())) {
                    imageUploads.add(new ImageUpload(   signField.imagePath,
                                                        "Uploading " + signField.title + "...",
                                                        signField));
                }
            }
        }

        // Update Form Object
        submitData = new Form(Constants.Misc.ID_INVALID, Constants.Misc.ID_INVALID, template.name, fields);

        // Check for current location
        if (new LocationUpdateHelper(context).IsUpdating()) {
            onLocationInitSuccess(LocationCache.instance.GetLocation());
        } else {
            locUpdateRunnable.Post(0);
        }
    }

    @Override
    public void onLocationInitSuccess(LocationObj location) {
        submitLocation = location.latlng;

        if (imageUploads.size() > 0) {
            UploadImages();
        } else {
            SubmitForm();
        }
    }

    @Override
    public void onLocationInitError() {
        submitLocation = new LatLng(0, 0);

        if (imageUploads.size() > 0) {
            UploadImages();
        } else {
            SubmitForm();
        }
    }

    private void SubmitForm() {
        Dialog.SubmitForm currentData = (Dialog.SubmitForm) data;
        boolean bCloseTask = ui.cbCloseTask.isChecked();
        SubmitFormTask submitTask = new SubmitFormTask(context, submitData, currentData.taskId, submitLocation, bCloseTask);
        submitTask.execute();
    }

    private void UploadImages() {
        ImageUpload data = imageUploads.get(imageUploadIndex);
        UploadPhotoTask uploadPhotoTask = new UploadPhotoTask(context, data.path, data.waitingText);
        uploadPhotoTask.SetListener(this);
        uploadPhotoTask.execute();
    }

    @Override
    public void onPhotoUploaded(String filename) {
        // Update filename in field
        ImageUpload data = imageUploads.get(imageUploadIndex);
        if (data.field.type.equals(FormField.TYPE_PHOTO)) {
            FormField.Photo photoField = (FormField.Photo) data.field;
            photoField.filename = filename;
        } else if (data.field.type.equals(FormField.TYPE_SIGNATURE)) {
            FormField.Signature signField = (FormField.Signature) data.field;
            signField.filename = filename;
        }

        // check whether image upload has completed or needs to continue
        imageUploadIndex++;
        if (imageUploads.size() > imageUploadIndex) {
            UploadImages();
        } else {
            SubmitForm();
        }
    }

    @Override
    public void onPhotoUploadFailed() {
        // Show error toast for the field
        ImageUpload data = imageUploads.get(imageUploadIndex);
        Dbg.Toast(context, data.field.title + " could not be uploaded...", Toast.LENGTH_SHORT);

        // check whether image upload has completed or needs to continue
        imageUploadIndex++;
        if (imageUploads.size() > imageUploadIndex) {
            UploadImages();
        } else {
            SubmitForm();
        }
    }
}
