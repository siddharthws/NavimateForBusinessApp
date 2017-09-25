package com.biz.navimate.interfaces;

import com.biz.navimate.objects.User;

/**
 * Created by Siddharth on 25-09-2017.
 */

public class IfaceServer {
    // GetProfile Task interface
    public interface GetProfile {
        void onProfileReceived(User user);
        void onProfileFailed();
    }

    // OtpSms Task interface
    public interface OtpSms {
        void onSmsSuccess();
        void onSmsFailure();
    }
}
