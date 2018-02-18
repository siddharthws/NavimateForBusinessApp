package com.biz.navimate.services;

import android.content.Context;

import com.biz.navimate.debug.Dbg;

/**
 * Created by Sai_Kameswari on 17-02-2018.
 */

public class LocReportService extends BaseService {
    // ----------------------- Constants ----------------------- //
    private static final String TAG = "LOC_REPORT_SERVICE";

    // Macros for time intervals
    private static final int   TIME_MS_60_S            = 60 * 1000; // 60 second interval

    // ----------------------- Classes ---------------------------//
    // ----------------------- Interfaces ----------------------- //
    // ----------------------- Globals ----------------------- //
    private static LocReportService service   = null;

    // ----------------------- Constructor ----------------------- //
    // ----------------------- Overrides ----------------------- //
    @Override
    public void Init(){
        service = this;
    }

    @Override
    public void StickyServiceJob()
    {
        // Getinterval for next sleep
        long interval = GetSleepInterval();

        // Sleep
        Sleep(interval);
    }

    @Override
    public void Destroy(){}

    // ----------------------- Public APIs ----------------------- //
    // APIs to start / stop the service
    public static void StartService(Context context) {
        if (!IsRunning()) {
            // Start Service
            StartService(context, LocReportService.class);
        }
    }

    public static void StopService() {
        // Stop service
        if (IsRunning()) {
            // Stop this service
            StopService(service);
        }
    }

    public static boolean IsRunning()
    {
        return IsRunning(service);
    }

    // ----------------------- Private APIs ----------------------- //
    // API to get sleep interval as per current params
    private long GetSleepInterval() {
        return TIME_MS_60_S;
    }
}