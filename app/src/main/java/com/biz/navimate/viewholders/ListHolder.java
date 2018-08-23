package com.biz.navimate.viewholders;

import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Created by Siddharth on 29-09-2017.
 */

public class ListHolder {
    // Task List Item
    public static class Task
    {
        public LinearLayout llRoot;
        public TextView     tvTitle, tvDescription;
        public Button       btnForm;
    }

    // Lead List Item
    public static class Lead
    {
        public TextView     tvTitle;
        public ImageView    ivTick;
    }

    // Removable List Item
    public static class Removable
    {
        public TextView     tvTitle;
        public ImageButton  ibRemove;
    }

    // Drawer list item
    public static class Drawer
    {
        public RelativeLayout rlDrawerItem;
        public ImageView      ivIcon;
        public TextView       tvTitle;
        public View           vwItemSep;
        public View           vwGroupSep;
    }

    // Generic list item
    public static class Generic
    {
        public ImageView            ivStart;
        public TextView             tvText;
        public ImageView            ivEnd;
    }

    // Form list item
    public static class Form
    {
        public TextView            tvLead;
        public TextView            tvForm;
        public TextView            tvDate;
        public TextView            tvStatus;
    }
}
