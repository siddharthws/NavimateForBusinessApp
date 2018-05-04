package com.biz.navimate.lists;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.biz.navimate.R;
import com.biz.navimate.interfaces.IfaceList;
import com.biz.navimate.objects.Dialog;
import com.biz.navimate.objects.Form;
import com.biz.navimate.objects.ListItem;
import com.biz.navimate.objects.Task;
import com.biz.navimate.viewholders.ListHolder;
import com.biz.navimate.views.RlDialog;
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
        holder.llRoot = (LinearLayout) view.findViewById(R.id.ll_task_list_root);

        // Assign holder to view
        view.setTag(holder);
    }

    @Override
    protected void SetView(View view, ListItem.Base data, int position) {
        // Gte dtaa and holder
        final ListItem.Task taskItem = (ListItem.Task) data;
        ListHolder.Task holder = (ListHolder.Task) view.getTag();
        Task task = taskItem.task;

        // Set title and description
        holder.tvTitle.setText(task.lead.title);
        holder.tvDescription.setText(task.lead.address);

        // Set different background for Open and Closed tasks
        if (taskItem.task.status == Task.TaskStatus.CLOSED) {
            holder.llRoot.setBackground(ContextCompat.getDrawable(parentContext, R.drawable.bg_off_white_shadow));
        } else {
            holder.llRoot.setBackground(ContextCompat.getDrawable(parentContext, R.drawable.bg_white_shadow_slant));
        }

        // Set btn lick listeners
        holder.btnForm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    // Create new form object
                    Form form = new Form(task);

                    // Open Submit form dialog with this form
                    RlDialog.Show(new Dialog.SubmitForm(form, false));
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