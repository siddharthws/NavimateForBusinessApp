package com.biz.navimate.objects;

import java.util.ArrayList;

/**
 * Created by Siddharth on 28-09-2017.
 */

public class Form {
    // ----------------------- Constants ----------------------- //
    private static final String TAG = "FORM";

    // ----------------------- Globals ----------------------- //
    public String name = "";
    public ArrayList<FormField.Base> fields = new ArrayList<>();

    // ----------------------- Constructor ----------------------- //
    public Form () {}

    public Form (String name, ArrayList<FormField.Base> fields) {
        this.name = name;
        this.fields = fields;
    }
}
