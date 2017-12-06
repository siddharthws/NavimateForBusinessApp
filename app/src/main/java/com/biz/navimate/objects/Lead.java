package com.biz.navimate.objects;

import com.biz.navimate.constants.Constants;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

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

    // ----------------------- Public APIs ----------------------- //
    public static Lead FromJson(JSONObject leadJson) throws JSONException {
        long dbId           = leadJson.getLong(Constants.Server.KEY_ID);
        long version        = leadJson.getLong(Constants.Server.KEY_VERSION);
        String title        = leadJson.getString(Constants.Server.KEY_TITLE);
        String description  = leadJson.getString(Constants.Server.KEY_DESCRIPTION);
        String phone        = leadJson.getString(Constants.Server.KEY_PHONE);
        String email        = leadJson.getString(Constants.Server.KEY_EMAIL);
        String address      = leadJson.getString(Constants.Server.KEY_ADDRESS);
        double latitude     = leadJson.getDouble(Constants.Server.KEY_LATITUDE);
        double longitude    = leadJson.getDouble(Constants.Server.KEY_LONGITUDE);

        return new Lead(dbId, version, title, description, phone, email, address, new LatLng(latitude, longitude));
    }
}
