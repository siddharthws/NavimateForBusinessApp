package com.biz.navimate.objects.core;

import android.content.ContentValues;
import android.database.Cursor;

import com.biz.navimate.constants.Constants;
import com.biz.navimate.database.DbHelper;
import com.biz.navimate.debug.Dbg;
import com.biz.navimate.objects.Field;
import com.biz.navimate.objects.Template;
import com.biz.navimate.objects.templating.fieldvalues.FieldValue;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public abstract class ObjNvm extends ObjDb {
    // ----------------------- Constants ----------------------- //
    private static final String TAG = "OBJ_NVM";

    // ----------------------- Globals ----------------------- //
    public int type = Constants.Template.TYPE_INVALID;

    public boolean bDirty = false;

    public String serverId = "";

    public Template template = null;
    public ArrayList<FieldValue> values = null;

    // ----------------------- Constructor ----------------------- //
    public ObjNvm(int type) {
        super();

        this.type = type;
        values = new ArrayList<>();

        // Set template
        template = DbHelper.templateTable.GetByType(type).get(0);
        SetTemplate(template);
    }

    // ----------------------- Overrides ----------------------- //
    // DB Converter methods
    @Override
    public void fromDb(Cursor cursor) {
        super.fromDb(cursor);

        bDirty = Boolean.valueOf(cursor.getString(cursor.getColumnIndex(Constants.DB.COLUMN_DIRTY)));

        serverId = cursor.getString  (cursor.getColumnIndex(Constants.DB.COLUMN_SRV_ID));

        long   templateId   = cursor.getLong    (cursor.getColumnIndex(Constants.DB.COLUMN_TEMPLATE_ID));
        template            = (Template) DbHelper.templateTable.GetById(templateId);

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
                values.add(FieldValue.newInstance(field, value));
            }
        } catch (JSONException e) {
            Dbg.error(TAG, "Error while parsing task values");
            Dbg.stack(e);
        }
    }

    @Override
    public ContentValues toDb () {
        ContentValues cv = super.toDb();

        // Firty Object Checking
        cv.put(Constants.DB.COLUMN_DIRTY, String.valueOf(bDirty));

        // Enter values into Database
        cv.put(Constants.DB.COLUMN_SRV_ID,         serverId);
        cv.put(Constants.DB.COLUMN_TEMPLATE_ID,    template.dbId);

        // Prepare JSON Array for values
        JSONArray valuesJson = new JSONArray();
        try {
            for(FieldValue value : values) {
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

    // ----------------------- Public APIs ----------------------- //
    //
    // Server JSON converter methods
    //
    public void fromServer(JSONObject json) {
        try {
            // Mark as not dirty
            bDirty = false;

            serverId = json.getString(Constants.Server.KEY_ID);

            // Get Local Template Object
            long templateId = json.getLong(Constants.Server.KEY_TEMPLATE_ID);
            template = DbHelper.templateTable.GetByServerId(templateId);

            // Parse field values
            JSONArray valuesJson = json.getJSONArray(Constants.Server.KEY_VALUES);
            values = new ArrayList<>();
            for (int i = 0; i < valuesJson.length(); i++) {
                JSONObject valueJson = valuesJson.getJSONObject(i);

                // Get field and string value
                Long fieldId = valueJson.getLong(Constants.Server.KEY_FIELD_ID);
                Field field = DbHelper.fieldTable.GetByServerId(fieldId);
                String value = valueJson.getString(Constants.Server.KEY_VALUE);

                // Add field value object
                values.add(FieldValue.newInstance(field, value));
            }
        } catch (Exception e) {
            Dbg.error(TAG, "Exception while converting from JSON");
            Dbg.stack(e);
        }
    }

    public JSONObject toServer() {
        JSONObject json = new JSONObject();

        try {
            json.put(Constants.Server.KEY_ID, serverId);

            json.put(Constants.Server.KEY_TEMPLATE_ID, template.serverId);

            // Create Values Array
            JSONArray valuesJson = new JSONArray();
            for (FieldValue value : values) {
                // Create Value JSON
                JSONObject valueJson = new JSONObject();
                valueJson.put(Constants.Server.KEY_FIELD_ID, value.field.serverId);
                valueJson.put(Constants.Server.KEY_VALUE, value.toString());

                // Add to Values JSON Array
                valuesJson.put(valueJson);
            }
            json.put(Constants.Server.KEY_VALUES, valuesJson);
        } catch (JSONException e) {
            Dbg.error(TAG, "Error while putting sync data in object");
            Dbg.stack(e);
        }

        return json;
    }

    // Method to reset field values to current template
    public void SetTemplate(Template template) {
        // Save template in cache
        this.template = template;

        // Clear existing values
        values.clear();

        // Add new field value as per template
        for (Field field : template.fields) {
            values.add(FieldValue.newInstance(field, field.value));
        }
    }
}
