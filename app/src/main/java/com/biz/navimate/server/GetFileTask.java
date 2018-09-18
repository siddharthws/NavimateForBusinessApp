package com.biz.navimate.server;

import android.content.Context;

import com.biz.navimate.constants.Constants;
import com.biz.navimate.debug.Dbg;
import com.biz.navimate.interfaces.IfaceServer;
import com.biz.navimate.objects.Statics;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;

import okhttp3.RequestBody;
import okhttp3.Response;
import okio.BufferedSink;
import okio.Okio;

public class GetFileTask extends BaseServerTask {
    // ----------------------- Constants ----------------------- //
    private static final String TAG = "GET_FILE_TASK";

    // ----------------------- Classes ---------------------------//
    // ----------------------- Interfaces ----------------------- //
    private IfaceServer.GetFile listener = null;
    public void SetListener(IfaceServer.GetFile listener) {
        this.listener = listener;
    }

    // ----------------------- Globals ----------------------- //
    private String extDir = "";
    private String filename = "";

    boolean bFileReceived = false;

    // ----------------------- Constructor ----------------------- //
    public GetFileTask(Context parentContext, String extDir, String filename) {
        super(parentContext, Constants.Server.URL_GET_FILE);
        this.extDir       = extDir;
        this.filename     = filename;
    }

    // ----------------------- Overrides ----------------------- //
    @Override
    public void onPreExecute() {}

    @Override
    public Void doInBackground (Void... params) {
        // Try getting file from directory
        GetFromDir();

        // If not found, try getting from server
        if (!bFileReceived) { GetFromServer(); }

        return null;
    }

    @Override
    public void onPostExecute (Void result) {
        if (listener != null) {
            if (bFileReceived) {
                listener.onFileSuccess();
            } else {
                listener.onFileFailed();
            }
        }
    }

    @Override
    protected void ParseOkHttpResponse(Response response) throws JSONException, IOException {
        if (response.code() == HttpURLConnection.HTTP_OK) {
            // Create new file to copy contents
            File downloadedFile = Statics.CreateFile(parentContext, filename, extDir);

            // Write data to file
            BufferedSink sink = Okio.buffer(Okio.sink(downloadedFile));
            sink.writeAll(response.body().source());
            sink.close();

            // Set File Received flag
            bFileReceived = true;
        } else {
            Dbg.error(TAG, "Response Code = " + response.code());
        }
    }

    // ----------------------- Public APIs ----------------------- //
    // ----------------------- Private APIs ----------------------- //
    private void GetFromDir() {
        bFileReceived = Statics.isFileExist(parentContext, filename, extDir);
    }

    private void GetFromServer() {
        // Init Request JSON
        JSONObject requestJson = new JSONObject();
        try {
            requestJson.put(Constants.Server.KEY_FILE_NAME, filename);
        }
        catch (JSONException e) {
            Dbg.error(TAG, "Error while preparing request");
            return;
        }

        // Add data to request Builder
        reqBuilder.method("POST", RequestBody.create(JSON, requestJson.toString()));

        // Call Super
        super.doInBackground();
    }
}
