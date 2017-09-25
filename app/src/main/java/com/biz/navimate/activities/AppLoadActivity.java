package com.biz.navimate.activities;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import com.biz.navimate.R;
import com.biz.navimate.application.App;
import com.biz.navimate.constants.Constants;
import com.biz.navimate.interfaces.IfaceResult;
import com.biz.navimate.misc.Preferences;
import com.biz.navimate.objects.User;
import com.biz.navimate.tasks.AppLoadTask;
import com.biz.navimate.viewholders.ActivityHolder;

public class AppLoadActivity    extends     BaseActivity
                                implements  IfaceResult.Registration,
                                            AppLoadTask.IfaceAppLoad
{
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
        // Set content view
        setContentView(R.layout.activity_app_load);

        // Inflate layout from xml
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        return inflater.inflate(R.layout.activity_app_load, null, false);
    }

    @Override
    protected void FindViews(View view) {
    }

    @Override
    protected void SetViews() {
        // Initialize Preferences
        Preferences.Init(this);

        // Check if user is registered
        if (Preferences.GetUser().appId == User.INVALID_ID) {
            // Set Registration result listener
            SetRegistrationResultListener(this);

            // Start Registration activity
            RegistrationActivity.Start(this);
        } else {
            onRegisterSuccess();
        }
    }

    // Registration Overrides
    @Override
    public void onRegisterSuccess() {
        // Execute App Load Task
        AppLoadTask appLoad = new AppLoadTask(this);
        appLoad.SetOnAppLoadingListener(this);
        appLoad.execute();
    }

    @Override
    public void onRegisterFailure() {
        finish();
    }

    // App Load Overrides
    @Override
    public void onLoadComplete() {
        // Start Homescreen
        HomescreenActivity.Start(this);
    }

    // ----------------------- Public APIs ----------------------- //
    public static void Start(BaseActivity activity) {
        BaseActivity.Start(activity, AppLoadActivity.class, -1, null, Constants.RequestCodes.INVALID, null);
    }

    // ----------------------- Private APIs ----------------------- //
}
