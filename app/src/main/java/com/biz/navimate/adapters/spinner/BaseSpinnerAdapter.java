package com.biz.navimate.adapters.spinner;

import android.content.Context;
import android.support.v7.widget.AppCompatSpinner;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import com.biz.navimate.objects.ObjSpinner;

import java.util.ArrayList;

public abstract class BaseSpinnerAdapter    extends     BaseAdapter
                                            implements  AdapterView.OnItemSelectedListener {
    // ----------------------- Constants ----------------------- //
    private static final String TAG = "BASE_SPINNER_ADAPTER";

    // ----------------------- Interfaces ----------------------- //
    public interface IfaceSpinner {
        void onItemSelected(int position);
    }
    private IfaceSpinner listener = null;
    public void SetListener(IfaceSpinner listener)
    {
        this.listener = listener;
    }

    // ----------------------- Globals ----------------------- //
    protected Context ctx = null;
    protected AppCompatSpinner spinner = null;
    protected ArrayList<ObjSpinner.Base> data = null;
    private int itemLayoutId = 0;

    // ----------------------- Constructor ----------------------- //
    public BaseSpinnerAdapter(Context ctx, AppCompatSpinner spinner, int itemLayoutId) {
        super();

        this.ctx = ctx;
        this.spinner = spinner;
        this.itemLayoutId = itemLayoutId;
        this.data = new ArrayList<>();

        // Set Spinner properties
        this.spinner.setAdapter(this);
        this.spinner.setOnItemSelectedListener(this);
    }

    // ----------------------- Abstracts ----------------------- //
    // Initialize view holder object for spinner items
    protected abstract void SetViewHolder(View view);

    // Populate UI of item using spinner data
    protected abstract void SetView(View view, ObjSpinner.Base data, int position);

    // ----------------------- Overrides ----------------------- //
    // BaseAdapter overrides
    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        // Get data for this position
        ObjSpinner.Base data = (ObjSpinner.Base) getItem(position);

        // Optimization to avoid inflating views
        View rowView = convertView;

        // Inflate View and Init View holder if required
        if (rowView == null) {
            // Inflate View
            LayoutInflater inflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView = inflater.inflate(itemLayoutId, viewGroup, false);

            // Init View holder
            SetViewHolder(rowView);
        }

        // Populate view with data
        SetView(rowView, data, position);

        return rowView;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getCount() {
        int count = 0;

        if (data != null) {
            count = data.size();
        }

        return count;
    }

    @Override
    public Object getItem(int position) {
        if (data.size() > position) {
            return data.get(position);
        }

        return null;
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        if (listener != null) {
            listener.onItemSelected(i);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) { }

    // ----------------------- Public APIs ----------------------- //
    // APIs for data list related operations
    public ArrayList<? extends ObjSpinner.Base> GetList() {
        return data;
    }

    public void Clear() {
        if (data != null) {
            data.clear();
        }

        this.notifyDataSetChanged();
    }

    public void Add(ObjSpinner.Base data) {
        if (this.data != null) {
            Add(this.data.size(), data);
        }
    }

    public void Add(int position, ObjSpinner.Base data) {
        if (this.data != null) {
            this.data.add(position, data);
        }

        this.notifyDataSetChanged();
    }

    public void AddAll(ArrayList<? extends ObjSpinner.Base> data) {
        if (this.data != null) {
            this.data.addAll(data);
        }

        this.notifyDataSetChanged();
    }

    public void Remove(int position) {
        if ((position >= 0) && (position < data.size())) {
            data.remove(position);
        }

        this.notifyDataSetChanged();
    }

    public void Remove(ObjSpinner.Base item) {
        if (data.contains(item)) {
            data.remove(item);
        }

        this.notifyDataSetChanged();
    }

    public int GetItemPosition(ObjSpinner.Base item) {
        return data.indexOf(item);
    }

    // ----------------------- Private APIs ----------------------- //
}
