package com.biz.navimate.objects;

import com.biz.navimate.interfaces.IfaceDialog;

/**
 * Created by Siddharth on 27-09-2017.
 */

public class Dialog {
    // Dialog Types
    public static final int TYPE_NONE                       = 0;
    public static final int TYPE_ALERT                      = 1;

    // Base class for all dialog objects
    public static abstract class Base
    {
        // Globals
        public int      type            = TYPE_NONE;
        public boolean  bCancellable    = false;

        public Base (int type, boolean bCancellable)
        {
            this.type = type;
            this.bCancellable = bCancellable;
        }
    }

    public static class Alert extends Base
    {
        // Globals
        public String message = "";

        public Alert()
        {
            super(TYPE_ALERT, true);
        }

        public Alert(String message)
        {
            super(TYPE_ALERT, true);
            this.message = message;
        }
    }
}
