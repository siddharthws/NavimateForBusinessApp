package com.biz.navimate.objects;

import com.google.android.gms.maps.model.LatLng;

public class ObjPlace {
    // ----------------------- Constants ----------------------- //
    private static final String TAG = "OBJ_PLACE";

    // ----------------------- Classes ---------------------------//
    // ----------------------- Interfaces ----------------------- //
    // ----------------------- Globals ----------------------- //
    private LatLng latlng    = null;
    private String address   = "";

    // ----------------------- Constructor ----------------------- //
    public ObjPlace(double lat, double lng, String address)
    {
        this.latlng     = new LatLng(lat, lng);
        this.address    = address;
    }

    // ----------------------- Overrides ----------------------- //
    // ----------------------- Public APIs ----------------------- //
    public LatLng GetLatLng() {
        return latlng;
    }

    public String GetAddress() {
        return address;
    }

    // ----------------------- Private APIs ----------------------- //
}