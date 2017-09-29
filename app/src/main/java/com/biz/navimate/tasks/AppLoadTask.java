package com.biz.navimate.tasks;

import android.content.Context;
import android.os.AsyncTask;
import android.os.SystemClock;

import java.util.ArrayList;

/**
 * Created by Siddharth on 25-09-2017.
 */

public class AppLoadTask extends AsyncTask<Void, Void, Void> {
    // ----------------------- Constants ----------------------- //
    private static final String TAG = "APP_LOAD_TASK";

    // ----------------------- Classes ----------------------- //
    // ----------------------- Interfaces ----------------------- //
    public interface IfaceAppLoad
    {
        void onLoadComplete();
    }
    private IfaceAppLoad listener = null;
    public void SetOnAppLoadingListener(IfaceAppLoad listener)
    {
        this.listener = listener;
    }

    // ----------------------- Globals ----------------------- //
    private Context parentContext = null;

    // ----------------------- Constructor ----------------------- //
    public AppLoadTask(Context parentContext)
    {
        this.parentContext = parentContext;
    }

    // ----------------------- Overrides ----------------------- //
    // Overrides for AsyncTask
    @Override
    protected Void doInBackground(Void... params)
    {
        SystemClock.sleep(3000);
        return null;
    }

    @Override
    public void onPostExecute(Void result)
    {
        // Call Listener's load complete
        if (listener != null)
        {
            listener.onLoadComplete();
        }
    }

    // ----------------------- Public APIs ----------------------- //
    // ----------------------- Private APIs ----------------------- //
}