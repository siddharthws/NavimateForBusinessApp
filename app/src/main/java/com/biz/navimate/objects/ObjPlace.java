package com.biz.navimate.objects;

import com.google.android.gms.maps.model.LatLng;

public class ObjPlace {
    // ----------------------- Constants ----------------------- //
    private static final String TAG = "OBJ_PLACE";

    // ----------------------- Classes ---------------------------//
    // ----------------------- Interfaces ----------------------- //
    // ----------------------- Globals ----------------------- //
    public double lat = 0, lng = 0;
    public String address   = "";

    // ----------------------- Constructor ----------------------- //
    public ObjPlace() { }

    public ObjPlace(double lat, double lng, String address) {
        this.lat        = lat;
        this.lng        = lng;
        this.address    = address;
    }

    // ----------------------- Overrides ----------------------- //
    // ----------------------- Public APIs ----------------------- //
    public LatLng GetLatLng() {
        return new LatLng(lat, lng);
    }

    public boolean isValid() { return (lat != 0 || lng != 0) && (address.length() > 0); }

    // ----------------------- Private APIs ----------------------- //
}