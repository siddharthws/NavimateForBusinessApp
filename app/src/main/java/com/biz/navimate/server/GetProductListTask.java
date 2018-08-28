package com.biz.navimate.server;

import android.content.Context;
import android.widget.Toast;

import com.biz.navimate.constants.Constants;
import com.biz.navimate.debug.Dbg;
import com.biz.navimate.interfaces.IfaceServer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import okhttp3.RequestBody;

public class GetProductListTask extends BaseServerTask {
    // ----------------------- Constants ----------------------- //
    private static final String TAG = "GET_PRODUCT_LIST_TASK";

    // ----------------------- Classes ---------------------------//
    // ----------------------- Interfaces ----------------------- //
    private IfaceServer.GetProductList listener = null;
    public void SetListener(IfaceServer.GetProductList listener) {
        this.listener = listener;
    }

    // ----------------------- Globals ----------------------- //
    HashMap<String, String> products = null;
    private int startIndex = 0;
    private int totalCount = 0;
    private  String text   = "";

    // ----------------------- Constructor ----------------------- //
    public GetProductListTask(Context parentContext, int startIndex, String text) {
        super(parentContext, Constants.Server.URL_GET_PRODUCT_LIST);
        this.startIndex = startIndex;
        this.text       = text;
    }

    // ----------------------- Overrides ----------------------- //
    @Override
    public void onPreExecute() {}

    @Override
    public Void doInBackground (Void... params) {
        // Init Request JSON
        JSONObject requestJson = new JSONObject();
        JSONObject pager = new JSONObject();
        try {
            requestJson.put(Constants.Server.KEY_TEXT,text);
            pager.put(Constants.Server.KEY_START, startIndex);
            pager.put(Constants.Server.KEY_COUNT, 10);
            requestJson.put(Constants.Server.KEY_PAGER, pager);
        }
        catch (JSONException e) {
            Dbg.error(TAG, "Error while preparing request");
            return null;
        }

        // Add data to request Builder
        reqBuilder.method("POST", RequestBody.create(JSON, requestJson.toString()));

        // Call Super
        super.doInBackground(params);

        // Save Product in Database
        if (IsResponseValid()) {
            try{
                products = new HashMap<>();
                JSONArray productsJson = responseJson.getJSONArray(Constants.Server.KEY_RESULTS);
                this.totalCount = responseJson.getInt(Constants.Server.KEY_TOTAL_COUNT);

                // Parse json data to HashMap
                for(int i =0 ; i < productsJson.length(); i++){
                    JSONObject product = productsJson.getJSONObject(i);
                    String id = product.getString(Constants.Server.KEY_ID);
                    String name = product.getString(Constants.Server.KEY_NAME);
                    products.put(id,name);
                }
            } catch (JSONException e) {
                Dbg.error(TAG, "Could not parse productJson");
                Dbg.stack(e);
            }
        }
        return null;
    }

    @Override
    public void onPostExecute (Void result) {
        if (products != null) {
            // Call listener
            if (listener != null) {
                listener.onProductReceived(products, totalCount);
            }
        } else {
            Dbg.Toast(parentContext, "Could not get product...", Toast.LENGTH_SHORT);

            // Call listener
            if (listener != null) {
                listener.onProductListFailed();
            }
        }
    }

    // ----------------------- Public APIs ----------------------- //
    // ----------------------- Private APIs ----------------------- //
}