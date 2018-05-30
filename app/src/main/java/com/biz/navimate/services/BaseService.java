package com.biz.navimate.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.annotation.Nullable;

import com.biz.navimate.application.App;
import com.biz.navimate.debug.Dbg;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Siddharth on 25-03-2017.
 */

public abstract class BaseService   extends     Service
                                    implements  Runnable
{
    // ----------------------- Constants ----------------------- //
    private static final String TAG = "BASE_SERVICE";

    // ----------------------- Classes ---------------------------//
    private class StarterTask extends AsyncTask<Void, Void, Void>
    {
        private Runnable serviceRunnable = null;

        public StarterTask (Runnable serviceRunnable)
        {
            this.serviceRunnable = serviceRunnable;

        }

        @Override
        protected Void doInBackground(Void... voids)
        {
            // Ensure bStopTask is set to True
            bStopTask = true;

            // Wait for 2 seconds for the service thread to stop
            long startTimeMs = System.currentTimeMillis();
            long waitDurationMs = 2000;
            while (bTaskRunning && ((System.currentTimeMillis() - startTimeMs) < waitDurationMs))
            {
                SystemClock.sleep(100);
            }

            // Start service if its no longer running
            if (!bTaskRunning)
            {
                // SetAlarm executor
                executor = Executors.newSingleThreadExecutor();

                // Start runnable if previous task has been stopped
                executor.execute(serviceRunnable);
            }
            return null;
        }
    }

    // ----------------------- Interfaces ----------------------- //
    public abstract void Init();
    public abstract void StickyServiceJob();

    // ----------------------- Globals ----------------------- //
    private ExecutorService     executor = null;
    private Boolean             bTaskRunning = false;
    public  Boolean             bStopTask = true;
    public  Boolean             bInterruptSleep = false;

    // ----------------------- Constructor ----------------------- //
    // ----------------------- Overrides ----------------------- //
    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        HandleCommand();
        return Service.START_NOT_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent)
    {
        return null;
    }

    @Override
    public void run()
    {
        // Reset Booleans
        bTaskRunning = true;
        bStopTask = false;

        // Call Init API of service
        Init();

        while (!bStopTask)
        {
            if (!App.IsInitialized())
            {
                Dbg.error(TAG, "App is not loaded. Stopping periodic service");
                break;
            }

            StickyServiceJob();
        }

        bTaskRunning = false;
    }


    // ----------------------- Public APIs ----------------------- //
    protected static void StartService(Context context, Class<?> cls)
    {
        // Start service with this context and intent
        Intent startIntent = new Intent(context, cls);
        context.startService(startIntent);
    }

    protected static void StopService(BaseService service)
    {
        if (service != null)
        {
            // Stop any running tasks from looping
            service.bStopTask = true;

            // Stop the service
            service.stopSelf();
        }
    }

    public static boolean IsRunning(BaseService service)
    {
        if (service != null)
        {
            if (service.bTaskRunning && !service.bStopTask)
            {
                return true;
            }
        }

        return false;
    }

    public static void Interrupt(BaseService service)
    {
        if (IsRunning(service)) {
            service.bInterruptSleep = true;
        }
    }

    // ----------------------- Private APIs ----------------------- //

    public void HandleCommand()
    {
        // If service is already running, return early
        if (IsRunning(this))
        {
            return;
        }

        // Start async task to execute the service load
        StarterTask startedTask = new StarterTask(this);
        startedTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    // API to sleep the service for certain time
    protected void Sleep (long durationMs) {
        long nextRunTimeMs = System.currentTimeMillis() + durationMs;
        while ((System.currentTimeMillis() < nextRunTimeMs) && !bStopTask && !bInterruptSleep) {
            SystemClock.sleep(1000);
        }

        // Reset Sleep Interrupt flag
        bInterruptSleep = false;
    }
}
