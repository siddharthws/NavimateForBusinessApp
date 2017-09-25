package com.biz.navimate.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.biz.navimate.application.App;
import com.biz.navimate.constants.Constants;
import com.biz.navimate.interfaces.IfaceResult;

import java.util.ArrayList;

/**
 * Created by Siddharth on 22-09-2017.
 */

public abstract class BaseActivity extends AppCompatActivity {
    // ----------------------- Constants ----------------------- //
    private static final String TAG = "BASE_ACTIVITY";

    // ----------------------- Classes ---------------------------//
    // ----------------------- Interfaces ----------------------- //
    // ----------------------- Globals ----------------------- //

    // Activity result related initData
    private int resumeRequestCode = 0, resumeResultCode = 0;
    private Intent resumeResultIntent = null;

    // Result Listeners
    private IfaceResult.Registration  registerListener             = null;

    // ----------------------- Constructor ----------------------- //
    // ----------------------- Abstracts ----------------------- //
    // Manadatory overrides to initialize activitiy views
    protected abstract View InflateLayout();
    protected abstract void FindViews(View view);
    protected abstract void SetViews();

    // ----------------------- Overrides ----------------------- //
    // Lifecycle overrides
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        // Call super
        super.onCreate(savedInstanceState);

        // Load App if not initialized
        if (!App.IsInitialized()) {
            AppLoadActivity.Start(this);
            return;
        }

        // Inflate Layout
        View view = InflateLayout();

        // Find Views
        FindViews(view);

        // Init View Data
        SetViews();
    }

    @Override
    public void onRestart()
    {
        // Call Super
        super.onRestart();
    }

    @Override
    public void onStart()
    {
        // Call Super
        super.onStart();
    }

    @Override
    public void onResume()
    {
        // Call Super
        super.onResume();

        // Set this to current activity
        App.SetCurrentActivity(this);

        if (resumeRequestCode != Constants.RequestCodes.INVALID)
        {
            // Call current listeners
            CallResultListeners();

            // Reset initData
            this.resumeRequestCode = Constants.RequestCodes.REGISTRATION;
            this.resumeResultCode = 0;
            this.resumeResultIntent = null;
        }
    }

    @Override
    public void onPause()
    {
        // Clear this as current activity
        App.ClearCurrentActivity(this);

        // Call Super
        super.onPause();
    }

    @Override
    public void onStop()
    {
        // Call Super
        super.onStop();
    }

    @Override
    public void onDestroy()
    {
        // Call Super
        super.onDestroy();
    }

    @Override
    public void onNewIntent(Intent intent)
    {
        super.onNewIntent(intent);
        setIntent(intent);
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
    }

    // Result overrides
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        // Set resume data
        this.resumeRequestCode = requestCode;
        this.resumeResultCode = resultCode;
        this.resumeResultIntent = data;

        // Call Super
        super.onActivityResult(requestCode, resultCode, data);
    }

    // ----------------------- Public APIs ----------------------- //
    // General APi to start any activity with custom params
    public static void Start(BaseActivity activity, Class<?> cls, int flags, Bundle extras, int requestCode, String action)
    {
        // SetAlarm Intent
        Intent intent = new Intent(activity, cls);

        // Set Extras
        if (extras != null)
        {
            intent.putExtras(extras);
        }

        // Set Action
        if (action != null)
        {
            intent.setAction(action);
        }

        // Set Flags
        if (flags != -1)
        {
            intent.setFlags(flags);
        }

        // Launch Activity
        if (requestCode == Constants.RequestCodes.INVALID)
        {
            // No result expected
            activity.startActivity(intent);
        }
        else
        {
            // Result expected
            activity.startActivityForResult(intent, requestCode);
        }
    }

    // APIs to set event listeners
    public void SetRegistrationResultListener(IfaceResult.Registration listener)
    {
        this.registerListener = listener;
    }

    // ----------------------- Private APIs ----------------------- //
    // API to check the activity result type and call listener
    private void CallResultListeners()
    {
        switch (resumeRequestCode)
        {
            case Constants.RequestCodes.REGISTRATION:
            {
                if (registerListener != null)
                {
                    if (resumeResultCode == Activity.RESULT_OK)
                    {
                        registerListener.onRegisterSuccess();
                    }
                    else
                    {
                        registerListener.onRegisterFailure();
                    }
                }
                break;
            }
        }
    }
}
