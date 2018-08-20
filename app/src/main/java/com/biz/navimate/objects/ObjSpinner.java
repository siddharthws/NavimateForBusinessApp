package com.biz.navimate.objects;

public class ObjSpinner {
    // ----------------------- Constants ----------------------- //
    // Types of items
    public static final int TYPE_INVALID                = 0;

    // ----------------------- Classes ---------------------------//
    // Base Class for Item Data
    public static abstract class Base {
        // ----------------------- Globals ----------------------- //
        public int type = TYPE_INVALID;

        // ----------------------- Constructor ----------------------- //
        public Base(int type) {
            this.type = type;
        }
    }
}
