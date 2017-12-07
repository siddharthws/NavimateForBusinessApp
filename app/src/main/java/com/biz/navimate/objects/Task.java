package com.biz.navimate.objects;

import com.biz.navimate.constants.Constants;
import com.biz.navimate.database.DbHelper;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Siddharth on 28-09-2017.
 */

public class Task extends ServerObject {
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
    public Task (long dbId, long serverId, long version, long leadId, long formTemplateId, TaskStatus status) {
        super(DbObject.TYPE_TASK, dbId, serverId, version);
        this.leadId             = leadId;
        this.formTemplateId     = formTemplateId;
        this.status             = status;
    }
    // ----------------------- Public APIs ----------------------- //
    public static Task FromJson(JSONObject taskJson) throws JSONException {
        long serverId           = taskJson.getLong(Constants.Server.KEY_ID);
        long version            = taskJson.getLong(Constants.Server.KEY_VERSION);
        long leadId             = taskJson.getLong(Constants.Server.KEY_LEAD_ID);
        long formTemplateId     = taskJson.getLong(Constants.Server.KEY_FORM_TEMPLATE_ID);
        Task.TaskStatus status  = Task.TaskStatus.valueOf(taskJson.getString(Constants.Server.KEY_STATUS));

        // Get Local Lead Object
        Lead lead = DbHelper.leadTable.GetByServerId(leadId);
        if (lead == null) {
            lead = new Lead(Constants.Misc.ID_INVALID, leadId, Constants.Misc.ID_INVALID, "", "", "", "", "", new LatLng(0, 0));
            DbHelper.leadTable.Save(lead);
        }

        // Get Local Template Object
        Form form = DbHelper.formTable.GetByServerId(formTemplateId);
        if (form == null) {
            form = new Form(Constants.Misc.ID_INVALID, formTemplateId, Constants.Misc.ID_INVALID, "", new ArrayList<FormField.Base>());
            DbHelper.formTable.Save(form);
        }

        // Get DbId
        long dbId = Constants.Misc.ID_INVALID;
        Task existingTask = DbHelper.taskTable.GetByServerId(serverId);
        if (existingTask != null) {
            dbId = existingTask.dbId;
        }

        return new Task(dbId, serverId, version, lead.dbId, form.dbId, status);
    }
}
