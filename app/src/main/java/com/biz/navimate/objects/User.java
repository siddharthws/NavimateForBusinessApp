package com.biz.navimate.objects;

/**
 * Created by Siddharth on 24-09-2017.
 */

public class User {
    // ----------------------- Constants ----------------------- //
    private static final String TAG = "USER";

    public static final int INVALID_ID = -1;

    // ----------------------- Globals ----------------------- //
    public String name = "", phone = "", email = "";
    public int appId = INVALID_ID;

    // ----------------------- Constructor ----------------------- //
    public User (String name, String phone, String email, int appId) {
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.appId = appId;
    }
}
