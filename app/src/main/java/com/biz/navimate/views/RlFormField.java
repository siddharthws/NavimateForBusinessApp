package com.biz.navimate.views;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.content.PermissionChecker;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.biz.navimate.R;
import com.biz.navimate.activities.BaseActivity;
import com.biz.navimate.activities.PhotoEditorActivity;
import com.biz.navimate.activities.SignatureActivity;
import com.biz.navimate.activities.ViewPhotoActivity;
import com.biz.navimate.application.App;
import com.biz.navimate.constants.Constants;
import com.biz.navimate.database.DbHelper;
import com.biz.navimate.debug.Dbg;
import com.biz.navimate.interfaces.IfacePermission;
import com.biz.navimate.interfaces.IfaceResult;
import com.biz.navimate.interfaces.IfaceServer;
import com.biz.navimate.objects.Dialog;
import com.biz.navimate.objects.FormEntry;
import com.biz.navimate.objects.ObjProduct;
import com.biz.navimate.objects.Statics;
import com.biz.navimate.server.GetProductTask;
import com.biz.navimate.zxing.IntentIntegrator;

/**
 * Created by Siddharth on 23-10-2017.
 */

public class RlFormField extends RelativeLayout implements IfacePermission.Call {
    // ----------------------- Constants ----------------------- //
    public static final String TAG = "RL_FORM_FIELD";

    // ----------------------- Classes ---------------------------//
    // ----------------------- Interfaces ----------------------- //
    // ----------------------- Globals ----------------------- //
    // UI
    private LinearLayout llText = null;
    private ImageButton ibQr, ibPhone = null;
    private TextView tvTitle;
    private EditText etNumber, etText = null;
    private CbCustom cbCheckbox = null;
    private TextView tvNumber, tvText = null;
    private RadioGroup rgRadioList = null;
    private LinearLayout llCheckList = null;
    private RelativeLayout rlPhoto = null, rlSignature = null, rlFile = null;
    private ImageView ivPhoto = null, ivSignature = null;
    private TextView tvPhoto = null, tvSignature = null, tvFile = null;
    private TextView tvDate = null;
    private DatePickerDialog dateDialog = null;
    private TimePickerDialog timeDialog = null;
    private Button btnProduct = null;

    private FormEntry.Base entry = null;
    private boolean bReadOnly = false;

    // ----------------------- Constructor ----------------------- //

    public RlFormField(Context context)
    {
        super(context);
        InitView(context);
    }

    public RlFormField(Context context, FormEntry.Base entry, boolean bReadOnly)
    {
        super(context);
        this.entry = entry;
        this.bReadOnly = bReadOnly;
        InitView(context);
    }

    public RlFormField(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        InitView(context);
    }

    public RlFormField(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        InitView(context);
    }

    // ----------------------- Overrides ----------------------- //
    @Override
    public void onCallPermissionSuccess() {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CALL_PHONE) == PermissionChecker.PERMISSION_GRANTED) {
            Intent intent = new Intent(Intent.ACTION_CALL);
            intent.setData(Uri.parse("tel:" + entry.toString()));
            getContext().startActivity(intent);
        }
    }

    @Override
    public void onCallPermissionFailure() {

    }

    // ----------------------- Public APIs ----------------------- //
    public String GetValue() {
        String value = "";

        switch (entry.field.type) {
            case Constants.Template.FIELD_TYPE_TEXT : {
                value = etText.getText().toString();
                break;
            }
            case Constants.Template.FIELD_TYPE_NUMBER : {
                value = etNumber.getText().toString();
                break;
            }
            case Constants.Template.FIELD_TYPE_CHECKBOX : {
                value = String.valueOf(cbCheckbox.isChecked());
                break;
            }
            case Constants.Template.FIELD_TYPE_PHOTO :
            case Constants.Template.FIELD_TYPE_FILE :
            case Constants.Template.FIELD_TYPE_SIGN :
            case Constants.Template.FIELD_TYPE_DATE :
            case Constants.Template.FIELD_TYPE_PRODUCT :    {
                value = entry.toString();
                break;
            }
            case Constants.Template.FIELD_TYPE_CHECKLIST : {
                // Get selection array
                ArrayList<Boolean> selection = new ArrayList<>();
                for (int i = 0; i < llCheckList.getChildCount(); i++) {
                    CbCustom cb = (CbCustom) llCheckList.getChildAt(i);
                    selection.add(cb.isChecked());
                }

                // Update entry and get string format
                ((FormEntry.CheckList) entry).selection = selection;
                value = entry.toString();
                break;
            }
            case Constants.Template.FIELD_TYPE_RADIOLIST : {
                // get selection index
                int selection = 0;
                for (int i = 0; i < rgRadioList.getChildCount(); i++) {
                    RbCustom rb = (RbCustom) rgRadioList.getChildAt(i);
                    if (rb.isChecked()) {
                        selection = i;
                        break;
                    }
                }

                // Update entry and get string format
                ((FormEntry.RadioList) entry).selection = selection;
                value = entry.toString();
                break;
            }
        }

        return value;
    }

    public FormEntry.Base GetEntry() {
        FormEntry.Base formEntry = FormEntry.Parse(entry.field, GetValue());
        return formEntry;
    }

    // ----------------------- Private APIs ----------------------- //
    private void InitView(Context context)
    {
        // Inflate Layout
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.rl_form_field, this, true);

        // Init UI
        llText = (LinearLayout) view.findViewById(R.id.ll_text);
        llCheckList = (LinearLayout) view.findViewById(R.id.ll_checkList);
        ibQr = (ImageButton) view.findViewById(R.id.ib_qr);
        ibPhone = (ImageButton) view.findViewById(R.id.ib_phone);
        tvTitle = (TextView) view.findViewById(R.id.tv_title);
        etNumber = (EditText) view.findViewById(R.id.et_number);
        etText = (EditText) view.findViewById(R.id.et_text);
        cbCheckbox = (CbCustom) view.findViewById(R.id.cb_checkbox);
        tvNumber = (TextView) view.findViewById(R.id.tv_number);
        tvText = (TextView) view.findViewById(R.id.tv_text);
        rgRadioList = (RadioGroup) view.findViewById(R.id.rg_radioList);
        rlPhoto = (RelativeLayout) view.findViewById(R.id.rl_photo);
        rlFile = (RelativeLayout) view.findViewById(R.id.rl_file);
        rlSignature = (RelativeLayout)  view.findViewById(R.id.rl_signature);
        ivPhoto = (ImageView) view.findViewById(R.id.iv_photo);
        ivSignature = (ImageView)       view.findViewById(R.id.iv_signature);
        tvPhoto = (TextView) view.findViewById(R.id.tv_photo);
        tvFile = (TextView) view.findViewById(R.id.tv_file);
        tvSignature = (TextView)       view.findViewById(R.id.tv_signature);
        tvDate = (TextView) view.findViewById(R.id.tv_date);
        btnProduct = (Button) view.findViewById(R.id.btn_product);

        // Populate title
        tvTitle.setText(entry.field.title);

        // Populate Value as per field type
        switch (entry.field.type) {
            case Constants.Template.FIELD_TYPE_TEXT : {
                InitTextField();
                break;
            }
            case Constants.Template.FIELD_TYPE_NUMBER : {
                InitNumberField();
                break;
            }
            case Constants.Template.FIELD_TYPE_DATE : {
                InitDateField();
                break;
            }
            case Constants.Template.FIELD_TYPE_CHECKBOX : {
                InitCheckboxField();
                break;
            }
            case Constants.Template.FIELD_TYPE_PHOTO : {
                InitPhotoField();
                break;
            }
            case Constants.Template.FIELD_TYPE_FILE : {
                InitFileField();
                break;
            }
            case Constants.Template.FIELD_TYPE_SIGN : {
                InitSignField();
                rlSignature.setVisibility(VISIBLE);
                break;
            }
            case Constants.Template.FIELD_TYPE_RADIOLIST : {
                InitRadioField();
                break;
            }
            case Constants.Template.FIELD_TYPE_CHECKLIST : {
                InitChecklistField();
                break;
            }
            case Constants.Template.FIELD_TYPE_PRODUCT : {
                InitProductField();
                break;
            }
        }

    }

    // APIs to init different form fields as per value
    private void InitTextField() {
        // Set Text UI
        if (bReadOnly) {
            // Disable QR scanning and edit text
            tvText.setVisibility(VISIBLE);
            tvText.setText(entry.toString());

            // Check for phone numbers
            if (entry.field.title.toLowerCase().contains("phone")) {
                ibPhone.setVisibility(VISIBLE);
                IfacePermission.Call callListener = this;
                ibPhone.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Check permission
                        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CALL_PHONE) == PermissionChecker.PERMISSION_GRANTED) {
                            onCallPermissionSuccess();
                        } else {
                            BaseActivity currentActivity = App.GetCurrentActivity();

                            if (currentActivity != null) {
                                currentActivity.SetCallPermissionListener(callListener);
                                currentActivity.RequestPermission(new String[]{Manifest.permission.CALL_PHONE});
                            } else {
                                Dbg.error(TAG, "Current Activity is null. Cannot ask permission");
                            }
                        }

                    }
                });
            }
        } else {
            llText.setVisibility(VISIBLE);
            etText.setText(entry.toString());

            // Set QR code reader
            ibQr.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    BaseActivity activity = App.GetCurrentActivity();
                    if (activity != null) {
                        activity.SetZxingResultListener(new IfaceResult.Zxing() {
                            @Override
                            public void onScanResult(String data) {
                                etText.setText(data);
                            }
                        });
                        IntentIntegrator scanIntegrator = new IntentIntegrator(activity);
                        scanIntegrator.initiateScan();
                    }
                }
            });
        }
    }

    private void InitNumberField() {
        // Set number UI vsiible
        if (bReadOnly) {
            tvNumber.setVisibility(VISIBLE);
            tvNumber.setText(entry.toString());
        } else {
            etNumber.setVisibility(VISIBLE);
            etNumber.setText(entry.toString());
        }
    }

    private void InitDateField() {
        // Set Date field visible
        tvDate.setVisibility(VISIBLE);

        // Set formatted date
        FormEntry.Date dateEntry = (FormEntry.Date) entry;
        if (dateEntry.cal != null) {
            SimpleDateFormat sdf = new SimpleDateFormat(Constants.Date.FORMAT_BACKEND);
            String date = sdf.format(dateEntry.cal.getTime());
            tvDate.setText(date);
        } else {
            tvDate.setText("Click here to set date");
        }

        // Set listener for picking date
        if (!bReadOnly) {
            tvDate.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Get current date / currently selected date
                    Calendar cal = Calendar.getInstance();
                    if (dateEntry.cal != null) {
                        cal = dateEntry.cal;
                    }

                    // Init Date dialog with current date
                    dateDialog = new DatePickerDialog(getContext(), R.style.DialogTheme, new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                            if (dateEntry.cal == null) dateEntry.cal = Calendar.getInstance();

                            dateEntry.cal.set(year, month, dayOfMonth);
                            timeDialog = new TimePickerDialog(getContext(), R.style.DialogTheme, new TimePickerDialog.OnTimeSetListener() {
                                @Override
                                public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {
                                    dateEntry.cal.set(dateEntry.cal.get(Calendar.YEAR),
                                            dateEntry.cal.get(Calendar.MONTH),
                                            dateEntry.cal.get(Calendar.DATE),
                                            hourOfDay,
                                            minute);
                                    SimpleDateFormat sdf = new SimpleDateFormat(Constants.Date.FORMAT_BACKEND);
                                    String date = sdf.format(dateEntry.cal.getTime());
                                    tvDate.setText(date);
                                }
                            }, dateEntry.cal.get(Calendar.HOUR_OF_DAY), dateEntry.cal.get(Calendar.MINUTE), false);
                            timeDialog.show();
                        }
                    }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));

                    // Show Dialog
                    dateDialog.show();
                }
            });
        }
    }

    private void InitCheckboxField() {
        // Set checbox UI vsiible
        cbCheckbox.setVisibility(VISIBLE);

        if (bReadOnly) {
            cbCheckbox.setEnabled(false);
        }

        cbCheckbox.setChecked(((FormEntry.Checkbox) entry).bChecked);
    }

    private void InitPhotoField() {
        // Set photo layout visible
        rlPhoto.setVisibility(VISIBLE);

        // Set photo in imageview
        SetPhoto(ivPhoto, tvPhoto);

        if (bReadOnly) {
            // Set listener to view photo in fullscreen
            rlPhoto.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    ViewPhotoActivity.Start(App.GetCurrentActivity(), entry.toString());
                }
            });
        } else {
            // Set listener to capture & preview photo from camera
            rlPhoto.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    BaseActivity activity = App.GetCurrentActivity();
                    if ((activity != null) && (takePictureIntent.resolveActivity(activity.getPackageManager()) != null)) {
                        // Create the File where the photo should go
                        final File photoFile = Statics.CreatePicture(getContext());

                        // Continue only if the File was successfully created
                        if (photoFile != null) {
                            Uri photoURI = FileProvider.getUriForFile(  getContext(),
                                    "com.biz.navimate.fileprovider",
                                    photoFile);
                            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);

                            // Set Photo Result Listener
                            activity.SetPhotoResultListener(new IfaceResult.Photo() {
                                @Override
                                public void onPhotoResult() {
                                    if (photoFile.exists()) {
                                        //scale the Image
                                        String scaledImageName = Statics.ScaleImageFile(getContext(), photoFile.getAbsolutePath());

                                        //set photo editor activity listener
                                        activity.SetPhotoEditorListener(new IfaceResult.PhotoEditor() {
                                            @Override
                                            public void onPhotoEditorResult(String compressedFile) {
                                                // Set field cache
                                                FormEntry.Photo photoEntry = (FormEntry.Photo) entry;
                                                photoEntry.filename = compressedFile;

                                                // Preview photo in dialog
                                                SetPhoto(ivPhoto, tvPhoto);
                                            }
                                        });
                                        PhotoEditorActivity.Start(App.GetCurrentActivity(), scaledImageName);
                                    }
                                }
                            });
                            activity.startActivityForResult(takePictureIntent, Constants.RequestCodes.PHOTO);
                        } else {
                            Dbg.Toast(getContext(), "Error while accesing photo directory...", Toast.LENGTH_SHORT);
                        }
                    } else {
                        Dbg.Toast(getContext(), "Camera App not available...", Toast.LENGTH_SHORT);
                    }
                }
            });
        }
    }

    private void InitFileField() {
        // Set photo layout visible
        rlFile.setVisibility(VISIBLE);

        if (bReadOnly) {
            tvFile.setText("");
        } else {
            // Set listener to launch file browser
            rlFile.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {

                    BaseActivity activity = App.GetCurrentActivity();
                    if (activity != null) {
                        Intent filePickerIntent = new Intent(Intent.ACTION_GET_CONTENT);
                        filePickerIntent.setType("*/*");

                        // Set Photo Result Listener
                        activity.SetFilePickerResultListener(new IfaceResult.FilePicker() {
                            @Override
                            public void onFilePicked(String name) {
                                FormEntry.File fileEntry = (FormEntry.File) entry;
                                fileEntry.filename = name;
                                tvFile.setText("File Picked");
                            }
                        });
                        activity.startActivityForResult(filePickerIntent, Constants.RequestCodes.FILE_PICKER);
                    } else {
                        Dbg.Toast(getContext(), "File picker not available...", Toast.LENGTH_SHORT);
                    }
                }
            });
        }
    }

    private void InitSignField() {
        // Set sign layout visible
        rlSignature.setVisibility(VISIBLE);

        // Set photo in imageview
        SetPhoto(ivSignature, tvSignature);

        // Disable button for read only
        if (bReadOnly) {
            // Set listener to view photo in fullscreen
            rlSignature.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    ViewPhotoActivity.Start(App.GetCurrentActivity(), entry.toString());
                }
            });
        } else {
            // Set listener to capture & preview signature
            rlSignature.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Set Result Listener
                    BaseActivity activity = App.GetCurrentActivity();
                    if (activity != null) {
                        activity.SetSignResultListener(new IfaceResult.Signature() {
                            @Override
                            public void onSignatureResult(String fileName) {
                                // Set field cache
                                FormEntry.Signature signEntry = (FormEntry.Signature) entry;
                                signEntry.filename = fileName;

                                // Preview photo in dialog
                                SetPhoto(ivSignature, tvSignature);
                            }
                        });

                        // Start Signature Activity
                        SignatureActivity.Start(activity);
                    }
                }
            });
        }
    }

    private void InitRadioField() {
        // Set Radio List visible
        rgRadioList.setVisibility(VISIBLE);

        // Add radio options
        FormEntry.RadioList radioEntry = (FormEntry.RadioList) entry;
        for (int i = 0; i < radioEntry.options.size(); i++) {
            String option = radioEntry.options.get(i);

            // Add radio button
            RbCustom rb = new RbCustom(getContext());
            rb.setText(option);

            // Add button to group
            rgRadioList.addView(rb);

            // Disable button for read only
            if (bReadOnly) {
                rb.setEnabled(false);
            }

            // Check if selected
            if (radioEntry.selection == i) {
                rb.setChecked(true);
            } else {
                rb.setChecked(false);
            }
        }
    }

    private void InitChecklistField() {
        // Set Checklist visisble
        llCheckList.setVisibility(VISIBLE);

        // Add radio options
        FormEntry.CheckList clEntry = (FormEntry.CheckList) entry;
        for (int i = 0; i < clEntry.options.size(); i++) {
            CbCustom cb = new CbCustom(getContext());
            cb.setText(clEntry.options.get(i));

            // Add to group
            llCheckList.addView(cb);

            // Disable button for read only
            if (bReadOnly) {
                cb.setEnabled(false);
            }

            // Check selected
            cb.setChecked(clEntry.selection.get(i));
        }
    }

    private void SetPhoto(ImageView ivImage, TextView tvError) {
        // Don't update if invalid photo name
        String imagename = entry.toString();
        if (imagename.length() == 0) {
            Dbg.error(TAG, "No image found");
            return;
        }

        // Check valid file name
        String absolutePath = Statics.GetAbsolutePath(getContext(), imagename, Environment.DIRECTORY_PICTURES);
        File imageFile = new File(absolutePath);
        if (!imageFile.exists()) {
            Dbg.error(TAG, "Image File not found");
            return;
        }

        // Set photo field visible
        ivImage.setVisibility(VISIBLE);
        ivImage.setImageBitmap(BitmapFactory.decodeFile(absolutePath));

        // Set error field invisible
        tvError.setVisibility(GONE);
    }

    private void InitProductField() {
        // Set product UI visible
        btnProduct.setVisibility(VISIBLE);

        FormEntry.Product pEntry = (FormEntry.Product) entry;

        btnProduct.setText(pEntry.name);

        btnProduct.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check if product exists in database
                ObjProduct product = DbHelper.productTable.GetByServerId(pEntry.id);
                if (product == null) {
                    GetProductTask prodTask = new GetProductTask(getContext(), pEntry.id);
                    prodTask.SetListener(new IfaceServer.GetProduct() {
                        @Override
                        public void onProductReceived(ObjProduct product) {
                            RlDialog.Show(new Dialog.ProductViewer(product));
                        }

                        @Override
                        public void onProductFailed() {}
                    });
                    prodTask.execute();
                } else {
                    RlDialog.Show(new Dialog.ProductViewer(product));
                }
            }
        });
    }
}
