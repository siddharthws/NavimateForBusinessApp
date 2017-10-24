package com.biz.navimate.objects;

import android.support.annotation.NonNull;

import com.biz.navimate.interfaces.IfaceDialog;

import java.util.ArrayList;

/**
 * Created by Siddharth on 27-09-2017.
 */

public class Dialog {
    // Dialog Types
    public static final int TYPE_NONE                       = 0;
    public static final int TYPE_ALERT                      = 1;
    public static final int TYPE_CONFIRM                    = 2;
    public static final int TYPE_WAITING                    = 3;
    public static final int TYPE_SUBMIT_FORM                = 4;
    public static final int TYPE_TASK_INFO                  = 5;
    public static final int TYPE_ROUTE_BUILDER              = 6;

    // Base class for all dialog objects
    public static abstract class Base
    {
        // Globals
        public int      type            = TYPE_NONE;
        public boolean  bCancellable    = false;

        public Base (int type, boolean bCancellable)
        {
            this.type = type;
            this.bCancellable = bCancellable;
        }
    }

    public static class Alert extends Base
    {
        // Globals
        public String message = "";

        public Alert()
        {
            super(TYPE_ALERT, true);
        }

        public Alert(String message)
        {
            super(TYPE_ALERT, true);
            this.message = message;
        }
    }

    public static class Confirm extends Base
    {
        // Globals
        public String message = "";
        public IfaceDialog.Confirm listener = null;

        public Confirm()
        {
            super(TYPE_CONFIRM, true);
        }

        public Confirm(String message, IfaceDialog.Confirm listener)
        {
            super(TYPE_CONFIRM, true);
            this.message = message;
            this.listener = listener;
        }
    }

    public static class Waiting extends Base
    {
        public String message = "";

        public Waiting(String message)
        {
            super(TYPE_WAITING, false);
            this.message = message;
        }
    }

    public static class SubmitForm extends Base
    {
        public Form form = null;
        public int taskId = -1;
        public boolean bCloseTask = false;

        public SubmitForm(Form form, int taskId, boolean bCloseTask)
        {
            super(TYPE_SUBMIT_FORM, false);

            this.form = new Form(   form.name,
                                    form.fields);
            this.taskId = taskId;
            this.bCloseTask = bCloseTask;
        }
    }

    public static class TaskInfo extends Base
    {
        public Task task = null;

        public TaskInfo(Task task)
        {
            super(TYPE_TASK_INFO, false);
            this.task = task;
        }
    }

    public static class RouteBuilder extends Base
    {
        public ArrayList<Lead> leads = null;
        public IfaceDialog.RouteBuilder listener = null;

        public RouteBuilder(@NonNull ArrayList<Lead> leads, IfaceDialog.RouteBuilder listener)
        {
            super(TYPE_ROUTE_BUILDER, false);
            this.leads = leads;
            this.listener = listener;
        }
    }
}
