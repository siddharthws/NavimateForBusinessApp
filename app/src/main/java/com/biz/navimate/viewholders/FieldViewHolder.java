package com.biz.navimate.viewholders;

import android.widget.LinearLayout;
import android.widget.TextView;

import com.biz.navimate.views.CbCustom;
import com.biz.navimate.views.compound.DateEditorView;
import com.biz.navimate.views.compound.EtClearable;
import com.biz.navimate.views.compound.ObjectPickerView;
import com.biz.navimate.views.compound.PhotoEditorView;
import com.biz.navimate.views.compound.SignEditorView;
import com.biz.navimate.views.custom.Dropdown;

import java.util.ArrayList;

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

    // Checklist Field View holder
    public static class Checklist extends Base {
        public LinearLayout llChecklist = null;
        public ArrayList<CbCustom> cbList = null;
    }

    // Checkbox Field View holder
    public static class Checkbox extends Base {
        public CbCustom   cbCheckbox    = null;
    }

    // Date Field View holder
    public static class Date extends Base {
        public DateEditorView   dtEditor = null;
    }

    // Object Field View holder
    public static class Object extends Base {
        public ObjectPickerView objPicker = null;
    }

    // Photo Field View holder
    public static class Photo extends Base {
        public PhotoEditorView peEditor = null;
    }

    // Signature Field View holder
    public static class Sign extends Base {
        public SignEditorView seEditor = null;
    }
}
