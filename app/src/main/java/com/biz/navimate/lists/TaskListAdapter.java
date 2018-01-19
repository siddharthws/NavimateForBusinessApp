package com.biz.navimate.lists;

import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.biz.navimate.R;
import com.biz.navimate.database.DbHelper;
import com.biz.navimate.interfaces.IfaceList;
import com.biz.navimate.objects.Lead;
import com.biz.navimate.objects.ListItem;
import com.biz.navimate.viewholders.ListHolder;
import com.biz.navimate.views.TvCalibri;

/**
 * Created by Siddharth on 29-09-2017.
 */

public class TaskListAdapter    extends     BaseListAdapter
                                implements  AdapterView.OnItemClickListener
{
    // ----------------------- Constants ----------------------- //
    private static final String TAG = "TASK_LIST_ADAPTER";

    // ----------------------- Interfaces ----------------------- //
    private IfaceList.Task listener = null;

    public void SetListener(IfaceList.Task listener) {
        this.listener = listener;
    }

    // ----------------------- Globals ----------------------- //

    // ----------------------- Constructor ----------------------- //
    public TaskListAdapter(Context parentContext, ListView lvList) {
        super(parentContext, lvList, R.layout.list_task);

        // Set List Click Listener
        lvList.setOnItemClickListener(this);
    }

    // ----------------------- Overrides ----------------------- //
    @Override
    protected void SetViewHolder(View view) {
        // Init Holder
        ListHolder.Task holder = new ListHolder.Task();
        holder.tvTitle = (TvCalibri) view.findViewById(R.id.tv_title);
        holder.tvDescription = (TvCalibri) view.findViewById(R.id.tv_description);
        holder.btnForm = (Button) view.findViewById(R.id.btn_submit_form);

        // Assign holder to view
        view.setTag(holder);
    }

    @Override
    protected void SetView(View view, ListItem.Base data, int position) {
        // Gte dtaa and holder
        final ListItem.Task taskItem = (ListItem.Task) data;
        ListHolder.Task holder = (ListHolder.Task) view.getTag();
        Lead lead = (Lead) DbHelper.leadTable.GetById(taskItem.task.leadId);

        // Set title and description
        holder.tvTitle.setText(lead.title);
        holder.tvDescription.setText(lead.address);

        // Set btn lick listeners
        holder.btnForm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onSubmitFormClick(taskItem.task);
                }
            }
        });
    }

    // ListView Overrides
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        // Get List Item Object
        ListItem.Task clickedItem = (ListItem.Task) getItem(position);

        // Call Listener
        if (listener != null) {
            listener.onItemClick(clickedItem.task);
        }
    }

    // ----------------------- Public APIs ----------------------- //
    // ----------------------- Private APIs ----------------------- //
    // ----------------------- Private APIs ----------------------- //
}