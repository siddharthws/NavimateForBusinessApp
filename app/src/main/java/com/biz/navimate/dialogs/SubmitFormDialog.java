package com.biz.navimate.dialogs;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;

import com.biz.navimate.R;
import com.biz.navimate.database.DbHelper;
import com.biz.navimate.interfaces.IfaceServer;
import com.biz.navimate.objects.Dialog;
import com.biz.navimate.objects.Form;
import com.biz.navimate.objects.FormField;
import com.biz.navimate.server.SubmitFormTask;
import com.biz.navimate.server.UploadPhotoTask;
import com.biz.navimate.viewholders.DialogHolder;
import com.biz.navimate.views.RlDialog;
import com.biz.navimate.views.RlFormField;

import java.util.ArrayList;

/**
 * Created by Siddharth on 29-09-2017.
 */

public class SubmitFormDialog   extends     BaseDialog
                                implements  View.OnClickListener, IfaceServer.UploadPhoto {
    // ----------------------- Constants ----------------------- //
    private static final String TAG = "SUBMIT_FORM_DIALOG";

    // ----------------------- Classes ---------------------------//
    private class ImageUpload {
        public Bitmap image;
        public String waitingText;
        public FormField.Base field;

        public ImageUpload(Bitmap image, String waitingText, FormField.Base field) {
            this.image = image;
            this.waitingText = waitingText;
            this.field = field;
        }
    }

    // ----------------------- Interfaces ----------------------- //
    // ----------------------- Globals ----------------------- //
    private DialogHolder.SubmitForm ui = null;
    private ArrayList<ImageUpload> imageUploads = null;
    private int imageUploadIndex = 0;

    // ----------------------- Constructor ----------------------- //
    public SubmitFormDialog(Context context)
    {
        super(context);
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

        // Set Fields
        for (FormField.Base field : currentData.form.fields) {
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
            FormField.Base field = rlField.GetField();

            // Check for images
            if (field.type.equals(FormField.TYPE_PHOTO)) {
                FormField.Photo photoField = (FormField.Photo) field;

                if (photoField.photo.getByteCount() > 0) {
                    imageUploads.add(new ImageUpload(   photoField.photo,
                                                        "Uploading " + photoField.title + "...",
                                                        photoField));
                }
            } else if (field.type.equals(FormField.TYPE_SIGNATURE)) {
                FormField.Signature signField = (FormField.Signature) field;

                if (signField.signature.getByteCount() > 0) {
                    imageUploads.add(new ImageUpload(   signField.signature,
                                                        "Uploading " + signField.title + "...",
                                                        signField));
                }
            }

            // Add to fields array
            fields.add(rlField.GetField());
        }

        // Update Form Object
        currentData.form = new Form(currentData.form.name, fields);

        if (imageUploads.size() > 0) {
            UploadImages();
        } else {
            SubmitForm();
        }
    }

    private void SubmitForm() {
        Dialog.SubmitForm currentData = (Dialog.SubmitForm) data;
        boolean bCloseTask = ui.cbCloseTask.isChecked();
        SubmitFormTask submitTask = new SubmitFormTask(context, currentData.form, currentData.taskId, bCloseTask);
        submitTask.execute();
    }

    private void UploadImages() {
        ImageUpload data = imageUploads.get(imageUploadIndex);
        UploadPhotoTask uploadPhotoTask = new UploadPhotoTask(context, data.image, data.waitingText);
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

    }
}
