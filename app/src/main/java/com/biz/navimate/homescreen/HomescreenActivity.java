package com.biz.navimate.homescreen;

import android.view.LayoutInflater;
import android.view.View;

import com.biz.navimate.R;
import com.biz.navimate.base.BaseActivity;
import com.biz.navimate.constants.Constants;
import com.biz.navimate.viewholders.ActivityHolder;

public class HomescreenActivity extends BaseActivity {
    // ----------------------- Constants ----------------------- //
    private static final String TAG = "HOMESCREEN_ACTIVITY";


    // ----------------------- Classes ---------------------------//
    // ----------------------- Interfaces ----------------------- //
    // ----------------------- Globals ----------------------- //
    private ActivityHolder.Homescreen ui = null;

    // ----------------------- Constructor ----------------------- //
    // ----------------------- Abstracts ----------------------- //
    // ----------------------- Overrides ----------------------- //
    // Init Overrides
    @Override
    protected View InflateLayout() {
        // Inflate layout from xml
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        return inflater.inflate(R.layout.activity_homescreen, null, false);
    }

    @Override
    protected void FindViews(View view) {
    }

    @Override
    protected void SetViews() {
    }

    // ----------------------- Public APIs ----------------------- //
    public static void Start(BaseActivity activity) {
        BaseActivity.Start(activity, HomescreenActivity.class, -1, null, Constants.RequestCodes.INVALID, null);
    }

    // ----------------------- Private APIs ----------------------- //
}