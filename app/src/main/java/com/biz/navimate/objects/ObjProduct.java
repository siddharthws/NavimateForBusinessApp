package com.biz.navimate.objects;

import android.content.ContentValues;
import android.database.Cursor;

import com.biz.navimate.constants.Constants;
import com.biz.navimate.database.DbHelper;
import com.biz.navimate.database.ProductTable;
import com.biz.navimate.debug.Dbg;
import com.biz.navimate.objects.core.ObjDb;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ObjProduct extends ServerObject {
    // ----------------------- Constants ----------------------- //
    private static final String TAG = "OBJ_PRODUCT";

    // ----------------------- Globals ----------------------- //
    public String textServerId = "";
    public String name = "", productId = "";
    public Template template = null;
    public ArrayList<FormEntry.Base> values = new ArrayList<>();

    // ----------------------- Constructor ----------------------- //
    public ObjProduct (JSONObject json) {
        super(Constants.Misc.ID_INVALID, Constants.Misc.ID_INVALID);
        fromJson(json);
    }

    public ObjProduct (Cursor cursor) {
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
            productId           = json.getString(Constants.Server.KEY_PRODUCT_ID);

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

        textServerId     = cursor.getString  (cursor.getColumnIndex(Constants.DB.COLUMN_SRV_ID));
        name             = cursor.getString  (cursor.getColumnIndex(Constants.DB.COLUMN_NAME));
        productId        = cursor.getString  (cursor.getColumnIndex(Constants.DB.COLUMN_PRODUCT_ID));

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
        cv.put(Constants.DB.COLUMN_NAME,            name);
        cv.put(Constants.DB.COLUMN_PRODUCT_ID,      productId);
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
}
