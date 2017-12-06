package com.biz.navimate.objects;

import com.biz.navimate.constants.Constants;

import org.json.JSONException;
import org.json.JSONObject;

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
    public static Task FromJson(JSONObject taskJson) throws JSONException {
        long dbId               = taskJson.getLong(Constants.Server.KEY_ID);
        long version            = taskJson.getLong(Constants.Server.KEY_VERSION);
        long leadId             = taskJson.getLong(Constants.Server.KEY_LEAD_ID);
        long formTemplateId     = taskJson.getLong(Constants.Server.KEY_FORM_TEMPLATE_ID);
        Task.TaskStatus status  = Task.TaskStatus.valueOf(taskJson.getString(Constants.Server.KEY_STATUS));

        return new Task(dbId, version, leadId, formTemplateId, status);
    }
}
