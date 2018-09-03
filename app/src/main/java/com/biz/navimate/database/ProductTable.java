package com.biz.navimate.database;

import android.content.ContentValues;
import android.database.Cursor;

import com.biz.navimate.objects.core.ObjDb;
import com.biz.navimate.objects.ObjProduct;

public class ProductTable extends BaseTable {
    // ----------------------- Constants ----------------------- //
    private static final String TAG = "PRODUCT_TABlE";

    // Table name
    public static final String TABLE_NAME             = "product_table";

    // Columns
    public static final String COLUMN_SRV_ID           = "server_id";
    public static final String COLUMN_NAME             = "name";
    public static final String COLUMN_PRODUCT_ID       = "product_id";
    public static final String COLUMN_TEMPLATE_ID      = "template_id";
    public static final String COLUMN_VALUES           = "_values";

    // Create query
    public static final String CREATE_TABLE =
            "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
                    COLUMN_ID             + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                    COLUMN_SRV_ID         + " TEXT," +
                    COLUMN_NAME           + " TEXT," +
                    COLUMN_PRODUCT_ID     + " TEXT," +
                    COLUMN_TEMPLATE_ID    + " INTEGER," +
                    COLUMN_VALUES         + " TEXT)";

    // ----------------------- Constructor ----------------------- //
    public ProductTable(DbHelper dbHelper)
    {
        super(dbHelper, TABLE_NAME, new String[]{   COLUMN_ID,
                COLUMN_SRV_ID,
                COLUMN_NAME,
                COLUMN_PRODUCT_ID,
                COLUMN_TEMPLATE_ID,
                COLUMN_VALUES});
    }

    // ----------------------- Public APIs ----------------------- //
    // API to get object by serverId
    public ObjProduct GetByServerId(String textServerId) {
        for (ObjDb dbItem : cache) {
            ObjProduct product = (ObjProduct) dbItem;
            if (product.textServerId.equals(textServerId)) {
                return product;
            }
        }

        return null;
    }

    // ----------------------- Private APIs ----------------------- //
    @Override
    protected ObjDb ParseToObject(Cursor cursor) {
        return new ObjProduct(cursor);
    }

    @Override
    protected ContentValues ParseToContent(ObjDb dbItem) {
        return  ((ObjProduct) dbItem).toContentValues();
    }
}
