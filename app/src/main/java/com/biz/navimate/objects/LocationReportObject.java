package com.biz.navimate.objects;

/**
 * Created by Siddharth on 03-02-2018.
 */

public class LocationReportObject extends DbObject {
    // ----------------------- Constants ----------------------- //
    private static final String TAG = "LOCATION_REPORT_OBJECT";

    // ----------------------- Globals ----------------------- //
    public double latitude, longitude;
    public long timestamp;
    public int status;
    public int battery;
    public float speed;

    // ----------------------- Constructor ----------------------- //
    public LocationReportObject (long dbId, double latitude, double longitude, long timestamp, int status, int battery, float speed) {
        super(DbObject.TYPE_LOCATION_REPORT, dbId);
        this.latitude = latitude;
        this.longitude = longitude;
        this.timestamp = timestamp;
        this.status = status;
        this.battery = battery;
        this.speed = speed;
    }

    // ----------------------- Public APIs ----------------------- //
}
