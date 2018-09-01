package com.biz.navimate.database;

import android.content.ContentValues;
import android.database.Cursor;

import com.biz.navimate.objects.core.ObjDb;
import com.biz.navimate.constants.Constants;
import com.biz.navimate.objects.ObjProduct;

public class ProductTable extends BaseTable {
    // ----------------------- Constants ----------------------- //
    private static final String TAG = "PRODUCT_TABlE";

    // Table name
    public static final String TABLE_NAME             = "product_table";

    // Create query
    public static final String CREATE_TABLE =
            "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
                    Constants.DB.COLUMN_ID             + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                    Constants.DB.COLUMN_SRV_ID         + " TEXT," +
                    Constants.DB.COLUMN_NAME           + " TEXT," +
                    Constants.DB.COLUMN_PRODUCT_ID     + " TEXT," +
                    Constants.DB.COLUMN_TEMPLATE_ID    + " INTEGER," +
                    Constants.DB.COLUMN_VALUES         + " TEXT)";

    // ----------------------- Constructor ----------------------- //
    public ProductTable(DbHelper dbHelper)
    {
        super(dbHelper, TABLE_NAME, new String[]{   Constants.DB.COLUMN_ID,
                                                    Constants.DB.COLUMN_SRV_ID,
                                                    Constants.DB.COLUMN_NAME,
                                                    Constants.DB.COLUMN_PRODUCT_ID,
                                                    Constants.DB.COLUMN_TEMPLATE_ID,
                                                    Constants.DB.COLUMN_VALUES});
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
