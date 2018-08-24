package com.biz.navimate.activities;

import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.Toast;

import com.biz.navimate.R;
import com.biz.navimate.constants.Constants;
import com.biz.navimate.debug.Dbg;
import com.biz.navimate.interfaces.IfaceServer;
import com.biz.navimate.lists.GenericListAdapter;
import com.biz.navimate.objects.ListItem;
import com.biz.navimate.server.GetProductListTask;
import com.biz.navimate.viewholders.ActivityHolder;
import com.biz.navimate.views.RlListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ProductPickerActivity extends         BaseActivity
        implements      AdapterView.OnItemClickListener, RlListView.LoadMoreListener
{
    // ----------------------- Constants ----------------------- //
    private static final String TAG = "PICK_PRODUCT_ACTIVITY";

    // ----------------------- Interfaces ----------------------- //
    // ----------------------- Globals ----------------------- //
    private ActivityHolder.ProductPicker  ui             = null;
    private GenericListAdapter listAdpater               = null;
    private String pickedProduct                         = "";
    private int startIndex                               = 0;

    // ----------------------- Constructor ----------------------- //
    // ----------------------- Overrides ----------------------- //

    @Override
    protected void InflateLayout() {
        // Set content view
        setContentView(R.layout.activity_product_picker);
    }

    @Override
    protected void FindViews() {
        // Init view holder
        ui = new ActivityHolder.ProductPicker();
        holder = ui;

        // Activity View
        ui.ibDone               = (ImageButton)     findViewById(R.id.ib_toolbar_done);
        ui.ibBack               = (ImageButton)     findViewById(R.id.ib_toolbar_back);
        ui.rlvList              = (RlListView)      findViewById(R.id.rlv_products);
    }

    @Override
    protected void SetViews() {
        // Init picked product
        pickedProduct = "";

        // Initialize List
        listAdpater = new GenericListAdapter(this, ui.rlvList.GetListView());
        InitList(startIndex);

        // Set Listeners
        ui.rlvList.GetListView().setOnItemClickListener(this);
        ui.rlvList.SetLoadMoreListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
    {
        // Get Clicked Object
        ListItem.Generic clickedItem = (ListItem.Generic) listAdpater.getItem(i);
        Dbg.Toast(this, clickedItem.title+" selected...", Toast.LENGTH_SHORT);
        // Check if item was already selected
        if (pickedProduct == clickedItem.sId)
        {
            // Remove as selected
            pickedProduct = "";
        }
        else
        {
            // Add to picked product
            pickedProduct = clickedItem.sId;
        }

        // Refresh List Adapter
        listAdpater.notifyDataSetChanged();
    }

    // ----------------------- Public APIs ----------------------- //
    public static void Start(BaseActivity parentActivity)
    {
        BaseActivity.Start(parentActivity, ProductPickerActivity.class, -1, null, Constants.RequestCodes.PRODUCT_PICKER, null);
    }

    // Button Click APIs
    public void ButtonClickBack(View view)
    {
        super.onBackPressed();
    }

    public void ButtonClickDone(View view)
    {
        // Ensure some contacts are selected
        if (pickedProduct == "")
        {
            Dbg.Toast(this, "No Product selected...", Toast.LENGTH_SHORT);
            return;
        }

        // Send activity result
        Intent resultData = new Intent();
        resultData.putExtra(Constants.Extras.PRODUCT_PICKER, pickedProduct);
        setResult(RESULT_OK, resultData);

        // Finish this activity
        finish();
    }

    @Override
    public void onLoadMore() {
        InitList(startIndex+10); // change it
    }

    // ----------------------- Private APIs ----------------------- //
    private void InitList(int startIndex) {
        // Reset Adapter
        //listAdpater.Clear();
        ui.rlvList.ShowWaiting();

        GetProductListTask productListTask = new GetProductListTask(getApplicationContext(),startIndex);
        productListTask.SetListener(new IfaceServer.GetProductList() {
            @Override
            public void onProductReceived(JSONObject productJson) {

                try {
                    JSONArray products = productJson.getJSONArray(Constants.Server.KEY_RESULT);
                    int count = productJson.getInt(Constants.Server.KEY_TOTAL_COUNT);

                    for (int i = 0; i < 10; i++) { // Change loop to run for number for items to be fetched at a time
                        JSONObject product = products.getJSONObject(i);
                        String id = product.getString(Constants.Server.KEY_ID);
                        String name = product.getString(Constants.Server.KEY_NAME);
                        listAdpater.Add(new ListItem.Generic(0L, id, name, 0,0,0));
                    }

                    // Update List UI
                    if (count == 0) {
                        ui.rlvList.ShowBlank();
                    } else {
                        ui.rlvList.ShowList();
                    }

                    // show load more if items still left to be fetched
                    if (listAdpater.getCount() < count){
                        ui.rlvList.ToggleLoadMore(true);
                    }

                } catch (JSONException e) {
                    ui.rlvList.ShowError();
                    Dbg.error(TAG, "Could not parse productJson");
                    Dbg.stack(e);
                }
            }

            @Override
            public void onProductFailed() {
                ui.rlvList.ShowError();
            }
        });
        productListTask.execute();

    }
}
