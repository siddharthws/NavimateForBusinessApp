package com.biz.navimate.registration;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import com.biz.navimate.R;
import com.biz.navimate.base.BaseActivity;
import com.biz.navimate.constants.Constants;
import com.biz.navimate.homescreen.HomescreenActivity;
import com.biz.navimate.viewholders.ActivityHolder;

public class RegistrationActivity extends BaseActivity {
    // ----------------------- Constants ----------------------- //
    private static final String TAG = "REGISTRATION_ACTIVITY";


    // ----------------------- Classes ---------------------------//
    // ----------------------- Interfaces ----------------------- //
    // ----------------------- Globals ----------------------- //
    private ActivityHolder.Registration ui = null;

    // ----------------------- Constructor ----------------------- //
    // ----------------------- Abstracts ----------------------- //
    // ----------------------- Overrides ----------------------- //
    // Init Overrides

    @Override
    protected View InflateLayout() {
        // Inflate layout from xml
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        return inflater.inflate(R.layout.activity_registration, null, false);
    }

    @Override
    protected void FindViews(View view) {
    }

    @Override
    protected void SetViews() {
    }

    // ----------------------- Public APIs ----------------------- //
    public static void Start(BaseActivity activity) {
        BaseActivity.Start(activity, RegistrationActivity.class, -1, null, Constants.RequestCodes.REGISTRATION, null);
    }

    // ----------------------- Private APIs ----------------------- //
}
