package com.biz.navimate.maps;

import com.biz.navimate.objects.Camera;
import com.google.android.gms.maps.GoogleMap;

/**
 * Created by Siddharth on 29-09-2017.
 */

public class CameraHelper {
    // ----------------------- Constants ----------------------- //
    private static final String TAG = "CAMERA_HELPER";

    // ----------------------- Interfaces ----------------------- //
    // ----------------------- Globals ----------------------- //
    private GoogleMap map = null;

    // Current Camera position
    private Camera.Base currentCamera = null;

    // ----------------------- Constructor ----------------------- //
    public CameraHelper()
    {

    }

    // ----------------------- Overrides ----------------------- //
    // ----------------------- Public APIs ----------------------- //
    // API to move the camera to a specific position
    //      - latlng - location to move
    //      - zoom - zoom level of the camera
    //      - bAnimate - Whether camera should be animated
    public void Move(Camera.Base camera)
    {
        // Place Holder
    }

    // API to set camera such that the array of locations is displayed
    public void ShowBounds(Camera.Base camera)
    {
        // Place Holder
    }

    // API to Load Map Camera
    public void LoadMap(GoogleMap map)
    {
        // Place Holder
    }

    // ----------------------- Private APIs ----------------------- //
}
