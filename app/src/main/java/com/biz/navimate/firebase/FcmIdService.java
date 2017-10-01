package com.biz.navimate.firebase;

import com.biz.navimate.debug.Dbg;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by Siddharth on 01-10-2017.
 */

public class FcmIdService extends FirebaseInstanceIdService {
    // ----------------------- Constants ----------------------- //
    private static final String TAG = "FCM_ID_SERVICE";

    // ----------------------- Interfaces ----------------------- //
    // ----------------------- Globals ----------------------- //
    // ----------------------- Constructor ----------------------- //
    public FcmIdService() {
        super();
    }

    // ----------------------- Overrides ----------------------- //
    @Override
    public void onTokenRefresh() {
        Dbg.info(TAG, "Firebase token refreshed : " + FirebaseInstanceId.getInstance().getToken());
    }

    // ----------------------- Public APIs ----------------------- //
    // ----------------------- Private APIs ----------------------- //
}
