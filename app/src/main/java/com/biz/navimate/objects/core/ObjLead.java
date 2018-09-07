package com.biz.navimate.objects.core;

import android.content.ContentValues;
import android.database.Cursor;

import com.biz.navimate.constants.Constants;
import com.biz.navimate.database.DbHelper;
import com.biz.navimate.debug.Dbg;
import com.biz.navimate.misc.Preferences;
import com.biz.navimate.objects.Field;
import com.biz.navimate.objects.FormEntry;
import com.biz.navimate.objects.ObjPlace;
import com.biz.navimate.objects.ServerObject;
import com.biz.navimate.objects.Template;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Siddharth on 28-09-2017.
 */

public class ObjLead extends ServerObject {
    // ----------------------- Constants ----------------------- //
    private static final String TAG = "LEAD";

    // ----------------------- Globals ----------------------- //
    public String textServerId = "";
    public String name = "";
    public ObjPlace place = null;
    public long ownerId = 0L;
    public Template template = null;
    public ArrayList<FormEntry.Base> values = new ArrayList<>();

    // ----------------------- Constructor ----------------------- //
    public ObjLead(JSONObject json) {
        super(Constants.Misc.ID_INVALID, Constants.Misc.ID_INVALID);
        fromJson(json);
    }

    public ObjLead(Cursor cursor) {
        super(Constants.Misc.ID_INVALID, Constants.Misc.ID_INVALID);
        fromCursor(cursor);
    }

    // ----------------------- Public APIs ----------------------- //
    //
    // Converter methods for JSON
    //
    public void fromJson(JSONObject json) {
        try {
            textServerId        = json.getString(Constants.Server.KEY_ID);
            name                = json.getString(Constants.Server.KEY_NAME);

            String address      = json.getString(Constants.Server.KEY_ADDRESS);
            double latitude     = json.getDouble(Constants.Server.KEY_LAT);
            double longitude    = json.getDouble(Constants.Server.KEY_LNG);
            place = new ObjPlace(latitude, longitude, address);

            ownerId             = json.getJSONObject(Constants.Server.KEY_OWNER).getLong(Constants.Server.KEY_ID);

            // Get Local Template Object
            long templateId         = json.getLong(Constants.Server.KEY_TEMPLATE_ID);
            template = DbHelper.templateTable.GetByServerId(templateId);

            // Parse values into Form Entry objects
            JSONArray valuesJson    = json.getJSONArray(Constants.Server.KEY_VALUES);
            values = new ArrayList<>();
            for (int i = 0; i < valuesJson.length(); i++) {
                JSONObject valueJson = valuesJson.getJSONObject(i);

                // Get field and string value
                Long fieldId = valueJson.getLong(Constants.Server.KEY_FIELD_ID);
                Field field = (Field) DbHelper.fieldTable.GetByServerId(fieldId);
                String value = valueJson.getString(Constants.Server.KEY_VALUE);

                // Add form entry object
                values.add(FormEntry.Parse(field, value));
            }
        } catch (Exception e) {
            Dbg.error(TAG, "Exception while converting from JSON");
            Dbg.stack(e);
        }
    }

    //
    // Converter methods for database
    //
    public void fromCursor(Cursor cursor) {
        super.fromDb(cursor);

        textServerId            = cursor.getString  (cursor.getColumnIndex(Constants.DB.COLUMN_SRV_ID));
        name                    = cursor.getString  (cursor.getColumnIndex(Constants.DB.COLUMN_TITLE));

        String address          = cursor.getString  (cursor.getColumnIndex(Constants.DB.COLUMN_ADDRESS));
        double latitude         = cursor.getDouble  (cursor.getColumnIndex(Constants.DB.COLUMN_LATITUDE));
        double longitude        = cursor.getDouble  (cursor.getColumnIndex(Constants.DB.COLUMN_LONGITUDE));
        place = new ObjPlace(latitude, longitude, address);

        ownerId                 = cursor.getLong    (cursor.getColumnIndex(Constants.DB.COLUMN_OWNER_ID));

        long   templateId       = cursor.getLong    (cursor.getColumnIndex(Constants.DB.COLUMN_TEMPLATE_ID));
        template       = (Template) DbHelper.templateTable.GetById(templateId);

        // Parse values into Form Entry objects
        values = new ArrayList<>();
        try {
            JSONArray valuesJson    = new JSONArray(cursor.getString(cursor.getColumnIndex(Constants.DB.COLUMN_VALUES)));
            for (int i = 0; i < valuesJson.length(); i++) {
                JSONObject valueJson = valuesJson.getJSONObject(i);

                // Get field and string value
                Long fieldId = valueJson.getLong(Constants.Server.KEY_FIELD_ID);
                Field field = (Field) DbHelper.fieldTable.GetById(fieldId);
                String value = valueJson.getString(Constants.Server.KEY_VALUE);

                // Add form entry object
                values.add(FormEntry.Parse(field, value));
            }
        } catch (JSONException e) {
            Dbg.error(TAG, "Error while parsing task values");
            Dbg.stack(e);
        }
    }

    public ContentValues toContentValues () {
        ContentValues cv = super.toDb();

        // Enter values into Database
        cv.put(Constants.DB.COLUMN_SRV_ID,          textServerId);
        cv.put(Constants.DB.COLUMN_TITLE,           name);
        cv.put(Constants.DB.COLUMN_ADDRESS,         place.address);
        cv.put(Constants.DB.COLUMN_LATITUDE,        place.lat);
        cv.put(Constants.DB.COLUMN_LONGITUDE,       place.lng);
        cv.put(Constants.DB.COLUMN_OWNER_ID,        ownerId);
        cv.put(Constants.DB.COLUMN_TEMPLATE_ID,     template.dbId);

        // Prepare JSON Array for values
        JSONArray valuesJson = new JSONArray();
        try {
            for(FormEntry.Base value : values) {
                JSONObject valueJson = new JSONObject();
                valueJson.put(Constants.Server.KEY_FIELD_ID, value.field.dbId);
                valueJson.put(Constants.Server.KEY_VALUE, value.toString());
                valuesJson.put(valueJson);
            }
        } catch (JSONException e) {
            Dbg.error(TAG, "JSON Exception while converting to DB string");
            Dbg.stack(e);
        }
        cv.put(Constants.DB.COLUMN_VALUES,          valuesJson.toString());

        return cv;
    }

    public boolean isOwned() {
        return ownerId == 0 || ownerId == Preferences.GetUser().appId;
    }
}
