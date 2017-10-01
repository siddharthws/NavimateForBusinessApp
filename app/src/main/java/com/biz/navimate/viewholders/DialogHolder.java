package com.biz.navimate.viewholders;

import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

/**
 * Created by Siddharth on 27-09-2017.
 */

public class DialogHolder {
    public static abstract class Base
    {
        // Inflated Dialog View
        public View dialogView = null;
    }

    public static class Alert extends Base
    {
        // Message Text
        public TextView tvMessage = null;

        // Button
        public Button btn = null;
    }

    public static class Confirm extends Base
    {
        // Message Text
        public TextView tvMessage = null;

        // Button
        public Button btnYes = null;
        public Button btnNo = null;
    }

    public static class Waiting extends Base
    {
        public TextView tvDialogMessage = null;
    }

    public static class SubmitForm extends Base
    {
        public EditText etSales = null;
        public EditText etNotes = null;
        public RadioButton rbFailed = null;
        public RadioButton rbWaiting = null;
        public RadioButton rbDone = null;
        public CheckBox cbCloseTask = null;
        public Button btnSubmit = null;
        public Button btnCancel = null;
    }
}
