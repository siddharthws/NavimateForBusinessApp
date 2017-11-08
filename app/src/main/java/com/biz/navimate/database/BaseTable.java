package com.biz.navimate.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.biz.navimate.debug.Dbg;
import com.biz.navimate.objects.DbObject;

import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by Jagannath on 08-11-2017.
 */

public abstract class BaseTable
{
    // ----------------------- Constants ----------------------- //
    private static final String TAG = "BASE_TABLE";

    // Column Names
    public static final String COLUMN_ID = "_id";

    // ----------------------- Globals ----------------------- //
    // Table parameters
    protected DbHelper dbHelper = null;
    protected String tableName = "";
    protected String[] columns = null;

    // Dynamic cache
    protected CopyOnWriteArrayList<DbObject> cache = null;

    // ----------------------- Constructor ----------------------- //
    public BaseTable(DbHelper dbHelper, String tableName, String[] columns)
    {
        this.dbHelper = dbHelper;
        this.tableName = tableName;
        this.columns = columns;

        cache = new CopyOnWriteArrayList<>();

        // Init data
        Init();
    }

    // ----------------------- Abstracts ----------------------- //
    protected   abstract ContentValues ParseToContent(DbObject dbItem);
    protected   abstract DbObject ParseToObject(Cursor cursor);

    // ----------------------- Overrides ----------------------- //
    // ----------------------- Public APIs ----------------------- //
    public int Add(DbObject dbItem)
    {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Get Content Values
        ContentValues content = ParseToContent(dbItem);

        // Add to table
        dbItem.dbId  = (int) db.insert(tableName, null, content);

        // Add to cache
        if (dbItem.dbId != DbObject.DB_ID_INVALID)
        {
            cache.add(dbItem);
        }
        else
        {
            Dbg.error(TAG, "Unable to insert item in " + tableName);
        }

        return dbItem.dbId;
    }

    public boolean Update(DbObject dbItem)
    {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Ensure updated card is valid
        if (dbItem.dbId == DbObject.DB_ID_INVALID)
        {
            Dbg.error(TAG, "Cannot update item with invalid DB ID");
            return false;
        }

        // Get Content values
        ContentValues content = ParseToContent(dbItem);

        // Update database
        int affectedRows = db.update(   tableName,
                                        content,
                                        COLUMN_ID + "=?",
                                        new String[] {Integer.toString(dbItem.dbId)});

        if (affectedRows != 1)
        {
            Dbg.error(TAG, "Contact group DB updation failed or corrupted. Affected rows = " + affectedRows);
        }
        else
        {
            // Delet old dynamic initData
            cache.remove(GetById(dbItem.dbId));

            // Insert new item
            cache.add(dbItem);

            return true;
        }

        return false;
    }

    public boolean Remove(int dbId)
    {
        Boolean bRemoved = false;
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        DbObject itemToBeRemoved = null;

        // Remove this db id from table
        int affectedRows = db.delete(   tableName,
                                        COLUMN_ID + "=?",
                                        new String[] {Integer.toString(dbId)});

        if (affectedRows != 1)
        {
            Dbg.error(TAG, "Item removal failed. Affected rows = " + affectedRows);
        }
        else
        {
            // Delete dynamic initData
            for (DbObject dbItem : cache)
            {
                if (dbItem.dbId == dbId)
                {
                    itemToBeRemoved = dbItem;
                    break;
                }
            }
        }

        if (itemToBeRemoved != null)
        {
            cache.remove(itemToBeRemoved);
            bRemoved = true;
        }

        return bRemoved;
    }

    public boolean Clear()
    {
        Boolean bCleared = true;
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Delete from database
        for (DbObject dbItem : cache)
        {
            // Delete using ID
            int affectedRows = db.delete(   tableName,
                                            COLUMN_ID + "=?",
                                            new String[] {Integer.toString(dbItem.dbId)});

            if (affectedRows != 1)
            {
                bCleared = false;
                Dbg.error(TAG, "DB deletion failed. Affected rows = " + affectedRows);
                break;
            }
        }

        if(bCleared)
        {
            // Delete cache
            cache.clear();
        }

        return bCleared;
    }

    public CopyOnWriteArrayList<? extends DbObject> GetAll()
    {
        return cache;
    }

    public DbObject GetById(int id)
    {
        for(DbObject dbItem : cache)
        {
            if (dbItem.dbId == id)
            {
                return dbItem;
            }
        }

        return null;
    }

    // ----------------------- Private APIs ----------------------- //
    private void Init()
    {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        cache.clear();

        Cursor dbRows = db.query(   tableName,
                columns,
                null,
                null,
                null,
                null,
                null);

        while (dbRows.moveToNext())
        {
            // ParseToObject from cursor
            DbObject dbItem = ParseToObject(dbRows);

            // Update DB Id
            dbItem.dbId = dbRows.getInt(dbRows.getColumnIndex(COLUMN_ID));

            // Add to cache
            cache.add(dbItem);
        }
    }
}
