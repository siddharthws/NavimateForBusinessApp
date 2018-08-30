package com.biz.navimate.lists;

import android.content.Context;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.biz.navimate.R;
import com.biz.navimate.constants.Constants;
import com.biz.navimate.database.DbHelper;
import com.biz.navimate.objects.Form;
import com.biz.navimate.objects.ListItem;
import com.biz.navimate.objects.Statics;
import com.biz.navimate.viewholders.ListHolder;

/**
 * Created by Siddharth on 11-12-2017.
 */

public class FormListAdapter extends BaseListAdapter {
    // ----------------------- Constants ----------------------- //
    private static final String TAG = "FORM_LIST_ADAPTER";

    // ----------------------- Interfaces ----------------------- //
    // ----------------------- Globals ----------------------- //

    // ----------------------- Constructor ----------------------- //
    public FormListAdapter(Context parentContext, ListView lvList)
    {
        super(parentContext, lvList, R.layout.list_form, true);
    }

    // ----------------------- Overrides ----------------------- //
    @Override
    protected void SetViewHolder(View view)
    {
        // Init Holder
        ListHolder.Form holder = new ListHolder.Form();
        holder.tvLead       = (TextView)   view.findViewById(R.id.tv_lead_title);
        holder.tvForm       = (TextView)   view.findViewById(R.id.tv_form_name);
        holder.tvDate       = (TextView)   view.findViewById(R.id.tv_date);
        holder.tvStatus     = (TextView)   view.findViewById(R.id.tv_sync_status);

        // Assign holder to view
        view.setTag(holder);
    }

    @Override
    protected void SetView(View view, ListItem.Base data, int position)
    {
        ListItem.Form    item       = (ListItem.Form) data;
        ListHolder.Form  holder     = (ListHolder.Form) view.getTag();

        // Get objects
        Form form = (Form) DbHelper.formTable.GetById(item.formId);

        // Set Template Name
        holder.tvForm.setText(form.template.name);

        // Set Lead Title
        holder.tvLead.setText(form.task != null ? form.task.lead.title : "");

        // Set Sync Status
        if (form.textServerId.length() == 0) {
            holder.tvStatus.setText("Saved...");
        } else {
            holder.tvStatus.setText("Synced...");
        }

        // Set Date
        if (Statics.IsOlderThanToday(form.timestamp)) {
            // For messages older than 1 day, display dd/MM format
            holder.tvDate.setText(Statics.GetFormattedDate(form.timestamp, "dd/MM"));
        } else {
            // Display HH:mm format
            holder.tvDate.setText(Statics.GetFormattedDate(form.timestamp, "HH:mm"));
        }
    }

    // ----------------------- Public APIs ----------------------- //
    // ----------------------- Private APIs ----------------------- //
}
