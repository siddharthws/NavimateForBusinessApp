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
        void onLeadPicked(ArrayList<Long> leads);
    }

    public interface Zxing
    {
        void onScanResult(String data);
    }

    public interface Photo
    {
        void onPhotoResult();
    }

    public interface Crop
    {
        void onPhotoCrop();
    }

    public interface PhotoEditor
    {
        void onPhotoEditorResult(String fileName);
    }

    public interface PhotoDraw
    {
        void onPhotoDraw(String fileName);
    }

    public interface Signature
    {
        void onSignatureResult(String fileName);
    }

    public interface FilePicker
    {
        void onFilePicked(String filename);
    }
}
