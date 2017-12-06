package com.biz.navimate.objects;

import com.biz.navimate.constants.Constants;

/**
 * Created by Siddharth on 28-09-2017.
 */

public class Task extends DbObject {
    // ----------------------- Constants ----------------------- //
    private static final String TAG = "TASK";

    public static enum TaskStatus {
        OPEN, CLOSED
    }

    // ----------------------- Globals ----------------------- //
    public long  leadId              = Constants.Misc.ID_INVALID;
    public long  formTemplateId      = Constants.Misc.ID_INVALID;
    public TaskStatus status         = TaskStatus.OPEN;

    // ----------------------- Constructor ----------------------- //
    public Task () {}

    public Task (long id, long version, long leadId, long formTemplateId, TaskStatus status) {
        super(DbObject.TYPE_TASK, version, id);
        this.leadId             = leadId;
        this.formTemplateId     = formTemplateId;
        this.status             = status;
    }
    // ----------------------- Public APIs ----------------------- //
}
