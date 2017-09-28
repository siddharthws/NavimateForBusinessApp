package com.biz.navimate.misc;

import android.content.Context;

import com.biz.navimate.debug.Dbg;

import io.michaelrocks.libphonenumber.android.NumberParseException;
import io.michaelrocks.libphonenumber.android.PhoneNumberUtil;
import io.michaelrocks.libphonenumber.android.Phonenumber;

/**
 * Created by Siddharth on 28-09-2017.
 */

public class PhoneNumberFormatter {
    // ----------------------- Constants ----------------------- //
    private static final String TAG = "PHONE_NUMBER_FORMATTER";

    // Min length for a valid phone number
    private static final int    PHONE_NUMBER_UNKNOWN_TYPE_MIN_LENGTH = 7;

    /*
     * Phone number Validation APIs
     */
    // API to convert String into international format number
    public static String GetFormattedPhoneNo(Context context, String rawNumber, String countryCode)
    {
        PhoneNumberUtil phoneUtil = PhoneNumberUtil.createInstance(context);
        String formattedNumber = "";

        // ParseToObject Raw number to Phone Number Object
        Phonenumber.PhoneNumber phoneNo = ParseStringToPhoneNo(phoneUtil, rawNumber, countryCode);

        // Check Validity of number
        if (IsPhoneNoValid(phoneUtil, phoneNo))
        {
            // Format to internation format
            formattedNumber = ParsePhoneNoToString(phoneUtil, phoneNo);
        }

        return formattedNumber;
    }

    // Create PhoneNumber Object from String
    private static Phonenumber.PhoneNumber ParseStringToPhoneNo(PhoneNumberUtil phoneUtil, String phoneNoString, String countryCode)
    {
        // ParseToObject phone number to international format
        Phonenumber.PhoneNumber phoneNo = null;

        try
        {
            phoneNoString = "+" + countryCode + "" + phoneNoString;
            phoneNo = phoneUtil.parse(phoneNoString, "");
        }
        catch (NumberParseException e)
        {
            Dbg.error(TAG, "Error while parsing phone number : " + phoneNoString);
            Dbg.stack(e);
            phoneNo = null;
        }

        return phoneNo;
    }

    // Validate Phone Number
    private static boolean IsPhoneNoValid(PhoneNumberUtil phoneUtil, Phonenumber.PhoneNumber phoneNo)
    {
        boolean bValid = false;

        // Phone number should not be null
        if (phoneNo != null)
        {
            // Phone Util Validity check
            if (phoneUtil.isPossibleNumber(phoneNo))
            {
                // Phone number type should be valid
                PhoneNumberUtil.PhoneNumberType numberType = phoneUtil.getNumberType(phoneNo);

                if ((numberType == PhoneNumberUtil.PhoneNumberType.MOBILE) ||
                        (numberType == PhoneNumberUtil.PhoneNumberType.FIXED_LINE_OR_MOBILE) ||
                        (numberType == PhoneNumberUtil.PhoneNumberType.PERSONAL_NUMBER))
                {
                    bValid = true;
                }
                else if (numberType == PhoneNumberUtil.PhoneNumberType.UNKNOWN)
                {
                    // For UNKNOWN numbers, perform size check
                    String formattedInternationNumber = ParsePhoneNoToString(phoneUtil, phoneNo);
                    if (formattedInternationNumber.length() > PHONE_NUMBER_UNKNOWN_TYPE_MIN_LENGTH)
                    {
                        bValid = true;
                    }
                    else
                    {
                        Dbg.error(TAG, "Formatted phone number is less than 7 characters");
                    }
                }
                else
                {
                    Dbg.error(TAG, "Unrecognizable Number type " + numberType.toString());
                }
            }
            else
            {
                Dbg.error(TAG, "Phone number is invalid as per phone util");
            }
        }
        else
        {
            Dbg.error(TAG, "Phone number is null");
        }

        return bValid;
    }

    // Create internationally formatted phone number string from honeNumber Object
    private static String ParsePhoneNoToString(PhoneNumberUtil phoneUtil, Phonenumber.PhoneNumber phoneNo)
    {
        String phoneNoString = "";

        phoneNoString = phoneUtil.format(phoneNo, PhoneNumberUtil.PhoneNumberFormat.E164);

        return phoneNoString;
    }
}
