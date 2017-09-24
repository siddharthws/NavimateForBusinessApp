package com.biz.navimate.appload;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import com.biz.navimate.R;
import com.biz.navimate.application.App;
import com.biz.navimate.base.BaseActivity;
import com.biz.navimate.constants.Constants;
import com.biz.navimate.viewholders.ActivityHolder;

public class AppLoadActivity extends BaseActivity {
    // ----------------------- Constants ----------------------- //
    private static final String TAG = "APP_LOAD_ACTIVITY";


    // ----------------------- Classes ---------------------------//
    // ----------------------- Interfaces ----------------------- //
    // ----------------------- Globals ----------------------- //
    private ActivityHolder.AppLoad ui = null;

    // ----------------------- Constructor ----------------------- //
    // ----------------------- Abstracts ----------------------- //
    // ----------------------- Overrides ----------------------- //
    // Init Overrides
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Mark app as initialized
        App.Initialize();

        // Call Super
        super.onCreate(savedInstanceState);
    }

    @Override
    protected View InflateLayout() {
        // Inflate layout from xml
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        return inflater.inflate(R.layout.activity_app_load, null, false);
    }

    @Override
    protected void FindViews(View view) {
    }

    @Override
    protected void SetViews() {
    }

    // ----------------------- Public APIs ----------------------- //
    public static void Start(BaseActivity activity) {
        BaseActivity.Start(activity, AppLoadActivity.class, -1, null, Constants.RequestCodes.INVALID, null);
    }

    // ----------------------- Private APIs ----------------------- //
}
