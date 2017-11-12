package com.biz.navimate.database;

import android.content.ContentValues;
import android.database.Cursor;

import com.biz.navimate.objects.DbObject;
import com.biz.navimate.objects.Form;
import com.biz.navimate.objects.FormField;

/**
 * Created by Sai_Kameswari on 08-11-2017.
 */

public class FormTable extends BaseTable {

    // ----------------------- Constants ----------------------- //
    private static final String TAG = "FORMTABlE_DB_HELPER";

    // Table name
    public static final String TABLE_NAME             = "table_form";

    // Columns
    public static final String COLUMN_TITLE           = "form_title";
    public static final String COLUMN_DATA            = "form_data";


    // Create query
    public static final String CREATE_TABLE =
            "CREATE TABLE IF NOT EXISTS" + TABLE_NAME + " (" +
                    COLUMN_ID            + " INTEGER PRIMARY KEY," +
                    COLUMN_TITLE         + " TEXT," +
                    COLUMN_DATA          + " TEXT)";

    public FormTable(DbHelper dbHelper)
    {
        super(dbHelper, TABLE_NAME, new String[]{   COLUMN_ID,
                                                    COLUMN_TITLE,
                                                    COLUMN_DATA});

    }

    // ----------------------- Private APIs ----------------------- //
    @Override
    protected DbObject ParseToObject(Cursor cursor)
    {
        Form card = new Form();

        card.dbId                       = cursor.getInt     (cursor.getColumnIndex(COLUMN_ID));
        card.name                       = cursor.getString  (cursor.getColumnIndex(COLUMN_TITLE));

        return card;
    }

    @Override
    protected ContentValues ParseToContent(DbObject dbItem)
    {
        Form contact = (Form) dbItem;
        ContentValues dbEntry = new ContentValues();

        // Enter values into Database
        dbEntry.put(COLUMN_TITLE,           contact.name);
        dbEntry.put(COLUMN_DATA,          contact.fields.toString());

        return dbEntry;
    }


}
