package com.biz.navimate.viewholders;

import android.widget.ImageButton;

import com.biz.navimate.views.TvCalibri;
import com.biz.navimate.views.custom.NvmImageButton;
import com.biz.navimate.views.custom.NvmEditText;

public class CustomViewHolder {
    public static class DateEditor {
        public ImageButton ibEdit, ibClear = null;
        public TvCalibri tvText = null;
    }

    public static class EtClearable {
        public NvmEditText etText = null;
        public NvmImageButton ibClear = null;
    }
}
