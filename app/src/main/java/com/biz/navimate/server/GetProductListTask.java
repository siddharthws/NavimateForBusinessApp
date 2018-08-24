package com.biz.navimate.server;

import android.content.Context;
import android.widget.Toast;

import com.biz.navimate.constants.Constants;
import com.biz.navimate.debug.Dbg;
import com.biz.navimate.interfaces.IfaceServer;
import com.biz.navimate.objects.Dialog;
import com.biz.navimate.views.RlDialog;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.RequestBody;

public class GetProductListTask extends BaseServerTask {
    // ----------------------- Constants ----------------------- //
    private static final String TAG = "GET_PRODUCT_LIST_TASK";

    // ----------------------- Classes ---------------------------//
    // ----------------------- Interfaces ----------------------- //
    private IfaceServer.GetProductList listener = null;
    public void SetListener(IfaceServer.GetProductList listener)
    {
        this.listener = listener;
    }

    // ----------------------- Globals ----------------------- //

    JSONObject productJson = null;
    private int startIndex = 0;
    // ----------------------- Constructor ----------------------- //
    public GetProductListTask(Context parentContext)
    {
        super(parentContext, Constants.Server.URL_GET_PRODUCT_LIST);
    }

    public GetProductListTask(Context parentContext, int startIndex)
    {
        super(parentContext, Constants.Server.URL_GET_PRODUCT_LIST);
        this.startIndex = startIndex;
    }

    // ----------------------- Overrides ----------------------- //
    @Override
    public void onPreExecute() {
        // Show waiting dialog
        RlDialog.Show(new Dialog.Waiting("Getting Product..."));
    }

    @Override
    public Void doInBackground (Void... params)
    {
        // Init Request JSON
        JSONObject requestJson = new JSONObject();
        JSONObject pager = new JSONObject();
        try
        {
            pager.put(Constants.Server.KEY_START, startIndex);
            pager.put(Constants.Server.KEY_COUNT, 10); // change value to Number of products to be shown at first.
            requestJson.put(Constants.Server.KEY_PAGER, pager);
        }
        catch (JSONException e)
        {
            Dbg.error(TAG, "Error while preparing request");
            return null;
        }

        // Add data to request Builder
        reqBuilder.method("POST", RequestBody.create(JSON, requestJson.toString()));

        // Call Super
        super.doInBackground(params);

        // Save Product in Database
        if (IsResponseValid()) {
            productJson = responseJson;
        }
        return null;
    }

    @Override
    public void onPostExecute (Void result)
    {
        RlDialog.Hide();

        if (productJson != null)
        {
            // Call listener
            if (listener != null)
            {
                listener.onProductReceived(productJson);
            }
        }
        else
        {
            Dbg.Toast(parentContext, "Could not get product...", Toast.LENGTH_SHORT);

            // Call listener
            if (listener != null)
            {
                listener.onProductFailed();
            }
        }
    }

    // ----------------------- Public APIs ----------------------- //
    // ----------------------- Private APIs ----------------------- //
}
