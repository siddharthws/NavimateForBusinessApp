package com.biz.navimate.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.Button;

import com.biz.navimate.R;
import com.biz.navimate.constants.Constants;
import com.biz.navimate.objects.Statics;
import com.biz.navimate.viewholders.ActivityHolder;
import com.biz.navimate.views.VwSignature;

/**
 * Created by Siddharth on 07-11-2017.
 */

public class SignatureActivity extends BaseActivity
{
    // ----------------------- Constants ----------------------- //
    private static final String TAG = "SIGNATURE_ACTIVITY";


    // ----------------------- Classes ---------------------------//
    // ----------------------- Interfaces ----------------------- //
    // ----------------------- Globals ----------------------- //
    private ActivityHolder.Signature ui = null;

    // ----------------------- Constructor ----------------------- //
    // ----------------------- Abstracts ----------------------- //
    // ----------------------- Overrides ----------------------- //
    // Init Overrides
    @Override
    protected void InflateLayout() {
        // Set content view
        setContentView(R.layout.activity_signature);
    }

    @Override
    protected void FindViews() {
        // Init view holder
        ui = new ActivityHolder.Signature();
        holder = ui;

        // Fins Views by ID
        ui.btnBack  = (Button) findViewById(R.id.btn_back);
        ui.btnClear = (Button) findViewById(R.id.btn_clear);
        ui.btnSave  = (Button) findViewById(R.id.btn_save);
        ui.vwSignature = (VwSignature) findViewById(R.id.vw_signature);
    }

    @Override
    protected void SetViews() {}

    // ----------------------- Public APIs ----------------------- //
    public static void Start(BaseActivity activity) {
        BaseActivity.Start(activity, SignatureActivity.class, -1, null, Constants.RequestCodes.SIGNATURE, null);
    }

    public void ButtonClickBack(View view) {
        // Finish this activity
        finish();
    }

    public void ButtonClickClear(View view) {
        // Clear Signature View
        ui.vwSignature.Clear();
    }

    public void ButtonClickSave(View view) {
        // Draw Bitmap using Signature View contents
        Bitmap signatureBitmap = Statics.GetBitmapFromView(ui.vwSignature);

        // Compress Bitmap
        Bitmap compressedBitmap = Statics.ScaleBitmap(signatureBitmap);

        // Send Bitmap as result
        Intent intent = new Intent();
        intent.putExtra(Constants.Extras.SIGNATURE_BITMAP, compressedBitmap);
        setResult(RESULT_OK, intent);

        // Finish this activity
        finish();
    }

    // ----------------------- Private APIs ----------------------- //
}
