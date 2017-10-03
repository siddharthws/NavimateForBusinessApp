package com.biz.navimate.objects;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Siddharth on 28-09-2017.
 */

public class Lead implements Serializable {
    // ----------------------- Constants ----------------------- //
    private static final String TAG = "LEAD";

    public static final int INVALID_ID = -1;

    // ----------------------- Globals ----------------------- //
    public int  id          = INVALID_ID;
    public String title = "", description = "", phone = "", email = "", address = "";
    public LatLng position = new LatLng(0, 0);

    // ----------------------- Constructor ----------------------- //
    public Lead () {}

    public Lead (int id, String title, String description, String phone, String email, String address, LatLng position) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.phone = phone;
        this.email = email;
        this.address = address;
        this.position = position;
    }

    @Override
    public boolean equals(Object object)
    {
        boolean bEqual = false;

        // Validate Object Instance
        if ((object != null) && (object instanceof Lead))
        {
            // Cast object to compare
            Lead compareObject = (Lead) object;

            if (compareObject.title.equals(title) && compareObject.description.equals(description) && compareObject.phone.equals(phone) && compareObject.email.equals(email)) {
                bEqual = true;
            }
        }

        return bEqual;
    }
}
