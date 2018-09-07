package com.biz.navimate.lists;

import android.content.Context;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.biz.navimate.R;
import com.biz.navimate.objects.ListItem;
import com.biz.navimate.viewholders.ListHolder;

/**
 * Created by Siddharth on 02-10-2017.
 */

public class RemovableListAdapter   extends     BaseListAdapter
{
    // ----------------------- Constants ----------------------- //
    private static final String TAG = "REMOVABLE_LIST_ADAPTER";

    // ----------------------- Interfaces ----------------------- //
    // ----------------------- Globals ----------------------- //

    // ----------------------- Constructor ----------------------- //
    public RemovableListAdapter(Context parentContext, ListView lvList) {
        super(parentContext, lvList, R.layout.list_removable, false);
    }

    // ----------------------- Overrides ----------------------- //
    @Override
    protected void SetViewHolder(View view) {
        // Init Holder
        ListHolder.Removable holder = new ListHolder.Removable();
        holder.tvTitle  = (TextView)    view.findViewById(R.id.tv_title);
        holder.ibRemove = (ImageButton) view.findViewById(R.id.ib_remove);

        // Assign holder to view
        view.setTag(holder);
    }

    @Override
    protected void SetView(View view, ListItem.Base data, final int position) {
        // Gte dtaa and holder
        ListItem.Lead item = (ListItem.Lead) data;
        ListHolder.Removable holder = (ListHolder.Removable) view.getTag();

        // Set title and description
        holder.tvTitle.setText(item.lead.name);

        // Set btn lick listeners
        holder.ibRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Remove(position);
            }
        });
    }

    // ----------------------- Public APIs ----------------------- //
    // ----------------------- Private APIs ----------------------- //
    // ----------------------- Private APIs ----------------------- //
}
