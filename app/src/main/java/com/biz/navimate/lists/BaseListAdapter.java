package com.biz.navimate.lists;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;

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
    private boolean bRequireHeightAdjustment        = false;

    // ----------------------- Constructor ----------------------- //
    public BaseListAdapter(Context parentContext, ListView lvList, int itemLayoutId, boolean bRequireHeightAdjustment)
    {
        super();

        this.parentContext = parentContext;
        this.lvList = lvList;
        this.data = new ArrayList<>();
        this.itemLayoutId = itemLayoutId;
        this.bRequireHeightAdjustment = bRequireHeightAdjustment;

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
            SetHeightBasedOnChildren();
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
            SetHeightBasedOnChildren();
        }

        this.notifyDataSetChanged();
    }

    public void AddAll(ArrayList<? extends ListItem.Base> data)
    {
        if (this.data != null)
        {
            this.data.addAll(data);
            SetHeightBasedOnChildren();
        }

        this.notifyDataSetChanged();
    }

    public void Remove(int position)
    {
        if ((position >= 0) && (position < data.size()))
        {
            data.remove(position);
            SetHeightBasedOnChildren();
        }

        this.notifyDataSetChanged();
    }

    public void Remove(ListItem.Base item)
    {
        if (data.contains(item))
        {
            data.remove(item);
            SetHeightBasedOnChildren();
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
    private void SetHeightBasedOnChildren() {
        // ignore if not required
        if (!bRequireHeightAdjustment) {
            return;
        }

        int desiredWidth = View.MeasureSpec.makeMeasureSpec(lvList.getWidth(), View.MeasureSpec.UNSPECIFIED);
        int totalHeight = 0;
        View view = null;
        for (int i = 0; i < getCount(); i++) {
            view = getView(i, view, lvList);
            if (i == 0)
                view.setLayoutParams(new ViewGroup.LayoutParams(desiredWidth, RelativeLayout.LayoutParams.WRAP_CONTENT));

            view.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += view.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = lvList.getLayoutParams();
        params.height = totalHeight + (lvList.getDividerHeight() * (getCount() - 1));
        lvList.setLayoutParams(params);
    }
}
