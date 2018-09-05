package com.biz.navimate.objects.templating.fieldvalues;

import com.biz.navimate.constants.Constants;
import com.biz.navimate.objects.Field;

public abstract class FieldValue {
    // ----------------------- Constants ----------------------- //
    private static final String TAG = "FIELD_VALUE";

    // ----------------------- Abstracts ----------------------- //
    // String converter methods
    public abstract String  toString    ();
    public abstract void    fromString  (String value);

    // Method to check for error in current input data in the editor
    // Returns blank string if no error found
    public abstract String  getError    ();

    // ----------------------- Globals ----------------------- //
    public Field field = null;

    // ----------------------- Constructor ----------------------- //
    public FieldValue(Field field) {
        this.field = field;
    }

    // ----------------------- Static Methods ----------------------- //
    // Method to parse field and value into FieldValue
    public static FieldValue newInstance(Field field, String value) {
        switch (field.type) {
            case Constants.Template.FIELD_TYPE_TEXT: {
                return new TextFieldValue(field, value);
            }
            case Constants.Template.FIELD_TYPE_NUMBER: {
                return new NumberFieldValue(field, value);
            }
            case Constants.Template.FIELD_TYPE_RADIOLIST: {
                return new RadiolistFieldValue(field, value);
            }
            case Constants.Template.FIELD_TYPE_CHECKLIST: {
                return new ChecklistFieldValue(field, value);
            }
        }

        return null;
    }
}
