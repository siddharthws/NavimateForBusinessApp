package com.biz.navimate.objects;

import android.content.ContentValues;
import android.database.Cursor;

import com.biz.navimate.constants.Constants;
import com.biz.navimate.database.DbHelper;
import com.biz.navimate.database.TemplateTable;
import com.biz.navimate.debug.Dbg;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Siddharth on 05-12-2017.
 */

public class Template extends ServerObject {
    // ----------------------- Constants ----------------------- //
    private static final String TAG = "TEMPLATE";

    // ----------------------- Globals ----------------------- //
    public String name = "";
    public ArrayList<Field> fields = new ArrayList<>();
    public int type = 0;

    // ----------------------- Constructor ----------------------- //
    public Template (JSONObject json) {
        super(DbObject.TYPE_FIELD, Constants.Misc.ID_INVALID, Constants.Misc.ID_INVALID, Constants.Misc.ID_INVALID);
        fromJson(json);
    }

    public Template (Cursor cursor) {
        super(DbObject.TYPE_FIELD, Constants.Misc.ID_INVALID, Constants.Misc.ID_INVALID, Constants.Misc.ID_INVALID);
        fromCursor(cursor);
    }

    // ----------------------- Public APIs ----------------------- //
    //
    // Converter methods for JSON
    //
    public void fromJson(JSONObject json) {
        try {
            serverId                = json.getLong(Constants.Server.KEY_ID);
            name                    = json.getString(Constants.Server.KEY_NAME);
            type                    = json.getInt(Constants.Server.KEY_TYPE);
            JSONArray fieldsJson    = json.getJSONArray(Constants.Server.KEY_FIELDS);

            // Parse fields from JSON
            fields = new ArrayList<>();
            for (int i = 0; i < fieldsJson.length(); i++) {
                // Get Field JSON
                JSONObject fieldJson = fieldsJson.getJSONObject(i);

                // Create new field object or update old one
                Field field = DbHelper.fieldTable.GetByServerId(fieldJson.getLong(Constants.Server.KEY_ID));
                if (field != null) {
                    field.fromJson(fieldJson);
                } else {
                    field = new Field(fieldJson);
                }

                // Save Field Object
                DbHelper.fieldTable.Save(field);

                // Add to template
                fields.add(field);
            }
        } catch (Exception e) {
            Dbg.error(TAG, "Exception whiel parsing from JSON");
            Dbg.stack(e);
        }
    }

    //
    // Converter methods for database
    //
    public void fromCursor(Cursor cursor)
    {
        dbId                    = cursor.getLong    (cursor.getColumnIndex(TemplateTable.COLUMN_ID));
        serverId                = cursor.getLong    (cursor.getColumnIndex(TemplateTable.COLUMN_SRV_ID));
        name                    = cursor.getString  (cursor.getColumnIndex(TemplateTable.COLUMN_NAME));
        type                    = cursor.getInt     (cursor.getColumnIndex(TemplateTable.COLUMN_TYPE));

        // Create Field IDs array
        String fieldsString     = cursor.getString  (cursor.getColumnIndex(TemplateTable.COLUMN_FIELD_IDS));
        fields = new ArrayList<>();
        try
        {
            JSONArray fieldsJson = new JSONArray(fieldsString);
            for (int i = 0; i < fieldsJson.length(); i++) {
                Long fieldId = fieldsJson.getLong(i);
                Field field = (Field) DbHelper.fieldTable.GetById(fieldId);
                fields.add(field);
            }
        } catch (JSONException e) {
            Dbg.error(TAG, "Error while converting JSON Array to Form Filed");
            Dbg.stack(e);
        }
    }

    public ContentValues toContentValues () {
        ContentValues cv = new ContentValues();
        // Enter values into Database
        cv.put(TemplateTable.COLUMN_SRV_ID,         serverId);
        cv.put(TemplateTable.COLUMN_NAME,           name);
        cv.put(TemplateTable.COLUMN_TYPE,           type);

        JSONArray fieldsJson = new JSONArray();
        for (Field field : fields) {
            fieldsJson.put(field.dbId);
        }
        cv.put(TemplateTable.COLUMN_FIELD_IDS, fieldsJson.toString());

        return cv;
    }
}
