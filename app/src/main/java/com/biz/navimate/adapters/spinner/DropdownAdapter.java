package com.biz.navimate.adapters.spinner;

import android.content.Context;
import android.support.v7.widget.AppCompatSpinner;
import android.view.View;
import android.widget.TextView;

import com.biz.navimate.R;
import com.biz.navimate.objects.ObjSpinner;
import com.biz.navimate.viewholders.SpinnerHolder;

public class DropdownAdapter extends BaseSpinnerAdapter {
    // ----------------------- Constants ----------------------- //
    private static final String TAG = "DROPDOWN_ADAPTER";

    // ----------------------- Globals ----------------------- //
    // ----------------------- Constructor ----------------------- //
    public DropdownAdapter(Context ctx, AppCompatSpinner spinner) {
        super(ctx, spinner, R.layout.spinner_dropdown);
    }

    // ----------------------- Overrides ----------------------- //
    @Override
    protected void SetViewHolder(View view) {
        // Init Holder
        SpinnerHolder.Dropdown holder = new SpinnerHolder.Dropdown();
        holder.tvText = (TextView) view.findViewById(R.id.tv_text);

        // Assign holder to view
        view.setTag(holder);
    }

    @Override
    protected void SetView(View view, ObjSpinner.Base data, int position) {
        // Get data and view holder
        ObjSpinner.Dropdown dropdownItem = (ObjSpinner.Dropdown) data;
        SpinnerHolder.Dropdown holder = (SpinnerHolder.Dropdown) view.getTag();

        // Set Item text
        holder.tvText.setText(dropdownItem.text);
    }

    // ----------------------- Public APIs ----------------------- //
    // ----------------------- Private APIs ----------------------- //
}
