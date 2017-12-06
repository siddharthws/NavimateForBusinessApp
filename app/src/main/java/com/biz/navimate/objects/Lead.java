package com.biz.navimate.objects;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Siddharth on 28-09-2017.
 */

public class Lead extends DbObject {
    // ----------------------- Constants ----------------------- //
    private static final String TAG = "LEAD";

    // ----------------------- Globals ----------------------- //
    public String title = "", description = "", phone = "", email = "", address = "";
    public LatLng position = new LatLng(0, 0);

    // ----------------------- Constructor ----------------------- //
    public Lead (long id, long version, String title, String description, String phone, String email, String address, LatLng position) {
        super(DbObject.TYPE_LEAD, version, id);
        this.title = title;
        this.description = description;
        this.phone = phone;
        this.email = email;
        this.address = address;
        this.position = position;
    }
}
