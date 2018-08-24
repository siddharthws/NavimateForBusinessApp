package com.biz.navimate.activities;

import android.content.Intent;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

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
        ui.vwSignature = (VwSignature) findViewById(R.id.vw_signature);
        ui.toolbar     = (Toolbar) findViewById(R.id.toolbar);

        //call method to Set toolbar
        setToolbar();
    }

    @Override
    protected void SetViews() {}

    // ----------------------- Public APIs ----------------------- //
    public static void Start(BaseActivity activity) {
        BaseActivity.Start(activity, SignatureActivity.class, -1, null, Constants.RequestCodes.SIGNATURE, null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.signaturetools, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // action with ID action_refresh was selected
            case R.id.action_save:
            {
                ButtonClickSave();
                break;
            }
            case R.id.action_clear:
            {
                ButtonClickClear();
                break;
            }
        }
        return true;
    }

    // ----------------------- Public APIs ----------------------- //
    public void ButtonClickBack() {
        // Finish this activity
        finish();
    }

    public void ButtonClickClear() {
        // Clear Signature View
        ui.vwSignature.Clear();
    }

    public void ButtonClickSave() {
        // Get image file from Signature View contents
        String compressedFileName = Statics.GetFileFromView(ui.vwSignature);

        // Send file path as result
        Intent intent = new Intent();
        intent.putExtra(Constants.Extras.SIGNATURE_IMAGE_PATH, compressedFileName);
        setResult(RESULT_OK, intent);

        // Finish this activity
        finish();
    }

    // ----------------------- Private APIs ----------------------- //
    private void setToolbar()
    {
        setSupportActionBar(ui.toolbar);
        getSupportActionBar().setTitle("Signature");
        ui.toolbar.setNavigationIcon(R.drawable.ic_back_white);

        ui.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ButtonClickBack();
            }
        });
    }
}
