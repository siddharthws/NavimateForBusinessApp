package com.biz.navimate.objects;

import com.biz.navimate.constants.Constants;
import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Siddharth on 28-09-2017.
 */

public class Form extends ServerObject {
    // ----------------------- Constants ----------------------- //
    private static final String TAG = "FORM";

    // ----------------------- Globals ----------------------- //
    public Long dataId = Constants.Misc.ID_INVALID;
    public Long templateId = Constants.Misc.ID_INVALID;
    public Long taskId = Constants.Misc.ID_INVALID;
    public Boolean bCloseTask = false;
    public Long timestamp = 0L;
    public LatLng latlng = null;

    // ----------------------- Constructor ----------------------- //
    public Form() {
        super(DbObject.TYPE_FORM, Constants.Misc.ID_INVALID, Constants.Misc.ID_INVALID, Constants.Misc.ID_INVALID);
    }

    public Form(Long templateId, Long taskId, Long dataId, boolean bCloseTask) {
        super(DbObject.TYPE_FORM, Constants.Misc.ID_INVALID, Constants.Misc.ID_INVALID, Constants.Misc.ID_INVALID);
        this.templateId = templateId;
        this.taskId = taskId;
        this.dataId = dataId;
        this.bCloseTask = bCloseTask;
    }

    public Form (long dbId, long serverId, long version, Long templateId, Long taskId, Long dataId, boolean bCloseTask, LatLng latlng, Long timestamp) {
        super(DbObject.TYPE_FORM, dbId, serverId, version);
        this.templateId = templateId;
        this.taskId = taskId;
        this.dataId = dataId;
        this.bCloseTask = bCloseTask;
        this.latlng = latlng;
        this.timestamp = timestamp;
    }
}
