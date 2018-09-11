package com.biz.navimate.viewholders;

import android.support.v7.widget.Toolbar;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.biz.navimate.fragments.NvmMapFragment;
import com.biz.navimate.views.DrawableImageView;
import com.biz.navimate.views.RlDialog;
import com.biz.navimate.views.RlDrawer;
import com.biz.navimate.views.RlEnterName;
import com.biz.navimate.views.RlEnterPhone;
import com.biz.navimate.views.RlListView;
import com.biz.navimate.views.RlVerifyPhone;
import com.biz.navimate.views.VwSignature;
import com.biz.navimate.views.compound.EtClearable;
import com.biz.navimate.views.compound.NvmToolbar;

/**
 * Created by Siddharth on 22-09-2017.
 */

public class ActivityHolder {
    // Base (Common) activity holder
    public static class Base {
        public RlDialog rlDialog = null;
        public NvmToolbar toolbar = null;
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

        // Task Count
        public TextView tvTaskCount = null;
        public TextView tvFormCount = null;

        // Drawer
        public RlDrawer rlDrawer = null;
    }

    public static class LeadList extends Base
    {
        // List
        public RlListView rlvList                                = null;
    }

    public static class ObjectPicker extends Base
    {
        // Toolbar
        public ImageButton ibBack       = null;
        public EtClearable etcSearch    = null;

        // List
        public RlListView rlvList       = null;
    }

    public static class Task extends Base
    {
        // Toolbar
        public ImageButton ibSync, ibBack                        = null;

        // List
        public RlListView rlvList                                = null;
    }

    public static class Forms extends Base
    {
        // Toolbar
        public ImageButton ibSync, ibBack                        = null;

        // List
        public RlListView rlvList                                = null;
    }

    public static class Signature extends Base
    {
        public VwSignature vwSignature = null;
        public Toolbar toolbar           = null;
    }

    public static class PhotoDraw extends Base
    {
        public DrawableImageView vwPhotoDraw = null;
        public Toolbar toolbar           = null;
    }

    public static class ViewPhoto extends Base
    {
        public ImageView ivImage    = null;
    }

    public static class PhotoEditor extends Base
    {
        public ImageView ivImage         = null;
        public Toolbar toolbar           = null;
    }
}