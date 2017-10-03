package com.biz.navimate.viewholders;

import android.support.v7.widget.AppCompatCheckBox;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
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

    public static class TaskInfo extends Base
    {
        public TextView tvTitle = null;
        public TextView tvDescription = null;
        public TextView tvPhone = null;
        public TextView tvEmail = null;
        public Button btnSubmit = null;
        public Button btnDismiss = null;
    }

    public static class RouteBuilder extends Base
    {
        public LinearLayout llAddLeads = null;
        public ListView lvLeads = null;
        public AppCompatCheckBox cbOptimize = null;

        public Button btnBuild = null;
        public Button btnCancel = null;
    }
}
