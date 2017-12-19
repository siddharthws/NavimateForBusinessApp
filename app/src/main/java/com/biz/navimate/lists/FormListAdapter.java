package com.biz.navimate.lists;

import android.content.Context;
import android.view.View;
import android.widget.ListView;

import com.biz.navimate.R;
import com.biz.navimate.constants.Constants;
import com.biz.navimate.database.DbHelper;
import com.biz.navimate.objects.Form;
import com.biz.navimate.objects.Lead;
import com.biz.navimate.objects.ListItem;
import com.biz.navimate.objects.Statics;
import com.biz.navimate.objects.Task;
import com.biz.navimate.objects.Template;
import com.biz.navimate.viewholders.ListHolder;
import com.biz.navimate.views.TvCalibri;

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
        super(parentContext, lvList, R.layout.list_form);
    }

    // ----------------------- Overrides ----------------------- //
    @Override
    protected void SetViewHolder(View view)
    {
        // Init Holder
        ListHolder.Form holder = new ListHolder.Form();
        holder.tvLead       = (TvCalibri)   view.findViewById(R.id.tv_lead_title);
        holder.tvForm       = (TvCalibri)   view.findViewById(R.id.tv_form_name);
        holder.tvDate       = (TvCalibri)   view.findViewById(R.id.tv_date);
        holder.tvStatus     = (TvCalibri)   view.findViewById(R.id.tv_sync_status);

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
        Task task = (Task) DbHelper.taskTable.GetById(form.taskId);
        Lead lead = (Lead) DbHelper.leadTable.GetById(task.leadId);
        Template template = (Template) DbHelper.templateTable.GetById(task.formTemplateId);

        // Set Lead / Template Name
        holder.tvLead.setText(lead.title);
        holder.tvForm.setText(template.name);

        // Set Sync Status
        if (form.serverId == Constants.Misc.ID_INVALID) {
            holder.tvStatus.setText("Saved...");
        } else {
            holder.tvStatus.setText("Submitted...");
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