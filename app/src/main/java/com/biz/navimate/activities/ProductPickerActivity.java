package com.biz.navimate.activities;

import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;

import com.biz.navimate.R;
import com.biz.navimate.constants.Constants;
import com.biz.navimate.interfaces.IfaceServer;
import com.biz.navimate.lists.GenericListAdapter;
import com.biz.navimate.objects.ListItem;
import com.biz.navimate.server.GetProductListTask;
import com.biz.navimate.viewholders.ActivityHolder;
import com.biz.navimate.views.RlListView;
import com.biz.navimate.views.compound.EtClearable;
import com.biz.navimate.views.custom.NvmEditText;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class ProductPickerActivity   extends     BaseActivity
                                     implements  AdapterView.OnItemClickListener,
                                                 RlListView.LoadMoreListener,
                                                 IfaceServer.GetProductList,
                                                 NvmEditText.IfaceEditText {
    // ----------------------- Constants ----------------------- //
    private static final String TAG = "PRODUCT_PICKER_ACTIVITY";

    // ----------------------- Interfaces ----------------------- //
    // ----------------------- Globals ----------------------- //
    private ActivityHolder.ProductPicker  ui             = null;
    private GenericListAdapter listAdpater               = null;
    private GetProductListTask productListTask           = null;
    private boolean bGettingProduct                      = false;

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
        ui.ibBack               = (ImageButton)     findViewById(R.id.ib_toolbar_back);
        ui.rlvList              = (RlListView)      findViewById(R.id.rlv_products);
        ui.etSearch             = (EtClearable)        findViewById(R.id.et_search);
    }

    @Override
    protected void SetViews() {
        // Initialize List
        listAdpater = new GenericListAdapter(this, ui.rlvList.GetListView(), true);
        GetProducts(0);

        // Set Listeners
        ui.rlvList.GetListView().setOnItemClickListener(this);
        ui.rlvList.SetLoadMoreListener(this);
        ui.etSearch.SetListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        HashMap<String, String> pickedProduct = new HashMap<>();

        // Get Clicked Object
        ListItem.Generic clickedItem = (ListItem.Generic) listAdpater.getItem(i);

        // Add picked product details
        pickedProduct.put("sId", clickedItem.sId);
        pickedProduct.put("name", clickedItem.title);

        // Send activity result
        Intent resultData = new Intent();
        resultData.putExtra(Constants.Extras.PICKED_PRODUCT, pickedProduct);
        setResult(RESULT_OK, resultData);

        // Finish this activity
        finish();
    }

    @Override
    public void onLoadMore() {
        //Get more products
        GetProducts(listAdpater.getCount());
    }

    @Override
    public void onProductReceived(HashMap<String,String> products, int totalCount) {
        //Add products to list
        AddToList(products, totalCount);
        bGettingProduct = false;
    }

    @Override
    public void onProductListFailed() {
        // Show error UI
        ui.rlvList.ShowError();
        bGettingProduct = false;
    }

    @Override
    public void onTextChanged(String text) {
        // Clear list and Get Searched products
        listAdpater.Clear();
        GetProducts(0);
    }
    // ----------------------- Public APIs ----------------------- //
    public static void Start(BaseActivity parentActivity) {
        BaseActivity.Start(parentActivity, ProductPickerActivity.class, -1, null, Constants.RequestCodes.PRODUCT_PICKER, null);
    }

    // Button Click APIs
    public void ButtonClickBack(View view) {
        super.onBackPressed();
    }

    // ----------------------- Private APIs ----------------------- //
    private void AddToList(HashMap<String,String> products, int totalCount){
        // Put Hashmap contents into listAdapter
        Iterator it = products.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            String id = (String) pair.getKey();
            String name = (String) pair.getValue();
            listAdpater.Add(new ListItem.Generic(0L, id, name, 0,0,R.drawable.bg_white_shadow_slant));
        }

        // Update List UI
        if (listAdpater.getCount() == 0) {
            ui.rlvList.ShowBlank();
        } else {
            ui.rlvList.ShowList();
        }

        // show load more if items still left to be fetched
        if (listAdpater.getCount() < totalCount){
            ui.rlvList.ToggleLoadMore(true);
        }
    }

    private void GetProducts(int startIndex){
        // Check if productList task is still running
        if(productListTask!=null && bGettingProduct){
            productListTask.cancel(true);
        }

        //Start Task to get Products
        productListTask = new GetProductListTask(getApplicationContext(),startIndex,ui.etSearch.GetText());
        productListTask.SetListener(this);
        productListTask.execute();
        bGettingProduct = true;
        ui.rlvList.ShowWaiting();
    }
}
