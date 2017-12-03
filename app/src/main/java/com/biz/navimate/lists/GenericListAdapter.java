package com.biz.navimate.lists;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;

import com.biz.navimate.R;
import com.biz.navimate.objects.ListItem;
import com.biz.navimate.viewholders.ListHolder;
import com.biz.navimate.views.TvCalibri;

/**
 * Created by Siddharth on 17-02-2017.
 */

public class GenericListAdapter extends BaseListAdapter
{
    // ----------------------- Constants ----------------------- //
    private static final String TAG = "GENERIC_LIST_ADAPTER";

    // ----------------------- Interfaces ----------------------- //
    // ----------------------- Globals ----------------------- //

    // ----------------------- Constructor ----------------------- //
    public GenericListAdapter(Context parentContext, ListView lvList)
    {
        super(parentContext, lvList, R.layout.list_generic);
    }

    // ----------------------- Overrides ----------------------- //
    @Override
    protected void SetViewHolder(View view)
    {
        // Init Holder
        ListHolder.Generic holder = new ListHolder.Generic();
        holder.tvText       = (TvCalibri)   view.findViewById(R.id.tv_text);
        holder.ivStart      = (ImageView)   view.findViewById(R.id.ivListIconStart);
        holder.ivEnd        = (ImageView)   view.findViewById(R.id.ivListIconEnd);

        // Assign holder to view
        view.setTag(holder);
    }

    @Override
    protected void SetView(View view, ListItem.Base data, int position)
    {
        ListItem.Generic    genericItem = (ListItem.Generic) data;
        ListHolder.Generic  holder      = (ListHolder.Generic) view.getTag();

        // Set background given by caller
        if (genericItem.background != 0)
        {
            view.setBackground(ContextCompat.getDrawable(parentContext, genericItem.background));
        }

        // Set Item text
        holder.tvText.setText(genericItem.title);

        // Set item images
        if (genericItem.startImageId != 0)
        {
            holder.ivStart.setVisibility(View.VISIBLE);
            holder.ivStart.setImageDrawable(ContextCompat.getDrawable(parentContext, genericItem.startImageId));
        }
        else
        {
            holder.ivStart.setVisibility(View.GONE);
        }

        if (genericItem.endImageId != 0)
        {
            holder.ivEnd.setVisibility(View.VISIBLE);
            holder.ivEnd.setImageDrawable(ContextCompat.getDrawable(parentContext, genericItem.endImageId));
        }
        else
        {
            holder.ivEnd.setVisibility(View.GONE);
        }
    }

    // ----------------------- Public APIs ----------------------- //
    // ----------------------- Private APIs ----------------------- //
}
