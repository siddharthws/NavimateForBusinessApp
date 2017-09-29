package com.biz.navimate.lists;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.biz.navimate.debug.Dbg;
import com.biz.navimate.objects.ListItem;

import java.util.ArrayList;

/**
 * Created by Siddharth on 29-09-2017.
 */

public abstract class BaseListAdapter extends BaseAdapter {
    // ----------------------- Constants ----------------------- //
    private static final String TAG = "BASE_LIST_ADAPTER";

    // ----------------------- Interfaces ----------------------- //
    // ----------------------- Globals ----------------------- //
    protected Context parentContext                 = null;
    protected ListView lvList                       = null;
    protected ArrayList<ListItem.Base> data         = null;
    private int itemLayoutId                        = 0;

    // ----------------------- Constructor ----------------------- //
    public BaseListAdapter(Context parentContext, ListView lvList, int itemLayoutId)
    {
        super();

        this.parentContext = parentContext;
        this.lvList = lvList;
        this.data = new ArrayList<>();
        this.itemLayoutId = itemLayoutId;

        // Set List Adapter this this
        lvList.setAdapter(this);
    }

    // ----------------------- Abstracts ----------------------- //
    // Initialize view holder object for list item
    protected abstract void SetViewHolder(View view);

    // Populate UI of list item using list data
    protected abstract void SetView(View view, ListItem.Base data, int position);

    // ----------------------- Overrides ----------------------- //
    // BaseAdapter overrides
    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup)
    {
        // Gte data for this position
        ListItem.Base data = (ListItem.Base) getItem(position);

        // Optimization to avoid inflating views every time list is scrolled
        View rowView = convertView;

        // Inflate View and Init View holder if required
        if (rowView == null)
        {
            // Inflate View
            LayoutInflater inflater = (LayoutInflater) parentContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView = inflater.inflate(itemLayoutId, viewGroup, false);

            // Init View holder
            SetViewHolder(rowView);
        }

        // Populate view with data
        SetView(rowView, data, position);

        return rowView;
    }

    @Override
    public long getItemId(int position)
    {
        ListItem.Base baseItem = (ListItem.Base) getItem(position);

        if (baseItem != null)
        {
            return baseItem.id;
        }

        Dbg.error(TAG, "Cannot get item ID");

        return -1;
    }

    @Override
    public int getCount()
    {
        int count = 0;

        if (data != null)
        {
            count = data.size();
        }

        return count;
    }

    @Override
    public Object getItem(int position)
    {
        if (data.size() > position)
        {
            return data.get(position);
        }

        return null;
    }

    // ----------------------- Public APIs ----------------------- //
    // APIs for data list related operations
    public ArrayList<? extends ListItem.Base> GetList()
    {
        return data;
    }

    public void Clear()
    {
        if (data != null)
        {
            data.clear();
        }

        this.notifyDataSetChanged();
    }

    public void Add(ListItem.Base data)
    {
        if (this.data != null)
        {
            Add(this.data.size(), data);
        }
    }

    public void Add(int position, ListItem.Base data)
    {
        if (this.data != null)
        {
            this.data.add(position, data);
        }

        this.notifyDataSetChanged();
    }

    public void AddAll(ArrayList<? extends ListItem.Base> data)
    {
        if (this.data != null)
        {
            this.data.addAll(data);
        }

        this.notifyDataSetChanged();
    }

    public void Remove(int position)
    {
        if ((position >= 0) && (position < data.size()))
        {
            data.remove(position);
        }

        this.notifyDataSetChanged();
    }

    public void Remove(ListItem.Base item)
    {
        if (data.contains(item))
        {
            data.remove(item);
        }

        this.notifyDataSetChanged();
    }

    public int GetItemPosition(ListItem.Base item)
    {
        int position = 0;

        for (ListItem.Base listItem : data)
        {
            if (listItem.equals(item))
            {
                return position;
            }

            position++;
        }

        return -1;
    }

    // ----------------------- Private APIs ----------------------- //
    // ----------------------- Private APIs ----------------------- //
}
