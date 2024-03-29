package com.biz.navimate.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.biz.navimate.constants.Constants;
import com.biz.navimate.debug.Dbg;
import com.biz.navimate.objects.core.ObjDb;

import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by Jagannath on 08-11-2017.
 */

public abstract class BaseTable {
    // ----------------------- Constants ----------------------- //
    private static final String TAG = "BASE_TABLE";

    // ----------------------- Globals ----------------------- //
    // Table parameters
    protected DbHelper dbHelper = null;
    protected String tableName = "";
    protected String[] columns = null;

    // Dynamic cache
    protected CopyOnWriteArrayList<ObjDb> cache = null;

    // ----------------------- Constructor ----------------------- //
    public BaseTable(DbHelper dbHelper, String tableName, String[] columns) {
        this.dbHelper = dbHelper;
        this.tableName = tableName;
        this.columns = columns;

        cache = new CopyOnWriteArrayList<>();

        // Init data
        Init();
    }

    // ----------------------- Abstracts ----------------------- //
    protected abstract ContentValues ParseToContent(ObjDb dbItem);

    protected abstract ObjDb ParseToObject(Cursor cursor);

    // ----------------------- Overrides ----------------------- //
    // ----------------------- Public APIs ----------------------- //
    // API to add / update data
    public boolean Save(ObjDb item) {
        // Update / Add item as per the id
        ObjDb existingItem = GetById(item.dbId);
        if (existingItem != null) {
            return Update(item);
        } else {
            return Add(item);
        }
    }

    protected boolean RemoveById(long dbId) {
        Boolean bRemoved = false;
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ObjDb itemToBeRemoved = null;

        // Remove this db id from table
        int affectedRows = db.delete(tableName,
                Constants.DB.COLUMN_ID + "=?",
                new String[]{Long.toString(dbId)});

        if (affectedRows != 1) {
            Dbg.error(TAG, "Item removal failed. Affected rows = " + affectedRows);
        } else {
            // Delete dynamic initData
            for (ObjDb dbItem : cache) {
                if (dbItem.dbId == dbId) {
                    itemToBeRemoved = dbItem;
                    break;
                }
            }
        }

        if (itemToBeRemoved != null) {
            cache.remove(itemToBeRemoved);
            bRemoved = true;
        }

        return bRemoved;
    }

    public boolean Clear() {
        Boolean bCleared = true;
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Delete from database
        for (ObjDb dbItem : cache) {
            // Delete using ID
            int affectedRows = db.delete(tableName,
                    Constants.DB.COLUMN_ID + "=?",
                    new String[]{Long.toString(dbItem.dbId)});

            if (affectedRows != 1) {
                bCleared = false;
                Dbg.error(TAG, "DB deletion failed. Affected rows = " + affectedRows);
                break;
            }
        }

        if (bCleared) {
            // Delete cache
            cache.clear();
        }

        return bCleared;
    }

    public CopyOnWriteArrayList<? extends ObjDb> GetAll() {
        return cache;
    }

    public ObjDb GetById(long id) {
        for (ObjDb dbItem : cache) {
            if (dbItem.dbId == id) {
                return dbItem;
            }
        }

        return null;
    }

    // ----------------------- Private APIs ----------------------- //
    // API to init cache from table
    private void Init() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        cache.clear();

        Cursor dbRows = db.query(tableName,
                columns,
                null,
                null,
                null,
                null,
                null);

        while (dbRows.moveToNext()) {
            // ParseToObject from cursor
            ObjDb dbItem = ParseToObject(dbRows);

            // Add to cache
            cache.add(dbItem);
        }
    }

    // API to add data to table
    private boolean Add(ObjDb dbItem) {
        boolean bAdded = false;
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Get Content Values
        ContentValues content = ParseToContent(dbItem);

        // Add to table
        long rowId = (int) db.insert(tableName, null, content);

        // Add to cache
        if (rowId != Constants.Misc.ID_INVALID) {
            // Update Data Object's DB ID
            dbItem.dbId = rowId;

            // Add object to cache
            cache.add(dbItem);

            // Mark success
            bAdded = true;
        } else {
            Dbg.error(TAG, "Unable to insert item in " + tableName);
        }

        return bAdded;
    }

    // API to update existing item
    private boolean Update(ObjDb dbItem) {
        boolean bUpdated = false;
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Ensure updated card is valid
        if (dbItem.dbId == Constants.Misc.ID_INVALID) {
            Dbg.error(TAG, "Cannot update item with invalid DB ID");
            return false;
        }

        // Get Content values
        ContentValues content = ParseToContent(dbItem);

        // Update database
        int affectedRows = db.update(tableName,
                content,
                Constants.DB.COLUMN_ID + "=?",
                new String[]{Long.toString(dbItem.dbId)});

        if (affectedRows != 1) {
            Dbg.error(TAG, "Contact group DB updation failed or corrupted. Affected rows = " + affectedRows);
        } else {
            // Delete old dynamic initData
            cache.remove(GetById(dbItem.dbId));

            // Insert new item
            cache.add(dbItem);

            bUpdated = true;
        }

        return bUpdated;
    }
}

