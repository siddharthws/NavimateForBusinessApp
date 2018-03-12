package com.biz.navimate.activities;

import android.view.View;

import com.biz.navimate.R;

/**
 * Created by Sai_Kameswari on 12-03-2018.
 */

public class PhotoEditorActivity extends BaseActivity {
    // ----------------------- Constants ----------------------- //
    private static final String TAG = "PHOTO_EDITOR_ACTIVITY";

    // ----------------------- Interfaces ----------------------- //
    // ----------------------- Globals ---------------------- //
    // ----------------------- Constructor ------------------------ //
    // ----------------------- Overrides ----------------------- //

    @Override
    protected void InflateLayout() {
        // Set content view
        setContentView(R.layout.activity_photo_editor);
    }

    @Override
    protected void FindViews() {}

    @Override
    protected void SetViews() {}

    // ----------------------- Public APIs ----------------------- //
    // Button Click APIs
    public void ButtonClickBack(View view)
    {
        super.onBackPressed();
    }

    // ----------------------- Private APIs ----------------------- //
}
