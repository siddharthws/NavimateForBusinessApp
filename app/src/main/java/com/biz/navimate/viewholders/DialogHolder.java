package com.biz.navimate.viewholders;

import android.support.v7.widget.AppCompatCheckBox;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.biz.navimate.views.RlFormField;
import com.biz.navimate.views.TvCalibri;

import java.util.ArrayList;

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

    public static class Progress extends Base
    {
        public TvCalibri tvMessage = null;
        public TvCalibri tvProgress = null;
        public ProgressBar pbProgress = null;
    }

    public static class SubmitForm extends Base
    {
        public ArrayList<RlFormField> fields = null;
        public LinearLayout llFields = null;
        public CheckBox cbCloseTask = null;
        public Button btnSubmit = null;
        public Button btnCancel = null;
    }

    public static class TaskInfo extends Base
    {
        public TextView tvTitle = null;
        public TextView tvDescription = null;
        public TextView tvAddress = null;
        public Button btnPhone = null;
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
