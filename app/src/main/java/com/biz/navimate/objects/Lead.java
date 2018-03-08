package com.biz.navimate.objects;

import com.biz.navimate.constants.Constants;
import com.biz.navimate.database.DbHelper;
import com.biz.navimate.debug.Dbg;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Siddharth on 28-09-2017.
 */

public class Lead extends ServerObject {
    // ----------------------- Constants ----------------------- //
    private static final String TAG = "LEAD";

    // ----------------------- Globals ----------------------- //
    public String title = "", address = "";
    public long templateId = Constants.Misc.ID_INVALID, dataId = Constants.Misc.ID_INVALID;
    public LatLng position = new LatLng(0, 0);

    // ----------------------- Constructor ----------------------- //
    public Lead (long dbId, long serverId, long version, String title, long templateId, long dataId, String address, LatLng position) {
        super(DbObject.TYPE_LEAD, dbId, serverId, version);
        this.title = title;
        this.dataId = dataId;
        this.templateId = templateId;
        this.address = address;
        this.position = position;
    }

    // ----------------------- Public APIs ----------------------- //
    public static Lead FromJson(JSONObject leadJson) throws JSONException {
        long serverId       = leadJson.getLong(Constants.Server.KEY_ID);
        long version        = leadJson.getLong(Constants.Server.KEY_VERSION);
        String title        = leadJson.getString(Constants.Server.KEY_TITLE);
        long templateId     = leadJson.getLong(Constants.Server.KEY_TEMPLATE_ID);
        long dataId         = leadJson.getLong(Constants.Server.KEY_DATA_ID);
        String address      = leadJson.getString(Constants.Server.KEY_ADDRESS);
        double latitude     = leadJson.getDouble(Constants.Server.KEY_LATITUDE);
        double longitude    = leadJson.getDouble(Constants.Server.KEY_LONGITUDE);

        // Get DbId
        long dbId = Constants.Misc.ID_INVALID;
        Lead existingLead = DbHelper.leadTable.GetByServerId(serverId);
        if (existingLead != null) {
            dbId = existingLead.dbId;
        }

        // Get Local Template Object
        Template template = DbHelper.templateTable.GetByServerId(templateId);
        if (template == null) {
            template = new Template(Constants.Misc.ID_INVALID, templateId, Constants.Misc.ID_INVALID, "", 0, new ArrayList<Long>());
            DbHelper.templateTable.Save(template);
        }

        // Get Local Data Object
        Data data = DbHelper.dataTable.GetByServerId(dataId);
        if (data == null) {
            data = new Data(Constants.Misc.ID_INVALID, dataId, Constants.Misc.ID_INVALID, new ArrayList<Long>());
            DbHelper.dataTable.Save(data);
        }

        return new Lead(dbId, serverId, version, title, template.dbId, data.dbId, address, new LatLng(latitude, longitude));
    }
}
