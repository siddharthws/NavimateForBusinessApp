package com.biz.navimate.interfaces;

/**
 * Created by Siddharth on 25-09-2017.
 */

public class IfacePermission {
    public interface Sms {
        void onSmsPermissionSuccess();
        void onSmsPermissionFailure();
    }

    public interface Location {
        void onLocationPermissionSuccess();
        void onLocationPermissionFailure();
    }

    public interface Call {
        void onCallPermissionSuccess();
        void onCallPermissionFailure();
    }
}
