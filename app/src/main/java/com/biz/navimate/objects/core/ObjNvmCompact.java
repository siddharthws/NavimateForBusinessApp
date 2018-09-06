package com.biz.navimate.objects.core;

import com.biz.navimate.constants.Constants;
import com.biz.navimate.debug.Dbg;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

public class ObjNvmCompact implements Serializable {
    // ----------------------- Constants ----------------------- //
    private static final String TAG = "OBJ_NVM_COMPACT";

    // ----------------------- Globals ----------------------- //
    public String id = "";
    public String name = "";

    // ----------------------- Constructor ----------------------- //
    public ObjNvmCompact() {}

    public ObjNvmCompact(String jsonString) {
        fromString(jsonString);
    }

    public ObjNvmCompact(String id, String name) {
        this.id = id;
        this.name = name;
    }

    // ----------------------- Public APIs ----------------------- //
    // String converter methods
    @Override
    public String toString() {
        JSONObject json = new JSONObject();
        // Create JSON Object for Product Data
        try {
            json.put(Constants.Server.KEY_ID, id);
            json.put(Constants.Server.KEY_NAME, name);
        } catch (JSONException e) {
            Dbg.stack(e);
        }

        return json.toString();
    }

    public void fromString(String value) {
        try {
            JSONObject productJson = new JSONObject(value);
            id = productJson.getString(Constants.Server.KEY_ID);
            name = productJson.getString(Constants.Server.KEY_NAME);
        } catch (JSONException e) {
            Dbg.error(TAG, "Error while converting compact object data");
            Dbg.stack(e);
        }
    }
}
