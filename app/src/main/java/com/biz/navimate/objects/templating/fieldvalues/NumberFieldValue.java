package com.biz.navimate.objects.templating.fieldvalues;

import com.biz.navimate.objects.Field;

public class NumberFieldValue extends FieldValue {
    // ----------------------- Constants ----------------------- //
    private static final String TAG = "NUMBER_FIELD_VALUE";

    // ----------------------- Classes ---------------------------//
    // ----------------------- Interfaces ----------------------- //
    // ----------------------- Globals ----------------------- //
    public double number = 0;

    // ----------------------- Constructor ----------------------- //
    public NumberFieldValue(Field field, String value) {
        super(field);

        fromString(value);
    }

    // ----------------------- Abstracts ----------------------- //
    // ----------------------- Overrides ----------------------- //
    // String converter methods
    @Override
    public String toString() {
        return String.valueOf(number);
    }

    @Override
    public void fromString(String value) {
        if (value == null || value.length() == 0) {
            value = "0";
        }
        this.number = Double.parseDouble(value);
    }

    @Override
    public String getError() {
        return "";
    }

    // ----------------------- Public methods ----------------------- //
    // ----------------------- Private methods ----------------------- //
    // ----------------------- Static methods ----------------------- //
}
