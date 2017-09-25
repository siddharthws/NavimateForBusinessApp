package com.biz.navimate.misc;

import java.util.Random;

/**
 * Created by Siddharth on 19-04-2017.
 */

public class OtpGenerator
{
    // ----------------------- Constants ----------------------- //
    private static final String TAG = "OTP_GENERATOR";

    private static final int    OTP_NUM_DIGITS          = 6;

    // ----------------------- Globals ----------------------- //
    private static String otp = "";

    // ----------------------- Constructor ----------------------- //
    // ----------------------- Public APIs ----------------------- //
    /*
     * Api to check if internet is available or not
     */
    public static String GetOtp ()
    {
        // Generate OTP once for one session
        if (otp.length() == 0)
        {
            otp = GenerateOtp();
        }

        return otp;
    }

    public static void ResetOtp()
    {
        otp = "";
    }

    // ----------------------- Private APIs ----------------------- //
    private static String GenerateOtp()
    {
        String otp = "";
        Random rand = new Random();

        for (int i = 0; i < OTP_NUM_DIGITS; i++)
        {
            int digit = rand.nextInt(10);
            otp += String.valueOf(digit);
        }

        return otp;
    }
}
