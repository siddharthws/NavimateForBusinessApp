package com.biz.navimate.maps;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.SupportMapFragment;
import com.biz.navimate.views.FlTouchWrapper;

/**
 * Custom Support map fragment class to make the map view touchable.
 * By default touch events cannot be detected on the map view.
 */

public class TouchableSupportMapFragment    extends         SupportMapFragment
                                            implements      FlTouchWrapper.IfaceTouchListener
{
    // ----------------------- Constants ----------------------- //
    private static final String TAG = "TOUCHABLE_SUPPORT_MAP_FRAGMENT";

    // ----------------------- Classes ---------------------------//
    // ----------------------- Interfaces ----------------------- //
    // ----------------------- Globals ----------------------- //
    public View                 mapContentView  = null;
    public FlTouchWrapper       flTouchWrapper = null;

    // ----------------------- Constructor ----------------------- //
    // ----------------------- Overrides ----------------------- //
    // ----------------------- Public APIs ----------------------- //
// ----------------------- Private APIs ----------------------- //

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState)
    {
        // Get Default Map View created by SupportMapFragment
        mapContentView = super.onCreateView(inflater, parent, savedInstanceState);

        // Wrap map view in a touch wrapper
        flTouchWrapper = new FlTouchWrapper(getActivity());
        flTouchWrapper.SetTouchListener(this);
        flTouchWrapper.addView(mapContentView);
        return flTouchWrapper;
    }

    @Override
    public View getView()
    {
        return mapContentView;
    }

    @Override
    public void onTouch()
    {
        // Perform map touch operation here.
    }
}
