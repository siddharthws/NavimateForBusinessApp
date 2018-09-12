package com.biz.navimate.objects.core;

import android.content.ContentValues;
import android.database.Cursor;

import com.biz.navimate.constants.Constants;
import com.biz.navimate.debug.Dbg;
import com.biz.navimate.misc.Preferences;
import com.biz.navimate.objects.ObjPlace;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Siddharth on 28-09-2017.
 */

public class ObjLead extends ObjNvm {
    // ----------------------- Constants ----------------------- //
    private static final String TAG = "OBJ_LEAD";

    // ----------------------- Globals ----------------------- //
    public String name = "";
    public ObjPlace place = null;
    public long ownerId = 0L;

    // ----------------------- Constructor ----------------------- //
    public ObjLead() {
        super(Constants.Template.TYPE_LEAD);
    }

    public ObjLead(JSONObject json) {
        super(Constants.Template.TYPE_LEAD);
        fromServer(json);
    }

    public ObjLead(Cursor cursor) {
        super(Constants.Template.TYPE_LEAD);
        fromDb(cursor);
    }

    public ObjLead(ObjLead obj) {
        super(obj);

        this.name = obj.name;
        this.place = obj.place != null ? new ObjPlace(obj.place.lat, obj.place.lng, obj.place.address) : null;
        this.ownerId = obj.ownerId;
    }

    // ----------------------- Overrides ----------------------- //
    // Converter methods for JSON
    @Override
    public void fromServer(JSONObject json) {
        super.fromServer(json);

        try {
            name                = json.getString(Constants.Server.KEY_NAME);
            ownerId             = json.getJSONObject(Constants.Server.KEY_OWNER).getLong(Constants.Server.KEY_ID);

            String address      = json.getString(Constants.Server.KEY_ADDRESS);
            double latitude     = json.getDouble(Constants.Server.KEY_LAT);
            double longitude    = json.getDouble(Constants.Server.KEY_LNG);
            place = new ObjPlace(latitude, longitude, address);
        } catch (Exception e) {
            Dbg.error(TAG, "Exception while converting from JSON");
            Dbg.stack(e);
        }
    }

    @Override
    public JSONObject toServer() {
        JSONObject json = super.toServer();

        try {
            json.put(Constants.Server.KEY_NAME,     name);

            json.put(Constants.Server.KEY_LAT,      place.lat);
            json.put(Constants.Server.KEY_LNG,      place.lng);
            json.put(Constants.Server.KEY_ADDRESS,  place.address);
        } catch (JSONException e) {
            Dbg.error(TAG, "Error while putting data in json");
            Dbg.stack(e);
        }

        return json;
    }

    // Converter methods for database
    @Override
    public void fromDb(Cursor cursor) {
        super.fromDb(cursor);

        name                    = cursor.getString  (cursor.getColumnIndex(Constants.DB.COLUMN_TITLE));
        ownerId                 = cursor.getLong    (cursor.getColumnIndex(Constants.DB.COLUMN_OWNER_ID));

        String address          = cursor.getString  (cursor.getColumnIndex(Constants.DB.COLUMN_ADDRESS));
        double latitude         = cursor.getDouble  (cursor.getColumnIndex(Constants.DB.COLUMN_LATITUDE));
        double longitude        = cursor.getDouble  (cursor.getColumnIndex(Constants.DB.COLUMN_LONGITUDE));
        place = new ObjPlace(latitude, longitude, address);
    }

    @Override
    public ContentValues toDb() {
        ContentValues cv = super.toDb();

        // Enter values into Database
        cv.put(Constants.DB.COLUMN_TITLE,           name);
        cv.put(Constants.DB.COLUMN_OWNER_ID,        ownerId);

        cv.put(Constants.DB.COLUMN_ADDRESS,         place.address);
        cv.put(Constants.DB.COLUMN_LATITUDE,        place.lat);
        cv.put(Constants.DB.COLUMN_LONGITUDE,       place.lng);

        return cv;
    }

    // ----------------------- Public APIs ----------------------- //
    public boolean isOwned() {
        return ownerId == 0 || ownerId == Preferences.GetUser().appId;
    }
}
