package com.biz.navimate.objects.templating.fieldvalues;

import com.biz.navimate.objects.Field;
import com.biz.navimate.objects.core.ObjNvmCompact;

public class ObjectFieldValue extends FieldValue {
    // ----------------------- Constants ----------------------- //
    private static final String TAG = "DATE_FIELD_VALUE";

    // ----------------------- Classes ---------------------------//
    // ----------------------- Interfaces ----------------------- //
    // ----------------------- Globals ----------------------- //
    public ObjNvmCompact obj = null;

    // ----------------------- Constructor ----------------------- //
    public ObjectFieldValue(Field field, String value) {
        super(field);

        fromString(value);
    }

    // ----------------------- Abstracts ----------------------- //
    // ----------------------- Overrides ----------------------- //
    // String converter methods
    @Override
    public String toString() {
        if (obj != null) {
            return obj.toString();
        } else {
            return "";
        }
    }

    @Override
    public void fromString(String value) {
        if (value == null || value.length() == 0) {
            obj = null;
        } else if (obj != null) {
            obj.fromString(value);
        } else {
            obj = new ObjNvmCompact(value);
        }
    }

    @Override
    public String getError() {
        String err = "";

        // return error if field is mandatory & value is empty
        if (field.bMandatory && obj == null) {
            err = "Field is mandatory";
        }

        return err;
    }

    // ----------------------- Public methods ----------------------- //
    // ----------------------- Private methods ----------------------- //
    // ----------------------- Static methods ----------------------- //
}
