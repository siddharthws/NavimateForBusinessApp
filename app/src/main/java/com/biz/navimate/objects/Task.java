package com.biz.navimate.objects;

/**
 * Created by Siddharth on 28-09-2017.
 */

public class Task {
    // ----------------------- Constants ----------------------- //
    private static final String TAG = "TASK";

    public static final int INVALID_ID = -1;

    // ----------------------- Globals ----------------------- //
    public int  id          = INVALID_ID;
    public Lead lead        = null;
    public Form template    = null;

    // ----------------------- Constructor ----------------------- //
    public Task () {}

    public Task (int id, Lead lead, Form template) {
        this.id = id;
        this.lead = lead;
        this.template = template;
    }

    // ----------------------- Public APIs ----------------------- //
}
