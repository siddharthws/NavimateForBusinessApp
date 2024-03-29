package com.biz.navimate.interfaces;

import com.biz.navimate.objects.ObjPlace;
import com.biz.navimate.objects.core.ObjNvmCompact;

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
        void onLeadPicked(ObjNvmCompact obj);
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

    public interface PlacePicker
    {
        void onPlacePicked(ObjPlace place);
    }

    public interface ObjectPicker
    {
        void onObjectPicked(ObjNvmCompact obj);
    }
}
