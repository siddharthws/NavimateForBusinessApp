package com.biz.navimate.tasks;

import android.content.Context;
import android.os.AsyncTask;

import com.biz.navimate.database.DbHelper;
import com.biz.navimate.server.GetWorkingHoursTask;
import com.biz.navimate.services.LocReportService;
import com.biz.navimate.services.LocationService;
import com.biz.navimate.services.WebSocketService;
import com.google.firebase.iid.FirebaseInstanceId;

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
    protected void onPreExecute()
    {
        //Get Account Settings
        GetWorkingHoursTask getWorkingHoursTask = new GetWorkingHoursTask(parentContext);
        getWorkingHoursTask.execute();
    }

    @Override
    protected Void doInBackground(Void... params)
    {
        //Initialise the DBHelper Class
        DbHelper.Init(parentContext);

        // Start Services
        WebSocketService.StartService(parentContext);
        LocationService.StartService(parentContext);
        LocReportService.StartService(parentContext);

        // Wait for FCM ID Token
        while (FirebaseInstanceId.getInstance().getToken() == null);
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
