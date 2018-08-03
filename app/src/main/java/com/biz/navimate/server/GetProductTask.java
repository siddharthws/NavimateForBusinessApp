package com.biz.navimate.server;

import android.content.Context;
import android.widget.Toast;

import com.biz.navimate.constants.Constants;
import com.biz.navimate.database.DbHelper;
import com.biz.navimate.debug.Dbg;
import com.biz.navimate.interfaces.IfaceServer;
import com.biz.navimate.objects.Dialog;
import com.biz.navimate.objects.ObjProduct;
import com.biz.navimate.views.RlDialog;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.RequestBody;

public class GetProductTask extends BaseServerTask {
    // ----------------------- Constants ----------------------- //
    private static final String TAG = "GET_PRODUCT_TASK";

    // ----------------------- Classes ---------------------------//
    // ----------------------- Interfaces ----------------------- //
    private IfaceServer.GetProduct listener = null;
    public void SetListener(IfaceServer.GetProduct listener)
    {
        this.listener = listener;
    }

    // ----------------------- Globals ----------------------- //
    private String id = "";
    private ObjProduct product;

    // ----------------------- Constructor ----------------------- //
    public GetProductTask(Context parentContext, String id)
    {
        super(parentContext, Constants.Server.URL_GET_PRODUCT);
        this.id = id;
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

        try
        {
            requestJson.put(Constants.Server.KEY_ID, id);
        }
        catch (JSONException e)
        {
            Dbg.error(TAG, "Error while putting id in JSON");
            return null;
        }

        // Add data to request Builder
        reqBuilder.method("POST", RequestBody.create(JSON, requestJson.toString()));

        // Call Super
        super.doInBackground(params);

        // Save Product in Database
        if (IsResponseValid()) {
            product = new ObjProduct(responseJson);
            DbHelper.productTable.Save(product);
        }

        return null;
    }

    @Override
    public void onPostExecute (Void result)
    {
        RlDialog.Hide();

        if (product != null)
        {
            // Call listener
            if (listener != null)
            {
                listener.onProductReceived(product);
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
