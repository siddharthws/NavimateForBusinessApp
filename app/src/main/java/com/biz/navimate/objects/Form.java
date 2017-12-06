package com.biz.navimate.objects;

import java.util.ArrayList;

/**
 * Created by Siddharth on 28-09-2017.
 */

public class Form extends DbObject {
    // ----------------------- Constants ----------------------- //
    private static final String TAG = "FORM";

    // ----------------------- Globals ----------------------- //
    public String name = "";
    public ArrayList<FormField.Base> fields = null;

    // ----------------------- Constructor ----------------------- //
    public Form (long id, long version, String name, ArrayList<FormField.Base> fields) {
        super(DbObject.TYPE_FORM, version, id);
        this.name = name;
        this.fields = fields;
    }
}
