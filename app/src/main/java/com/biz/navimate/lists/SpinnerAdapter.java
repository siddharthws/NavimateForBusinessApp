package com.biz.navimate.lists;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import java.util.ArrayList;

/**
 * Created by Sai_Kameswari on 07-02-2018.
 */

public class SpinnerAdapter extends ArrayAdapter<String> implements OnItemSelectedListener {
    // ----------------------- Constants ----------------------- //
    private static final String TAG = "SPINNER_ADAPTER";

    // ----------------------- Classes ---------------------------//
    // ----------------------- Interfaces ----------------------- //
    public interface IfaceSpinner
    {
        void onItemSelected(long id);
    }
    private IfaceSpinner listener = null;

    public void SetListener(IfaceSpinner listener)
    {
        this.listener = listener;
    }
    // ----------------------- Globals ----------------------- //
    ArrayList<Long> itemIds = null;

    // ----------------------- Constructor ----------------------- //
    public SpinnerAdapter(Context context, Spinner spinner) {
        super(context, android.R.layout.simple_spinner_item);

        //Init Spinner Adapter
        setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(this);
        spinner.setOnItemSelectedListener(this);
        itemIds = new ArrayList<>();
    }

    // ----------------------- Overrides ----------------------- //
    @Override
    public boolean isEnabled(int position){
        if(position == 0)
        {
            // Disable the first item from Spinner
            // First item will be use for hint
            return false;
        }
        else
        {
            return true;
        }
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        View view = super.getDropDownView(position, convertView, parent);
        TextView tv = (TextView) view;

        //Make Descriptive Item Grey and others selectable
        if(position == 0){
            tv.setTextColor(Color.GRAY);
        }
        else {
            tv.setTextColor(Color.BLACK);
        }
        return view;
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        //Return if descriptive item is selected
        if(i == 0)
        {
            return;
        }

        //Send selected item ID to parent's listener
        long selectedId = itemIds.get(i);
        listener.onItemSelected(selectedId);
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {}
    // ----------------------- Public APIs ----------------------- //

    //API to add new Item to Adapter
    public void AddItem(String name, long id)
    {
        add(name);
        itemIds.add(id);
    }
    // ----------------------- Private APIs ----------------------- //
}
