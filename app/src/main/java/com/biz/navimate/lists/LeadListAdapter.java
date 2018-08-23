package com.biz.navimate.lists;

import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.biz.navimate.R;
import com.biz.navimate.interfaces.IfaceList;
import com.biz.navimate.objects.ListItem;
import com.biz.navimate.viewholders.ListHolder;

/**
 * Created by Siddharth on 02-10-2017.
 */

public class LeadListAdapter    extends     BaseListAdapter
                                implements  AdapterView.OnItemClickListener
{
    // ----------------------- Constants ----------------------- //
    private static final String TAG = "LEAD_LIST_ADAPTER";

    // ----------------------- Interfaces ----------------------- //
    private IfaceList.Lead listener = null;

    public void SetListener(IfaceList.Lead listener) {
        this.listener = listener;
    }

    // ----------------------- Globals ----------------------- //

    // ----------------------- Constructor ----------------------- //
    public LeadListAdapter(Context parentContext, ListView lvList) {
        super(parentContext, lvList, R.layout.list_lead);

        // Set List Click Listener
        lvList.setOnItemClickListener(this);
    }

    // ----------------------- Overrides ----------------------- //
    @Override
    protected void SetViewHolder(View view) {
        // Init Holder
        ListHolder.Lead holder = new ListHolder.Lead();

        holder.tvTitle  = (TextView)    view.findViewById(R.id.tv_title);
        holder.ivTick   = (ImageView)   view.findViewById(R.id.iv_tick);

        // Assign holder to view
        view.setTag(holder);
    }

    @Override
    protected void SetView(View view, ListItem.Base data, int position) {
        // Gte dtaa and holder
        final ListItem.Lead leadItem = (ListItem.Lead) data;
        ListHolder.Lead holder = (ListHolder.Lead) view.getTag();

        // Set title and description
        holder.tvTitle.setText(leadItem.lead.title);

        if (leadItem.bSelected) {
            holder.ivTick.setVisibility(View.VISIBLE);
        } else {
            holder.ivTick.setVisibility(View.GONE);
        }
    }

    // ListView Overrides
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        // Get List Item Object
        ListItem.Lead clickedItem = (ListItem.Lead) getItem(position);

        // Call Listener
        if (listener != null) {
            listener.onItemClick(clickedItem.lead);
        }
    }

    // ----------------------- Public APIs ----------------------- //
    // ----------------------- Private APIs ----------------------- //
    // ----------------------- Private APIs ----------------------- //
}
