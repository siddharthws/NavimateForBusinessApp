package com.biz.navimate.viewholders;

import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;

import com.biz.navimate.fragments.NvmMapFragment;
import com.biz.navimate.views.RlDialog;
import com.biz.navimate.views.RlDrawer;
import com.biz.navimate.views.RlEnterName;
import com.biz.navimate.views.RlEnterPhone;
import com.biz.navimate.views.RlVerifyPhone;
import com.biz.navimate.views.TvCalibri;
import com.biz.navimate.views.VwSignature;

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
        public RlEnterName rlEnterName                      = null;
    }

    // Homescreen activity holder
    public static class Homescreen extends Base {
        // Map
        public NvmMapFragment mapFragment = null;
        public FrameLayout flMap = null;

        // Drawer
        public RlDrawer rlDrawer = null;

        // List
        public ListView lvTasks = null;

        // Buttons
        public ImageButton ibMap = null, ibList = null;
    }

    public static class LeadPicker extends Base
    {
        // Toolbar
        public ImageButton ibDone, ibBack                        = null;
        public TvCalibri tvSelectedCount                         = null;

        // List
        public ListView lvList                                  = null;
    }

    public static class Forms extends Base
    {
        // Toolbar
        public ImageButton ibSync, ibBack                        = null;

        // List
        public ListView lvList                                  = null;
    }

    public static class Signature extends Base
    {
        public Button btnSave, btnBack, btnClear        = null;
        public VwSignature vwSignature = null;
    }

    public static class ViewPhoto extends Base
    {
        public ImageView ivImage    = null;
    }
}
