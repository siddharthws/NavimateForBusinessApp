package com.biz.navimate.objects;

import com.biz.navimate.constants.Constants;
import com.biz.navimate.objects.core.ObjDb;

/**
 * Created by Siddharth on 07-12-2017.
 */

public class ServerObject extends ObjDb {
    // ----------------------- Constants ----------------------- //
    private static final String TAG = "SERVER_OBJECT";

    // ----------------------- Globals ----------------------- //
    public long     serverId    = Constants.Misc.ID_INVALID;
    public long     version     = Constants.Misc.ID_INVALID;

    // ----------------------- Constructor ----------------------- //
    public ServerObject(long serverId, long version)
    {
        super();
        this.serverId   = serverId;
        this.version    = version;
    }
}
