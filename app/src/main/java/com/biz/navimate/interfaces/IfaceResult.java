package com.biz.navimate.interfaces;

import java.util.ArrayList;

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

    public interface LeadPicker
    {
        void onLeadPicked(ArrayList<Integer> leads);
    }

    public interface Zxing
    {
        void onScanResult(String data);
    }
}
