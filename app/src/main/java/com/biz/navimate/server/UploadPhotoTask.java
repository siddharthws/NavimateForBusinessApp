package com.biz.navimate.server;

import android.content.Context;
import android.graphics.Bitmap;

import com.biz.navimate.constants.Constants;
import com.biz.navimate.debug.Dbg;
import com.biz.navimate.interfaces.IfaceServer;
import com.biz.navimate.objects.Dialog;
import com.biz.navimate.views.RlDialog;

import org.json.JSONException;

import java.io.ByteArrayOutputStream;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;

/**
 * Created by Siddharth on 08-11-2017.
 */

public class UploadPhotoTask extends BaseServerTask {
    // ----------------------- Constants ----------------------- //
    private static final String TAG = "UPLOAD_PHOTO_TASK";

    // ----------------------- Classes ---------------------------//
    // ----------------------- Interfaces ----------------------- //
    private IfaceServer.UploadPhoto listener = null;
    public void SetListener(IfaceServer.UploadPhoto listener)
    {
        this.listener = listener;
    }

    // ----------------------- Globals ----------------------- //
    private Bitmap photo = null;
    private String progressText = "";
    private String uploadedFilename = "";

    // ----------------------- Constructor ----------------------- //
    public UploadPhotoTask(Context parentContext, Bitmap photo, String progressText)
    {
        super(parentContext, Constants.Server.URL_UPLOAD_PHOTO);
        this.photo = photo;
        this.progressText = progressText;
    }

    // ----------------------- Overrides ----------------------- //
    @Override
    public void onPreExecute ()
    {
        // Start Progress Dialog
        RlDialog.Show(new Dialog.Progress(progressText, 0));
    }

    @Override
    public void onProgressUpdate (Integer... progress)
    {
        // Refresh progress dialog
        RlDialog.Show(new Dialog.Progress(progressText, progress[0]));
    }

    @Override
    public Void doInBackground (Void... params)
    {
        // Create image byte array
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        photo.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] ba = baos.toByteArray();

        // Create Multipart file body
        MultipartRequestBody fileBody = new MultipartRequestBody(ba, "image/jpeg");
        fileBody.SetProgressListener(new MultipartRequestBody.IfaceProgress() {
            @Override
            public void onMultipartProgressUpdate(int progress) {
                publishProgress(progress);
            }
        });

        // Create Request Body to Post
        RequestBody requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM).addFormDataPart("uploadedFile", "image.jpg", fileBody).build();
        reqBuilder.method("POST", requestBody);

        // Call Super
        super.doInBackground(params);

        // Get Filename from response
        if (IsResponseValid()) {
            try {
                uploadedFilename = responseJson.getString(Constants.Server.KEY_FILENAME);
            } catch (JSONException e) {
                Dbg.error(TAG, "JSON Exception while parsing response");
                Dbg.stack(e);
            }
        }

        return null;
    }

    @Override
    public void onPostExecute (Void result)
    {
        // Stop Progress Dialog
        RlDialog.Hide();

        if (IsResponseValid())
        {
            // Call listener
            if (listener != null)
            {
                listener.onPhotoUploaded(uploadedFilename);
            }
        }
        else
        {
            // Call listener
            if (listener != null)
            {
                listener.onPhotoUploadFailed();
            }
        }
    }

    @Override
    protected boolean IsResponseValid()
    {
        // Check base response
        if (!super.IsResponseValid())
        {
            Dbg.error(TAG, "Super not valid");
            return false;
        }

        // Check if name is available
        if (!responseJson.has(Constants.Server.KEY_FILENAME))
        {
            Dbg.error(TAG, "File Name not valid");
            return false;
        }

        return true;
    }

    // ----------------------- Public APIs ----------------------- //
    // ----------------------- Private APIs ----------------------- //
}
