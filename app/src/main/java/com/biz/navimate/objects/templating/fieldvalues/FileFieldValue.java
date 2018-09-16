package com.biz.navimate.objects.templating.fieldvalues;

import com.biz.navimate.objects.Field;

public class FileFieldValue extends FieldValue {
    // ----------------------- Constants ----------------------- //
    private static final String TAG = "FILE_FIELD_VALUE";

    // ----------------------- Classes ---------------------------//
    // ----------------------- Interfaces ----------------------- //
    // ----------------------- Globals ----------------------- //
    public String filename = "";

    // ----------------------- Constructor ----------------------- //
    public FileFieldValue(Field field, String value) {
        super(field);

        fromString(value);
    }

    // ----------------------- Abstracts ----------------------- //
    // ----------------------- Overrides ----------------------- //
    // String converter methods
    @Override
    public String toString() {
        return filename;
    }

    @Override
    public void fromString(String value) {
        this.filename = value;
    }

    @Override
    public String getError() {
        String err = "";

        // return error if field is mandatory & value is empty
        if (field.bMandatory && filename.length() == 0) {
            err = "Field is mandatory";
        }

        return err;
    }

    // ----------------------- Public methods ----------------------- //
    // ----------------------- Private methods ----------------------- //
    // ----------------------- Static methods ----------------------- //
}
