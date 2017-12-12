package com.biz.navimate.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.biz.navimate.R;
import com.biz.navimate.constants.Constants;
import com.biz.navimate.database.DbHelper;
import com.biz.navimate.debug.Dbg;
import com.biz.navimate.interfaces.IfaceServer;
import com.biz.navimate.lists.FormListAdapter;
import com.biz.navimate.lists.GenericListAdapter;
import com.biz.navimate.lists.LeadListAdapter;
import com.biz.navimate.objects.Data;
import com.biz.navimate.objects.Dialog;
import com.biz.navimate.objects.Form;
import com.biz.navimate.objects.Lead;
import com.biz.navimate.objects.ListItem;
import com.biz.navimate.objects.Task;
import com.biz.navimate.server.SyncFormsTask;
import com.biz.navimate.viewholders.ActivityHolder;
import com.biz.navimate.views.RlDialog;
import com.biz.navimate.views.TvCalibri;

import java.util.ArrayList;
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
        ui.lvList               = (ListView)        findViewById(R.id.lv_forms);
    }

    @Override
    protected void SetViews() {
        // Initialize List
        listAdpater = new FormListAdapter(this, ui.lvList);
        InitList();

        // Set Listeners
        ui.lvList.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
    {
        // Get Clicked Object
        ListItem.Form clickedItem = (ListItem.Form) listAdpater.getItem(i);
        Form form = (Form) DbHelper.formTable.GetById(clickedItem.formId);
        Data data = (Data) DbHelper.dataTable.GetById(form.dataId);

        // Open Form Dialog
        RlDialog.Show(new Dialog.SubmitForm(data, form.taskId, form.bCloseTask, true));
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
        CopyOnWriteArrayList<Form> forms = (CopyOnWriteArrayList<Form>) DbHelper.formTable.GetAll();
        for (int i = forms.size() - 1; i >= 0; i--) {
            listAdpater.Add(new ListItem.Form(forms.get(i).dbId));
        }
    }
}
