package com.biz.navimate.viewholders;

import android.view.View;
import android.widget.Button;
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
}
