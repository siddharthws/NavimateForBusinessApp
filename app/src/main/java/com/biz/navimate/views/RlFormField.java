package com.biz.navimate.views;

import java.io.File;
import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.biz.navimate.R;
import com.biz.navimate.activities.BaseActivity;
import com.biz.navimate.activities.SignatureActivity;
import com.biz.navimate.application.App;
import com.biz.navimate.constants.Constants;
import com.biz.navimate.debug.Dbg;
import com.biz.navimate.interfaces.IfaceResult;
import com.biz.navimate.objects.FormField;
import com.biz.navimate.objects.Statics;
import com.biz.navimate.zxing.IntentIntegrator;

/**
 * Created by Siddharth on 23-10-2017.
 */

public class RlFormField extends RelativeLayout {
    // ----------------------- Constants ----------------------- //
    public static final String TAG = "RL_FORM_FIELD";

    // ----------------------- Classes ---------------------------//
    // ----------------------- Interfaces ----------------------- //
    // ----------------------- Globals ----------------------- //
    // UI
    private LinearLayout llText = null;
    private ImageButton ibQr = null;
    private TvCalibri tvTitle;
    private EditText etNumber, etText = null;
    private RadioGroup rgRadioList = null;
    private LinearLayout llCheckList = null;
    private RelativeLayout rlPhoto = null, rlSignature = null;
    private ImageView ivPhoto = null, ivSignature = null;
    private TvCalibri tvPhoto = null, tvSignature = null;

    private FormField.Base field = null;

    // ----------------------- Constructor ----------------------- //

    public RlFormField(Context context)
    {
        super(context);
        InitView(context);
    }

    public RlFormField(Context context, FormField.Base field)
    {
        super(context);
        this.field = field;
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
    // ----------------------- Public APIs ----------------------- //
    public FormField.Base GetField() {
        FormField.Base currentField = null;

        // Create field from data
        if (field.type.equals(FormField.TYPE_TEXT)) {
            String text = etText.getText().toString();
            currentField = new FormField.Text(field.title, text);
        } else if (field.type.equals(FormField.TYPE_NUMBER)) {
            int number = Integer.parseInt(etNumber.getText().toString());
            currentField = new FormField.Number(field.title, number);
        } else if (field.type.equals(FormField.TYPE_PHOTO)) {
            currentField = new FormField.Photo(field.title, "", ((FormField.Photo) field).imagePath);
        } else if (field.type.equals(FormField.TYPE_SIGNATURE)) {
            currentField = new FormField.Signature(field.title, "", ((FormField.Signature) field).imagePath);
        } else if (field.type.equals(FormField.TYPE_RADIO_LIST)) {
            // get selection string
            String selection = "";
            for (int i = 0; i < rgRadioList.getChildCount(); i++) {
                RbCustom rb = (RbCustom) rgRadioList.getChildAt(i);
                if (rb.isChecked()) {
                    selection = rb.getText().toString();
                    break;
                }
            }

            currentField = new FormField.RadioList(field.title, ((FormField.RadioList) field).options, selection);
        } else if (field.type.equals(FormField.TYPE_CHECK_LIST)) {
            // Clear selection list

            // Add all checked items to selection list
            ArrayList<Boolean> selection = new ArrayList<>();
            for (int i = 0; i < llCheckList.getChildCount(); i++) {
                CbCustom cb = (CbCustom) llCheckList.getChildAt(i);
                selection.add(cb.isChecked());
            }
            currentField = new FormField.CheckList(field.title, ((FormField.CheckList) field).options, selection);
        }

        return currentField;
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
        tvTitle = (TvCalibri) view.findViewById(R.id.tv_title);
        etNumber = (EditText) view.findViewById(R.id.et_number);
        etText = (EditText) view.findViewById(R.id.et_text);
        rgRadioList = (RadioGroup) view.findViewById(R.id.rg_radioList);
        rlPhoto = (RelativeLayout) view.findViewById(R.id.rl_photo);
        rlSignature = (RelativeLayout)  view.findViewById(R.id.rl_signature);
        ivPhoto = (ImageView) view.findViewById(R.id.iv_photo);
        ivSignature = (ImageView)       view.findViewById(R.id.iv_signature);
        tvPhoto = (TvCalibri) view.findViewById(R.id.tv_photo);
        tvSignature = (TvCalibri)       view.findViewById(R.id.tv_signature);

        // Populate title
        tvTitle.setText(field.title);

        // Parse data from Field into UI
        if (field.type.equals(FormField.TYPE_TEXT)) {
            llText.setVisibility(VISIBLE);
            etText.setText(((FormField.Text) field).data);
        } else if (field.type.equals(FormField.TYPE_NUMBER)) {
            etNumber.setVisibility(VISIBLE);
            etNumber.setText(String.valueOf(((FormField.Number) field).data));
        } else if (field.type.equals(FormField.TYPE_PHOTO)) {
            rlPhoto.setVisibility(VISIBLE);
        } else if (field.type.equals(FormField.TYPE_SIGNATURE)) {
            rlSignature.setVisibility(VISIBLE);
        } else if (field.type.equals(FormField.TYPE_RADIO_LIST)) {
            rgRadioList.setVisibility(VISIBLE);

            // Add radio options
            for (String option : ((FormField.RadioList) field).options) {
                RbCustom rb = new RbCustom(context);
                rb.setText(option);

                // Add to group
                rgRadioList.addView(rb);

                // Check selected
                if (option.equals(((FormField.RadioList) field).selection)) {
                    rb.setChecked(true);
                }
            }
        } else if (field.type.equals(FormField.TYPE_CHECK_LIST)) {
            llCheckList.setVisibility(VISIBLE);

            // Add radio options
            FormField.CheckList clField = (FormField.CheckList) field;
            for (int i = 0; i < clField.options.size(); i++) {
                CbCustom cb = new CbCustom(context);
                cb.setText(clField.options.get(i));

                // Add to group
                llCheckList.addView(cb);

                // Check selected
                cb.setChecked(clField.selection.get(i));
            }
        }

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

        // Set Photo Capture Listener
        rlPhoto.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                BaseActivity activity = App.GetCurrentActivity();
                if ((activity != null) && (takePictureIntent.resolveActivity(activity.getPackageManager()) != null)) {
                    // Create the File where the photo should go
                    final File photoFile = Statics.CreateTempImageFile(getContext());

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
                                    // Scale Image File
                                    String compressedFile = Statics.ScaleImageFile(getContext(), photoFile.getAbsolutePath());

                                    // Set field cache
                                    ((FormField.Photo) field).imagePath = compressedFile;

                                    // Preview photo in dialog
                                    tvPhoto.setVisibility(GONE);
                                    ivPhoto.setVisibility(VISIBLE);
                                    ivPhoto.setImageBitmap(BitmapFactory.decodeFile(((FormField.Photo) field).imagePath));
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

        // Set Photo Capture Listener
        rlSignature.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // Set Result Listener
                BaseActivity activity = App.GetCurrentActivity();
                if (activity != null) {
                    activity.SetSignResultListener(new IfaceResult.Signature() {
                        @Override
                        public void onSignatureResult(String path) {
                            // Set field cache
                            ((FormField.Signature) field).imagePath = path;

                            // Preview photo in dialog
                            tvSignature.setVisibility(GONE);
                            ivSignature.setVisibility(VISIBLE);
                            ivSignature.setImageBitmap(BitmapFactory.decodeFile(((FormField.Signature) field).imagePath));
                        }
                    });

                    // Start Signature Activity
                    SignatureActivity.Start(activity);
                }
            }
        });
    }
}
