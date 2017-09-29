package com.biz.navimate.maps;

import com.biz.navimate.debug.Dbg;
import com.biz.navimate.objects.Camera;
import com.biz.navimate.objects.Statics;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

/**
 * Created by Siddharth on 29-09-2017.
 */

public class CameraHelper {
    // ----------------------- Constants ----------------------- //
    private static final String TAG = "CAMERA_HELPER";

    // Padding for bounds
    private static final int PADDING_TOP_DP     = 20;
    private static final int PADDING_BOTTOM_DP  = 20;
    private static final int PADDING_RIGHT_DP   = 20;
    private static final int PADDING_LEFT_DP    = 20;

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
        // Update Camera Cache
        this.currentCamera = camera;

        // Update Map if map is available
        if (map != null)
        {
            switch (camera.type)
            {
                case Camera.CAM_UPDATE_LOCATION:
                {
                    MoveToLocation((Camera.Location) camera);
                    break;
                }
                case Camera.CAM_UPDATE_BOUNDS:
                {
                    MoveToBounds((Camera.Bounds) camera);
                    break;
                }
            }
        }
    }

    // API to Load Map Camera
    public void LoadMap(GoogleMap map)
    {
        // Assign Map
        this.map = map;

        // Move Camera as per cache
        if (currentCamera != null)
        {
            Move(currentCamera);
        }
    }

    // ----------------------- Private APIs ----------------------- //
    private void MoveToLocation(Camera.Location camera)
    {
        // Create CameraUpdate based on parameters
        CameraUpdate camUpdate = null;

        // If both zoom and location are provided, update using newLatLngZoom
        if (Statics.IsPositionValid(camera.location) && camera.zoom != 0)
        {
            camUpdate = CameraUpdateFactory.newLatLngZoom(camera.location, camera.zoom);
        }
        // If only location is valid, using newLatLng
        else if (Statics.IsPositionValid(camera.location))
        {
            camUpdate = CameraUpdateFactory.newLatLng(camera.location);
        }
        // If only zoom is valid, use zoomTo
        else if (camera.zoom != 0)
        {
            camUpdate = CameraUpdateFactory.zoomTo(camera.zoom);
        }

        // Move camera as per animation params
        if (camera.bAnimate)
        {
            map.animateCamera(camUpdate);
        }
        else
        {
            map.moveCamera(camUpdate);
        }
    }

    private void MoveToBounds(Camera.Bounds camera)
    {
        // Skip update if no bounds present
        if ((camera.bounds == null) || (camera.bounds.size() < 2))
        {
            Dbg.error(TAG, "Invalid Bound Points");
            return;
        }

        // Create Bounds object from positions
        LatLngBounds.Builder boundBuilder = new LatLngBounds.Builder();
        for (LatLng position : camera.bounds)
        {
            boundBuilder.include(position);
        }
        LatLngBounds bounds = boundBuilder.build();

        // Include Padding. This is necessary to ensure bound points do come on extreme edge of the screen
        bounds = IncludePaddingPoints(bounds);

        // Create Camera Update
        CameraUpdate camUpdate = CameraUpdateFactory.newLatLngBounds(bounds, 0);

        // Move camera as per animation params
        if (camera.bAnimate)
        {
            map.animateCamera(camUpdate);
        }
        else
        {
            map.moveCamera(camUpdate);
        }
    }

    // API to include a given padding to bounds. This ensure bounds are not on the edge of screen but a little inside
    private LatLngBounds IncludePaddingPoints(LatLngBounds bounds)
    {
        LatLngBounds.Builder paddedBoundBuilder = new LatLngBounds.Builder();

        // Radius of earth
        final double EARTH_RADIUS_M = 6372795.477598;

        // Get bound height and width in meters
        int boundHeightM    = Statics.GetDistanceBetweenCoordinates(new LatLng(bounds.northeast.latitude, 0), new LatLng(bounds.southwest.latitude, 0));
        int boundWidthM     = Statics.GetDistanceBetweenCoordinates(new LatLng(0, bounds.northeast.longitude), new LatLng(0, bounds.southwest.longitude));

        // Get map height and width in pixels
        int mapHeightPx     = Statics.SCREEN_SIZE.y;
        int mapWidthPx      = Statics.SCREEN_SIZE.x;

        // Get padding in meters
        int paddingTopM     = (Statics.GetPxFromDip(PADDING_TOP_DP)      * boundHeightM) / mapHeightPx;
        int paddingBottomM  = (Statics.GetPxFromDip(PADDING_BOTTOM_DP)   * boundHeightM) / mapHeightPx;
        int paddingLeftM    = (Statics.GetPxFromDip(PADDING_LEFT_DP)     * boundWidthM)  / mapWidthPx;
        int paddingRightM   = (Statics.GetPxFromDip(PADDING_RIGHT_DP)    * boundWidthM)  / mapWidthPx;

        // Find north east and south west points on map
        LatLng north = new LatLng(bounds.northeast.latitude + (float) (180 * paddingTopM)      / (float) (Math.PI * EARTH_RADIUS_M), bounds.northeast.longitude);
        LatLng south = new LatLng(bounds.southwest.latitude - (float) (180 * paddingBottomM)   / (float) (Math.PI * EARTH_RADIUS_M), bounds.southwest.longitude);
        LatLng east = new LatLng(bounds.northeast.latitude, bounds.northeast.longitude + (float) (180 * paddingRightM)    / (float) (Math.PI * EARTH_RADIUS_M));
        LatLng west = new LatLng(bounds.southwest.latitude, bounds.southwest.longitude - (float) (180 * paddingLeftM)     / (float) (Math.PI * EARTH_RADIUS_M));

        // Include in builder
        paddedBoundBuilder.include(north);
        paddedBoundBuilder.include(south);
        paddedBoundBuilder.include(east);
        paddedBoundBuilder.include(west);

        // Return bounds
        return paddedBoundBuilder.build();
    }
}
