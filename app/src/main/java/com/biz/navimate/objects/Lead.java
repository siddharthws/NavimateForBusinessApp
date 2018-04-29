package com.biz.navimate.objects;

import com.biz.navimate.constants.Constants;
import com.biz.navimate.database.DbHelper;
import com.biz.navimate.debug.Dbg;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Siddharth on 28-09-2017.
 */

public class Lead extends ServerObject {
    // ----------------------- Constants ----------------------- //
    private static final String TAG = "LEAD";

    // ----------------------- Globals ----------------------- //
    public String textServerId = "";
    public String title = "", address = "";
    public Template template = new Template();
    public LatLng position = new LatLng(0, 0);
    public ArrayList<FormEntry.Base> values = new ArrayList<>();

    // ----------------------- Constructor ----------------------- //
    public Lead () {
        super(DbObject.TYPE_TEMPLATE, Constants.Misc.ID_INVALID, Constants.Misc.ID_INVALID, Constants.Misc.ID_INVALID);
    }

    public Lead (long dbId, String serverId, String title, String address, LatLng position, Template template, ArrayList<FormEntry.Base> values) {
        super(DbObject.TYPE_LEAD, dbId, Constants.Misc.ID_INVALID, Constants.Misc.ID_INVALID);
        this.textServerId = serverId;
        this.title = title;
        this.address = address;
        this.position = position;
        this.template = template;
        this.values = values;
    }

    // ----------------------- Public APIs ----------------------- //
    public static Lead FromJson(JSONObject leadJson) throws JSONException {
        String serverId      = leadJson.getString(Constants.Server.KEY_ID);
        String name         = leadJson.getString(Constants.Server.KEY_NAME);
        String address      = leadJson.getString(Constants.Server.KEY_ADDRESS);
        double latitude     = leadJson.getDouble(Constants.Server.KEY_LAT);
        double longitude    = leadJson.getDouble(Constants.Server.KEY_LNG);

        // Get Local Template Object
        long templateId         = leadJson.getLong(Constants.Server.KEY_TEMPLATE_ID);
        Template template = DbHelper.templateTable.GetByServerId(templateId);

        // Parse values into Form Entry objects
        JSONArray valuesJson    = leadJson.getJSONArray(Constants.Server.KEY_VALUES);
        ArrayList<FormEntry.Base> values = new ArrayList<>();
        for (int i = 0; i < valuesJson.length(); i++) {
            JSONObject valueJson = valuesJson.getJSONObject(i);

            // Get field and string value
            Long fieldId = valueJson.getLong(Constants.Server.KEY_FIELD_ID);
            Field field = (Field) DbHelper.fieldTable.GetByServerId(fieldId);
            String value = valueJson.getString(Constants.Server.KEY_VALUE);

            // Add form entry object
            values.add(FormEntry.Parse(field, value));
        }

        // Get DbId
        long dbId = Constants.Misc.ID_INVALID;
        Lead existingLead = DbHelper.leadTable.GetByServerId(serverId);
        if (existingLead != null) {
            dbId = existingLead.dbId;
        }

        return new Lead(dbId, serverId, name, address, new LatLng(latitude, longitude), template, values);
    }
}
