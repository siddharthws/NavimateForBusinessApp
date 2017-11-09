package com.biz.navimate.views;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.provider.MediaStore;
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
        if (field.type.equals(FormField.TYPE_TEXT)) {
            ((FormField.Text) field).data = etText.getText().toString();
        } else if (field.type.equals(FormField.TYPE_NUMBER)) {
            ((FormField.Number) field).data = Integer.parseInt(etNumber.getText().toString());
        } else if (field.type.equals(FormField.TYPE_RADIO_LIST)) {
            for (int i = 0; i < rgRadioList.getChildCount(); i++) {
                RbCustom rb = (RbCustom) rgRadioList.getChildAt(i);
                if (rb.isChecked()) {
                    ((FormField.RadioList) field).selection = rb.getText().toString();
                    break;
                }
            }
        } else if (field.type.equals(FormField.TYPE_CHECK_LIST)) {
            // Clear selection list
            ((FormField.CheckList) field).selection.clear();

            // Add all checked items to selection list
            for (int i = 0; i < llCheckList.getChildCount(); i++) {
                CbCustom cb = (CbCustom) llCheckList.getChildAt(i);
                ((FormField.CheckList) field).selection.add(cb.isChecked());
            }
        }
        return field;
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
                    // Set Photo Result Listener
                    activity.SetPhotoResultListener(new IfaceResult.Photo() {
                        @Override
                        public void onPhotoResult(Bitmap photo) {
                            // Scale Photo
                            Bitmap scaledPhoto = Statics.ScaleBitmap(photo);

                            // Set field cache
                            ((FormField.Photo) field).photo = scaledPhoto;

                            // Preview photo in dialog
                            tvPhoto.setVisibility(GONE);
                            ivPhoto.setVisibility(VISIBLE);
                            ivPhoto.setImageBitmap(scaledPhoto);
                        }
                    });

                    // Start Image Capture Intent
                    activity.startActivityForResult(takePictureIntent, Constants.RequestCodes.PHOTO);
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
                        public void onSignatureResult(Bitmap signature) {
                            // Scale Photo
                            Bitmap scaledSign = Statics.ScaleBitmap(signature);

                            // Set field cache
                            ((FormField.Signature) field).signature = scaledSign;

                            // Preview photo in dialog
                            tvSignature.setVisibility(GONE);
                            ivSignature.setVisibility(VISIBLE);
                            ivSignature.setImageBitmap(scaledSign);
                        }
                    });

                    // Start Signature Activity
                    SignatureActivity.Start(activity);
                }
            }
        });
    }
}
