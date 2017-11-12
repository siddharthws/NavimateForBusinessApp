package com.biz.navimate.database;

import android.content.ContentValues;
import android.database.Cursor;

import com.biz.navimate.debug.Dbg;
import com.biz.navimate.objects.DbObject;
import com.biz.navimate.objects.Form;
import com.biz.navimate.objects.FormField;

import org.json.JSONArray;
import org.json.JSONException;

/**
 * Created by Sai_Kameswari on 08-11-2017.
 */

public class FormTable extends BaseTable {

    // ----------------------- Constants ----------------------- //
    private static final String TAG = "FORM_TABlE";

    // Table name
    public static final String TABLE_NAME             = "form_table";

    // Columns
    public static final String COLUMN_NAME            = "name";
    public static final String COLUMN_DATA            = "data";

    // Create query
    public static final String CREATE_TABLE =
            "CREATE TABLE IF NOT EXISTS" + TABLE_NAME + " (" +
                    COLUMN_ID            + " INTEGER PRIMARY KEY," +
                    COLUMN_NAME          + " TEXT," +
                    COLUMN_DATA          + " TEXT)";

    public FormTable(DbHelper dbHelper)
    {
        super(dbHelper, TABLE_NAME, new String[]{   COLUMN_ID,
                                                    COLUMN_NAME,
                                                    COLUMN_DATA});
    }

    // ----------------------- Private APIs ----------------------- //
    @Override
    protected DbObject ParseToObject(Cursor cursor)
    {
        Form form   =   null;
        JSONArray fieldsToJson = null;

        int dbId                       = cursor.getInt     (cursor.getColumnIndex(COLUMN_ID));
        String name                    = cursor.getString  (cursor.getColumnIndex(COLUMN_NAME));
        String fields                  = cursor.getString  (cursor.getColumnIndex(COLUMN_DATA));

        try
        {
            fieldsToJson  =   new JSONArray(fields);
        } catch (JSONException e) {
            Dbg.error(TAG, "Error while converting JSON Array to Form Filed");
        }

        if(fieldsToJson!=null)
        {
            form = new Form (name, fieldsToJson);
            form.dbId   =   dbId;
        }
        return form;
    }

    @Override
    protected ContentValues ParseToContent(DbObject dbItem)
    {
        Form form = (Form) dbItem;
        ContentValues dbEntry = new ContentValues();

        // Enter values into Database
        dbEntry.put(COLUMN_NAME,           form.name);

        JSONArray fields = new JSONArray();
        try
        {
            for (FormField.Base field : form.fields) {
                fields.put(field.toJson());
            }

        } catch (JSONException ex) {
            Dbg.error(TAG, "Error while converting fields to String");
        }

        dbEntry.put(COLUMN_DATA,          fields.toString());

        return dbEntry;
    }


}
