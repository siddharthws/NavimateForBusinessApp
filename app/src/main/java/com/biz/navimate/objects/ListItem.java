package com.biz.navimate.objects;

/**
 * Created by Siddharth on 29-09-2017.
 */

public class ListItem {
    // ----------------------- Constants ----------------------- //
    // Types of list items
    public static final int TYPE_INVALID                = 0;
    public static final int TYPE_TASK                   = 1;
    public static final int TYPE_LEAD                   = 2;

    // Drawer related List items
    public static final int TYPE_DRAWER_GROUP_SEPARATOR = 3;
    public static final int TYPE_DRAWER_ITEM_SEPARATOR  = 4;
    public static final int TYPE_DRAWER_ITEM            = 5;

    // ----------------------- Classes ---------------------------//
    // Base Class for list Item Data
    public static abstract class Base
    {
        // ----------------------- Globals ----------------------- //
        public int    type              = TYPE_INVALID;

        // ----------------------- Constructor ----------------------- //
        public Base(int type)
        {
            this.type               = type;
        }
    }

    // Data object for task list item
    public static class Task extends Base {
        // ----------------------- Globals ----------------------- //
        public com.biz.navimate.objects.Task task = null;

        // ----------------------- Constructor ----------------------- //
        public Task(com.biz.navimate.objects.Task task)
        {
            super(TYPE_TASK);
            this.task               = task;
        }
    }

    // Data object for task list item
    public static class Lead extends Base {
        // ----------------------- Globals ----------------------- //
        public com.biz.navimate.objects.Lead lead = null;
        public boolean bSelected = true;

        // ----------------------- Constructor ----------------------- //
        public Lead(com.biz.navimate.objects.Lead lead, boolean bSelected)
        {
            super(TYPE_LEAD);
            this.lead               = lead;
            this.bSelected          = bSelected;
        }
    }

    // Drawer list item
    public static class Drawer extends Base
    {
        // ----------------------- Globals ----------------------- //
        public String title             = "";
        public int action               = 0;
        public int imageId              = 0;

        // ----------------------- Constructor ----------------------- //
        public Drawer(int type, int action, String title, int imageId)
        {
            super(type);

            this.title      = title;
            this.imageId    = imageId;
            this.action     = action;
        }
    }
}
