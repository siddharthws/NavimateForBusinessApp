package com.biz.navimate.objects;

import com.biz.navimate.constants.Constants;

/**
 * Created by Siddharth on 07-12-2017.
 */

public class ServerObject extends DbObject {
    // ----------------------- Constants ----------------------- //
    private static final String TAG = "SERVER_OBJECT";

    // ----------------------- Globals ----------------------- //
    public long     serverId    = Constants.Misc.ID_INVALID;
    public long     version     = Constants.Misc.ID_INVALID;

    // ----------------------- Constructor ----------------------- //
    public ServerObject(int type,
                        long dbId,
                        long serverId,
                        long version)
    {
        super(type, dbId);
        this.serverId   = serverId;
        this.version    = version;
    }
}
