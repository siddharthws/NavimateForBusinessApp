package com.biz.navimate.objects.templating.fieldvalues;

import com.biz.navimate.debug.Dbg;
import com.biz.navimate.objects.Field;

import org.json.JSONArray;
import org.json.JSONException;

public class PhotoFieldValue extends FieldValue {
    // ----------------------- Constants ----------------------- //
    private static final String TAG = "PHOTO_FIELD_VALUE";

    // ----------------------- Classes ---------------------------//
    // ----------------------- Interfaces ----------------------- //
    // ----------------------- Globals ----------------------- //
    public String filename = "";

    // ----------------------- Constructor ----------------------- //
    public PhotoFieldValue(Field field, String value) {
        super(field);

        fromString(value);
    }

    // ----------------------- Abstracts ----------------------- //
    // ----------------------- Overrides ----------------------- //
    // String converter methods
    @Override
    public String toString() {
        JSONArray array = new JSONArray();
        array.put(filename);
        return array.toString();
    }

    @Override
    public void fromString(String value) {
        try {
            JSONArray fileArray = new JSONArray(value);
            if (fileArray.length() > 0) {
                filename = fileArray.getString(0);
            }
        } catch (JSONException e) {
            Dbg.error(TAG, "Could not parse array from value");
        }
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
