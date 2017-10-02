package com.biz.navimate.viewholders;

import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

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
}
