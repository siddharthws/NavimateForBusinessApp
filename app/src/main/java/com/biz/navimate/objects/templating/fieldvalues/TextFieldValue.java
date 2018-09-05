package com.biz.navimate.objects.templating.fieldvalues;

import com.biz.navimate.objects.Field;

public class TextFieldValue extends FieldValue {
    // ----------------------- Constants ----------------------- //
    private static final String TAG = "TEXT_FIELD_VALUE";

    // ----------------------- Classes ---------------------------//
    // ----------------------- Interfaces ----------------------- //
    // ----------------------- Globals ----------------------- //
    public String text = "";

    // ----------------------- Constructor ----------------------- //
    public TextFieldValue(Field field, String value) {
        super(field);

        fromString(value);
    }

    // ----------------------- Abstracts ----------------------- //
    // ----------------------- Overrides ----------------------- //
    // String converter methods
    @Override
    public String toString() {
        return text;
    }

    @Override
    public void fromString(String value) {
        this.text = value;
    }

    @Override
    public String getError() {
        String err = "";

        // return error if field is mandatory & value is empty
        if (field.bMandatory && text.length() == 0) {
            err = "Field is mandatory";
        }

        return err;
    }

    // ----------------------- Public methods ----------------------- //
    // ----------------------- Private methods ----------------------- //
    // ----------------------- Static methods ----------------------- //
}
