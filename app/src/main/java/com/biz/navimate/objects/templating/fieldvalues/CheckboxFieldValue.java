package com.biz.navimate.objects.templating.fieldvalues;

import com.biz.navimate.objects.Field;

public class CheckboxFieldValue extends FieldValue {
    // ----------------------- Constants ----------------------- //
    private static final String TAG = "CHECKBOX_FIELD_VALUE";

    // ----------------------- Classes ---------------------------//
    // ----------------------- Interfaces ----------------------- //
    // ----------------------- Globals ----------------------- //
    public boolean bChecked = false;

    // ----------------------- Constructor ----------------------- //
    public CheckboxFieldValue(Field field, String value) {
        super(field);

        fromString(value);
    }

    // ----------------------- Abstracts ----------------------- //
    // ----------------------- Overrides ----------------------- //
    // String converter methods
    @Override
    public String toString() {
        return String.valueOf(bChecked);
    }

    @Override
    public void fromString(String value) {
        bChecked = Boolean.valueOf(value);
    }

    @Override
    public String getError() {
        return "";
    }

    // ----------------------- Public methods ----------------------- //
    // ----------------------- Private methods ----------------------- //
    // ----------------------- Static methods ----------------------- //
}
