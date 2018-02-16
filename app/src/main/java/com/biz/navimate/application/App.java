package com.biz.navimate.application;

import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.biz.navimate.R;
import com.biz.navimate.activities.BaseActivity;
import com.biz.navimate.constants.Constants;

import org.acra.ACRA;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;
import org.acra.sender.HttpSender;

/**
 * Created by Siddharth on 22-09-2017.
 */

// ACRA crash reporting setup
@ReportsCrashes
(
    formUri = Constants.Server.URL_ACRA,
    httpMethod = org.acra.sender.HttpSender.Method.POST,
    reportType = HttpSender.Type.JSON,
    mode = ReportingInteractionMode.TOAST,
    resToastText = R.string.crash_report_string
)

public class App extends Application {
    // ----------------------- Constants ----------------------- //
    private static final String TAG = "APP";

    // ----------------------- Classes ---------------------------//
    // ----------------------- Interfaces ----------------------- //
    // ----------------------- Globals ----------------------- //
    private static BaseActivity mCurrentActivity = null;
    private static boolean bAppInitialized = false;

    // ----------------------- Constructor ----------------------- //
    // ----------------------- Overrides ----------------------- //
    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }

    @Override
    protected void attachBaseContext (Context base) {
        super.attachBaseContext(base);

        // Initialize ACRA in Release mode only
        if (!Constants.App.DEBUG)
        {
            ACRA.init(this);
        }
    }

    // ----------------------- Public APIs ----------------------- //
    // Current Activity Related APIs
    public static BaseActivity GetCurrentActivity() {
        return mCurrentActivity;
    }

    public static void SetCurrentActivity(BaseActivity currentActivity) {
        if (currentActivity != null) {
            mCurrentActivity = currentActivity;
        }
    }

    public static void ClearCurrentActivity(BaseActivity activity) {
        if ((activity != null) && (mCurrentActivity != null)) {
            if (activity.equals(mCurrentActivity)) {
                mCurrentActivity = null;
            }
        }
    }

    public static boolean IsInForeground() {
        if (mCurrentActivity != null) {
            return true;
        }

        return false;
    }

    public static Boolean IsCurrentActivity(BaseActivity activity) {
        if ((activity != null) && (mCurrentActivity != null)) {
            if (activity.equals(mCurrentActivity)) {
                return true;
            }
        }

        return false;
    }

    // App Init Status
    public static void Initialize() {
        bAppInitialized = true;
    }

    public static void Uninitialize() {
        bAppInitialized = false;
    }

    public static boolean IsInitialized() {
        return bAppInitialized;
    }

    // Versioning related APIs
    public static String GetVersionName(Context context) {
        PackageInfo pInfo = null;
        String versionName = "0.0.0";

        try {
            pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            versionName = pInfo.versionName;
        }
        catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return versionName;
    }

    public static int GetVersionNumber(Context context) {
        PackageInfo pInfo = null;
        int versionNumber = 0;

        try {
            pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            versionNumber = pInfo.versionCode;
        }
        catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return versionNumber;
    }

    // ----------------------- Private APIs ----------------------- //
}
