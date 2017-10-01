package com.biz.navimate.interfaces;

/**
 * Created by Siddharth on 25-09-2017.
 */

public class IfaceResult {
    public interface Registration {
        void onRegisterSuccess();
        void onRegisterFailure();
    }

    // Activity Result interfaces
    public interface ResultGps
    {
        void onGpsEnableSuccess();
        void onGpsEnableFailure();
    }
}
