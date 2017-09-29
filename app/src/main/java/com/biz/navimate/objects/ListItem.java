package com.biz.navimate.objects;

/**
 * Created by Siddharth on 29-09-2017.
 */

public class ListItem {
    // ----------------------- Constants ----------------------- //
    // Types of list items
    public static final int TYPE_INVALID                = 0;

    // ----------------------- Classes ---------------------------//
    // Base Class for list Item Data
    public static abstract class Base
    {
        // ----------------------- Globals ----------------------- //
        public int    type              = TYPE_INVALID;
        public int    id                = -1;

        // ----------------------- Constructor ----------------------- //
        public Base(int type, int id)
        {
            this.type               = type;
            this.id                 = id;
        }
    }
}
