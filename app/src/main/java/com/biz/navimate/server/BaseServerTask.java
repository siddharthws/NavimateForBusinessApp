package com.biz.navimate.server;

import android.content.Context;
import android.os.AsyncTask;

import com.biz.navimate.constants.Constants;
import com.biz.navimate.debug.Dbg;
import com.biz.navimate.misc.InternetHelper;
import com.biz.navimate.misc.Preferences;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Siddharth on 09-04-2017.
 */

public abstract class BaseServerTask extends AsyncTask<Void, Void, Void>
{
    // ----------------------- Constants ----------------------- //
    private static final String TAG = "BASE_SERVER_TASK";

    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    // ----------------------- Classes ---------------------------//
    // ----------------------- Interfaces ----------------------- //
    // ----------------------- Abstracts ----------------------- //
    // ----------------------- Globals ----------------------- //
    protected Context parentContext = null;

    // Connection related initData
    private String url = null;

    // Request / Response Parameters
    protected  JSONObject requestJson = null;
    protected  JSONObject responseJson = null;

    // ----------------------- Constructor ----------------------- //
    public BaseServerTask(Context parentContext, String url)
    {
        this.parentContext = parentContext;
        this.url = url;
    }

    // ----------------------- Overrides ----------------------- //
    @Override
    public Void doInBackground (Void... params)
    {
        // Check for internet
        if (!InternetHelper.IsInternetAvailable(parentContext))
        {
            Dbg.error(TAG, "Internet Error !");
        }
        // Connect to Server
        else if (!ConnectToServer())
        {
            Dbg.error(TAG, "Cannot connect to server");
        }

        return null;
    }

    // ----------------------- Public APIs ----------------------- //
    // ----------------------- Private APIs ----------------------- //
    protected boolean IsResponseValid()
    {
        // Valid response JSON
        if (responseJson == null)
        {
            return false;
        }

        return true;
    }

    private boolean ConnectToServer()
    {
        boolean bResult = false;

        // Build OkHttp3 Request
        OkHttpClient httpClient = new OkHttpClient();
        Request.Builder builder = new Request.Builder()
                                        .url(url)
                                        .header(Constants.Server.KEY_ID, String.valueOf(Preferences.GetUser().appId));

        // Add JSON body in the request (if required)
        if (requestJson != null) {
            builder.method("POST", RequestBody.create(JSON, requestJson.toString()));
        }

        try {
            // Execute request and get response
            Request request = builder.build();
            Response response = httpClient.newCall(request).execute();

            // Validate and parse reponse
            if (response.code() == HttpURLConnection.HTTP_OK) {
                responseJson = new JSONObject(response.body().string());
                bResult = true;
            } else {
                Dbg.error(TAG, "Response Code = " + response.code());
            }
        } catch (IOException e) {
            Dbg.error(TAG, "IO exception while making server request");
            Dbg.stack(e);
        } catch (JSONException e) {
            Dbg.error(TAG, "JSON exception while making server request");
            Dbg.stack(e);
        }

        return bResult;
    }
}
