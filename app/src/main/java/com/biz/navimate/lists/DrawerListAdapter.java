package com.biz.navimate.lists;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.biz.navimate.R;
import com.biz.navimate.objects.ListItem;
import com.biz.navimate.viewholders.ListHolder;
import com.biz.navimate.views.TvCalibri;

/**
 * Created by Siddharth on 23-11-2016.
 */

public class DrawerListAdapter extends BaseListAdapter
{
    // ----------------------- Constants ----------------------- //
    private static final String TAG = "DRAWER_LIST_ADAPTER";

    // ----------------------- Globals ----------------------- //
    // ----------------------- Constructor ----------------------- //
    public DrawerListAdapter(Context parentContext, ListView lvDrawer)
    {
        super(parentContext, lvDrawer, R.layout.list_item_drawer);
    }

    // ----------------------- Overrides ----------------------- //
    @Override
    protected void SetViewHolder(View view)
    {
        // Init Holder
        ListHolder.Drawer holder = new ListHolder.Drawer();
        holder.rlDrawerItem         = (RelativeLayout) view.findViewById(R.id.rl_drawer_item);
        holder.ivIcon               = (ImageView) view.findViewById(R.id.ivDrawerIcon);
        holder.tvTitle              = (TvCalibri) view.findViewById(R.id.tvDrawerTitle);
        holder.vwItemSep            =  view.findViewById(R.id.vw_drawer_item_separator);
        holder.vwGroupSep           =  view.findViewById(R.id.vw_drawer_group_separator);

        // Assign holder to view
        view.setTag(holder);
    }

    @Override
    protected void SetView(View view, ListItem.Base data, int position)
    {
        ListItem.Drawer itemData = (ListItem.Drawer) data;
        ListHolder.Drawer   holder = (ListHolder.Drawer) view.getTag();

        // SetAlarm View properties
        if (itemData.type == ListItem.TYPE_DRAWER_GROUP_SEPARATOR)
        {
            // Separator View
            holder.vwItemSep.setVisibility(View.GONE);
            holder.vwGroupSep.setVisibility(View.VISIBLE);
            holder.rlDrawerItem.setVisibility(View.GONE);
        }
        else if (itemData.type == ListItem.TYPE_DRAWER_ITEM_SEPARATOR)
        {
            // Separator View
            holder.vwItemSep.setVisibility(View.VISIBLE);
            holder.vwGroupSep.setVisibility(View.GONE);
            holder.rlDrawerItem.setVisibility(View.GONE);
        }
        else
        {
            // Drawer Item View
            holder.vwItemSep.setVisibility(View.GONE);
            holder.vwGroupSep.setVisibility(View.GONE);
            holder.rlDrawerItem.setVisibility(View.VISIBLE);

            // Set image and title
            holder.ivIcon.setImageDrawable(ContextCompat.getDrawable(parentContext, itemData.imageId));
            holder.tvTitle.setText(itemData.title);
        }
    }

    // ----------------------- Public APIs ----------------------- //
    // ----------------------- Private APIs ----------------------- //
}
