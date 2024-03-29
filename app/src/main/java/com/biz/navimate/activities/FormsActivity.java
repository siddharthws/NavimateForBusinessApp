package com.biz.navimate.activities;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;

import com.biz.navimate.R;
import com.biz.navimate.constants.Constants;
import com.biz.navimate.database.DbHelper;
import com.biz.navimate.interfaces.IfaceServer;
import com.biz.navimate.lists.FormListAdapter;
import com.biz.navimate.objects.Dialog;
import com.biz.navimate.objects.core.ObjForm;
import com.biz.navimate.objects.ListItem;
import com.biz.navimate.server.SyncFormsTask;
import com.biz.navimate.viewholders.ActivityHolder;
import com.biz.navimate.views.RlDialog;
import com.biz.navimate.views.RlListView;

import java.util.concurrent.CopyOnWriteArrayList;

public class FormsActivity  extends     BaseActivity
                            implements  AdapterView.OnItemClickListener
{
    // ----------------------- Constants ----------------------- //
    private static final String TAG = "FORMS_ACTIVITY";

    // ----------------------- Interfaces ----------------------- //
    // ----------------------- Globals ----------------------- //
    private ActivityHolder.Forms  ui                 = null;
    private FormListAdapter listAdpater              = null;

    // ----------------------- Constructor ----------------------- //
    // ----------------------- Overrides ----------------------- //

    @Override
    protected void InflateLayout() {
        // Set content view
        setContentView(R.layout.activity_forms);
    }

    @Override
    protected void FindViews() {
        // Init view holder
        ui = new ActivityHolder.Forms();
        holder = ui;

        // Activity View
        ui.ibSync               = (ImageButton)     findViewById(R.id.ib_toolbar_sync);
        ui.ibBack               = (ImageButton)     findViewById(R.id.ib_toolbar_back);
        ui.rlvList              = (RlListView)      findViewById(R.id.rlv_forms);
    }

    @Override
    protected void SetViews() {
        // Initialize List
        listAdpater = new FormListAdapter(this, ui.rlvList.GetListView());
        InitList();

        // Set Listeners
        ui.rlvList.GetListView().setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
    {
        // Get Clicked Object
        ListItem.Form clickedItem = (ListItem.Form) listAdpater.getItem(i);
        ObjForm form = (ObjForm) DbHelper.formTable.GetById(clickedItem.formId);

        // Open Form Dialog
        RlDialog.Show(new Dialog.SubmitForm(form, true));
    }

    // ----------------------- Public APIs ----------------------- //
    public static void Start(BaseActivity parentActivity)
    {
        BaseActivity.Start(parentActivity, FormsActivity.class, -1, null, Constants.RequestCodes.INVALID, null);
    }

    // Button Click APIs
    public void ButtonClickBack(View view)
    {
        super.onBackPressed();
    }

    public void ButtonClickSync(View view)
    {
       // Sync Forms
        SyncFormsTask syncForms = new SyncFormsTask(this, true);
        syncForms.SetListener(new IfaceServer.SyncForms() {
            @Override
            public void onFormsSynced() {
                InitList();
            }
        });
        syncForms.execute();
    }

    // ----------------------- Private APIs ----------------------- //
    private void InitList() {
        // Reset Adapter
        listAdpater.Clear();

        // Add form items in reverse order
        CopyOnWriteArrayList<ObjForm> forms = (CopyOnWriteArrayList<ObjForm>) DbHelper.formTable.GetAll();
        for (int i = forms.size() - 1; i >= 0; i--) {
            listAdpater.Add(new ListItem.Form(forms.get(i).dbId));
        }

        // Update List UI
        if (forms.size() == 0) {
            ui.rlvList.ShowBlank();
        } else {
            ui.rlvList.ShowList();
        }
    }
}
