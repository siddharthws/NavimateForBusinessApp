package com.biz.navimate.viewholders;

import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.biz.navimate.views.custom.NvmImageButton;
import com.biz.navimate.views.custom.NvmEditText;
import com.biz.navimate.views.custom.NvmImageView;

public class CustomViewHolder {
    /*
     * Compound Views
     */
    // Toolbar
    public static class NvmToolbar {
        public ImageButton  ibSearch = null,
                            ibCross = null,
                            ibSave = null,
                            ibEdit = null,
                            ibAdd = null,
                            ibBack = null;
        public TextView tvText = null;
        public com.biz.navimate.views.compound.EtClearable etcSearch = null;
        public LinearLayout llButtons = null;
    }

    // Label Box
    public static class LabelBox {
        public TextView         tvLabel = null;
        public TextView         tvError = null;
        public RelativeLayout   rlContainer = null;
    }

    // Custom Scroll View
    public static class NvmScrollView {
        public ScrollView svContainer = null;
    }

    public static class DateEditor {
        public ImageButton ibEdit, ibClear = null;
        public TextView tvText = null;
    }

    public static class LocationEditor {
        public ImageButton ibEdit, ibClear = null;
        public TextView tvText = null;
    }

    public static class EtClearable {
        public NvmEditText etText = null;
        public NvmImageButton ibClear = null;
    }

    public static class ObjectPicker {
        public ImageButton ibEdit, ibClear = null;
        public TextView tvText = null;
        public NvmImageView ivIcon = null;
    }
}
