package com.biz.navimate.activities;

import android.graphics.Point;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;

import com.biz.navimate.R;
import com.biz.navimate.application.App;
import com.biz.navimate.constants.Constants;
import com.biz.navimate.interfaces.IfaceResult;
import com.biz.navimate.misc.Preferences;
import com.biz.navimate.objects.Statics;
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
    protected void InflateLayout() {
        // Set content view
        setContentView(R.layout.activity_app_load);
    }

    @Override
    protected void FindViews() {
        // Init view holder
        ui = new ActivityHolder.AppLoad();
        holder = ui;
    }

    @Override
    protected void SetViews() {
        // Initialize Preferences
        Preferences.Init(this);

        // Set Screen size
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        Statics.SCREEN_SIZE      = new Point(metrics.widthPixels, metrics.heightPixels);

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
