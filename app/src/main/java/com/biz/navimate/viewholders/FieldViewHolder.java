package com.biz.navimate.viewholders;

import android.widget.TextView;

import com.biz.navimate.views.compound.EtClearable;
import com.biz.navimate.views.custom.Dropdown;

public class FieldViewHolder {
    // Base (Common) field views holder
    public static class Base {
    }

    // Text Field View holder
    public static class Text extends Base {
        public EtClearable etcText = null;
        public TextView tvText = null;
    }

    // Number Field View holder
    public static class Number extends Base {
        public EtClearable etcNumber = null;
        public TextView tvNumber = null;
    }

    // Radiolist Field View holder
    public static class Radiolist extends Base {
        public Dropdown ddRadiolist    = null;
    }
}
