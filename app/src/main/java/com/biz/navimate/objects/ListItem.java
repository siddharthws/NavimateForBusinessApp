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
}
