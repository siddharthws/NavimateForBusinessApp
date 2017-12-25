package com.biz.navimate.objects;

import android.support.annotation.NonNull;

import com.biz.navimate.constants.Constants;
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
    public static final int TYPE_PROGRESS                   = 4;
    public static final int TYPE_SUBMIT_FORM                = 5;
    public static final int TYPE_TASK_INFO                  = 6;
    public static final int TYPE_ROUTE_BUILDER              = 7;
    public static final int TYPE_MAP_SETTINGS               = 8;
    public static final int TYPE_ADD_TASK                   = 9;

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

    public static class AddTask extends Base
    {
        public AddTask()
        {
            super(TYPE_ADD_TASK, true);
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

    public static class Progress extends Base
    {
        public String message = "";
        public int progress = 0;

        public Progress()
        {
            super(TYPE_PROGRESS, false);
        }

        public Progress(String message, int progress)
        {
            super(TYPE_PROGRESS, false);
            this.message = message;
            this.progress = progress;
        }
    }

    public static class SubmitForm extends Base
    {
        public Data data = null;
        public long taskId = Constants.Misc.ID_INVALID;
        public boolean bCloseTask = false, bReadOnly = false;

        public SubmitForm(Data data, long taskId, boolean bCloseTask, boolean bReadOnly)
        {
            super(TYPE_SUBMIT_FORM, true);

            this.data = data;
            this.taskId = taskId;
            this.bCloseTask = bCloseTask;
            this.bReadOnly = bReadOnly;
        }
    }

    public static class TaskInfo extends Base
    {
        public Task task = null;

        public TaskInfo(Task task)
        {
            super(TYPE_TASK_INFO, true);
            this.task = task;
        }
    }

    public static class RouteBuilder extends Base
    {
        public ArrayList<Lead> leads = null;
        public IfaceDialog.RouteBuilder listener = null;

        public RouteBuilder(@NonNull ArrayList<Lead> leads, IfaceDialog.RouteBuilder listener)
        {
            super(TYPE_ROUTE_BUILDER, true);
            this.leads = leads;
            this.listener = listener;
        }
    }

    public static class MapSettings extends Base
    {
        public IfaceDialog.MapSettings listener = null;

        public MapSettings(IfaceDialog.MapSettings listener)
        {
            super(TYPE_MAP_SETTINGS, true);
            this.listener = listener;
        }
    }
}
