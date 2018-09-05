package com.biz.navimate.objects.templating.fieldvalues;

import com.biz.navimate.constants.Constants;
import com.biz.navimate.debug.Dbg;
import com.biz.navimate.objects.Field;

import java.util.Calendar;

public class DateFieldValue extends FieldValue {
    // ----------------------- Constants ----------------------- //
    private static final String TAG = "DATE_FIELD_VALUE";

    // ----------------------- Classes ---------------------------//
    // ----------------------- Interfaces ----------------------- //
    // ----------------------- Globals ----------------------- //
    public Calendar cal = null;

    // ----------------------- Constructor ----------------------- //
    public DateFieldValue(Field field, String value) {
        super(field);

        fromString(value);
    }

    // ----------------------- Abstracts ----------------------- //
    // ----------------------- Overrides ----------------------- //
    // String converter methods
    @Override
    public String toString() {
        String val = "";

        if (cal != null) {
            val = Constants.Formatters.DATE_BACKEND.format(cal.getTime());
        }

        return val;
    }

    @Override
    public void fromString(String value) {
        if (value != null && value.length() > 0 && !value.equals("null")) {
            try {
                cal = Calendar.getInstance();
                cal.setTime(Constants.Formatters.DATE_BACKEND.parse(value));
            } catch (Exception e) {
                Dbg.error(TAG, "Could not parse to date : " + value);
                Dbg.stack(e);
            }
        }
    }

    @Override
    public String getError() {
        String err = "";

        // return error if field is mandatory & value is empty
        if (field.bMandatory && cal == null) {
            err = "Field is mandatory";
        }

        return err;
    }

    // ----------------------- Public methods ----------------------- //
    // ----------------------- Private methods ----------------------- //
    // ----------------------- Static methods ----------------------- //
}
