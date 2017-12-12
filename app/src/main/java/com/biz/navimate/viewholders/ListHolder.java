package com.biz.navimate.viewholders;

import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.biz.navimate.views.TvCalibri;

/**
 * Created by Siddharth on 29-09-2017.
 */

public class ListHolder {
    // Task List Item
    public static class Task
    {
        public TvCalibri    tvTitle, tvDescription;
        public Button       btnForm;
    }

    // Lead List Item
    public static class Lead
    {
        public TvCalibri    tvTitle;
        public ImageView    ivTick;
    }

    // Removable List Item
    public static class Removable
    {
        public TvCalibri    tvTitle;
        public ImageButton  ibRemove;
    }

    // Drawer list item
    public static class Drawer
    {
        public RelativeLayout rlDrawerItem;
        public ImageView      ivIcon;
        public TvCalibri      tvTitle;
        public View           vwItemSep;
        public View           vwGroupSep;
    }

    // Generic list item
    public static class Generic
    {
        public ImageView            ivStart;
        public TvCalibri            tvText;
        public ImageView            ivEnd;
    }

    // Form list item
    public static class Form
    {
        public TvCalibri            tvLead;
        public TvCalibri            tvForm;
        public TvCalibri            tvDate;
        public TvCalibri            tvStatus;
    }
}
