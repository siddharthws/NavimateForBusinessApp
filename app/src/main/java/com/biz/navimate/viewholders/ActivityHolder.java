package com.biz.navimate.viewholders;

import com.biz.navimate.views.RlDialog;
import com.biz.navimate.views.RlEnterPhone;
import com.biz.navimate.views.RlVerifyPhone;

/**
 * Created by Siddharth on 22-09-2017.
 */

public class ActivityHolder {
    // Base (Common) activity holder
    public static class Base {
        public RlDialog rlDialog = null;
    }

    // App loading activity holder
    public static class AppLoad extends Base {
        // Placeholder
    }

    // Registration activity holder
    public static class Registration extends Base {
        public RlEnterPhone rlEnterPhone                    = null;
        public RlVerifyPhone rlVerifyPhone                  = null;
    }

    // Homescreen activity holder
    public static class Homescreen extends Base {
        // Placeholder
    }
}
