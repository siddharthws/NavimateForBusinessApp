package com.biz.navimate.fragments;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.biz.navimate.R;
import com.biz.navimate.debug.Dbg;
import com.biz.navimate.maps.MarkerHelper;
import com.biz.navimate.maps.TouchableSupportMapFragment;
import com.biz.navimate.objects.Statics;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;

/**
 * Created by Siddharth on 28-09-2017.
 */

public class NvmMapFragment     extends     BaseFragment
                                implements  OnMapReadyCallback,
                                            GoogleMap.OnMapLoadedCallback
{
    // ----------------------- Constants ----------------------- //
    private final static String TAG = "NVM_MAP_FRAGMENT";

    // Fragment Identifier
    public static final String  FRAGMENT_IDENTIFIER     = "nvm_map_fragment";
    public static final int     FRAGMENT_LAYOUT_ID      = R.id.fl_map_fragment;

    // ----------------------- Interfaces ----------------------- //
    // ----------------------- Globals ----------------------- //
    private TouchableSupportMapFragment supportMapFragment         = null;
    private GoogleMap                   googleMap                   = null;

    // Helpers
    public MarkerHelper markerHelper                                = null;

    // ----------------------- Constructor ----------------------- //
    public NvmMapFragment()
    {
        super();
    }

    // Factory method to create new instance of fragment
    public static NvmMapFragment newInstance()
    {
        // Init Map fragment properties and return object
        NvmMapFragment fragment    = new NvmMapFragment();

        // Init Helpers
        fragment.markerHelper = new MarkerHelper();

        return fragment;
    }

    // ----------------------- Overrides ----------------------- //
    // Fragment related overrides
    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState)
    {
        View fragmentView = inflater.inflate(R.layout.fragment_nvm_map, container, false);

        // Init UI
        supportMapFragment             = (TouchableSupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map_fragment);

        // Ready the map
        supportMapFragment.getMapAsync(this);

        return fragmentView;
    }

    // Map Related Overrides
    /* Called when map is ready on screen
     */
    @Override
    public void onMapReady(GoogleMap googleMap)
    {
        // Init Google map object
        this.googleMap = googleMap;

        // Set callbacks
        googleMap.setOnMapLoadedCallback(this);

        // Set compass to bottom left (else it gets hidden by toolbar)
        InitMapCompassButton();
    }

    // Called when map has been loaded on the screen
    @Override
    public void onMapLoaded()
    {
        // Load markers on map
        markerHelper.LoadMap(googleMap);
    }

    // ----------------------- Public APIs ----------------------- //
    // API to add this fragment to an activity
    public static NvmMapFragment AddFragment(FragmentManager fm)
    {
        NvmMapFragment fragment = null;

        if (fm.findFragmentByTag(FRAGMENT_IDENTIFIER) != null)
        {
            // If fragment is already added, return the added instance
            fragment = (NvmMapFragment) fm.findFragmentByTag(FRAGMENT_IDENTIFIER);
        }
        else
        {
            // Create new instance
            fragment = NvmMapFragment.newInstance();

            // Add to fm
            fm.beginTransaction().add(FRAGMENT_LAYOUT_ID, fragment, FRAGMENT_IDENTIFIER).commit();
        }

        return fragment;
    }

    // ----------------------- Private APIs ----------------------- //
    private void InitMapCompassButton()
    {
        try
        {
            // Get Map View
            View mapView = supportMapFragment.getView();
            if (mapView != null)
            {
                // Get Parent of map
                final ViewGroup mapParent = (ViewGroup) mapView.findViewWithTag("GoogleMapMyLocationButton").getParent();

                mapParent.post(new Runnable() {
                    @Override
                    public void run()
                    {
                        try
                        {
                            // Ste all map buttons to bottom left
                            for (int i = 0, n = mapParent.getChildCount(); i < n; i++)
                            {
                                // Get child's layout params
                                View view = mapParent.getChildAt(i);
                                RelativeLayout.LayoutParams rlp = (RelativeLayout.LayoutParams) view.getLayoutParams();

                                // position on right bottom
                                rlp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                                rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
                                rlp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, 0);
                                rlp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);

                                // Set margin from screen edge
                                rlp.leftMargin      = Statics.GetPxFromDip(20);
                                rlp.bottomMargin    = Statics.GetPxFromDip(20);

                                // Reset Layout
                                view.requestLayout();
                            }
                        }
                        catch (Exception ex)
                        {
                            Dbg.error(TAG, "Exception while initializing compass");
                            Dbg.stack(ex);
                        }
                    }
                });
            }
        }
        catch (Exception ex)
        {
            Dbg.error(TAG, "Exception while initializing compass");
            Dbg.stack(ex);
        }
    }
}
