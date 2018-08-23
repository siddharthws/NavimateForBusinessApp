package com.biz.navimate.views;

import android.Manifest;
import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.PermissionChecker;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.biz.navimate.R;
import com.biz.navimate.activities.BaseActivity;
import com.biz.navimate.application.App;
import com.biz.navimate.constants.Constants;
import com.biz.navimate.debug.Dbg;
import com.biz.navimate.interfaces.IfacePermission;
import com.biz.navimate.interfaces.IfaceServer;
import com.biz.navimate.misc.OtpGenerator;
import com.biz.navimate.receivers.SmsReceiver;
import com.biz.navimate.runnables.KeyboardRunnable;
import com.biz.navimate.runnables.VerifyPhoneResendRunnable;
import com.biz.navimate.server.OtpSmsTask;

/**
 * Created by Siddharth on 28-09-2017.
 */

public class RlVerifyPhone  extends     RelativeLayout
                            implements  SmsReceiver.SmsReceiverInterface,
                                        View.OnClickListener,
                                        IfacePermission.Sms,
                                        IfaceServer.OtpSms {
    // ----------------------- Constants ----------------------- //
    public static final String TAG = "RL_VERIFY_PHONE";

    // Minimum time between sms resending
    private static final int SMS_RESEND_TIME_S = 60;

    // Template for verification SMS
    private static final String VERIFICATION_SMS_TEXT = " is your OTP for Navimate verification.";
    private static final String DESCRIPTION_START = "A 6-digit OTP has been sent via SMS on ";
    private static final String DESCRIPTION_END = ". Enter the OTP to verify this number.";

    // ----------------------- Classes ---------------------------//
    // ----------------------- Interfaces ----------------------- //
    public interface VerifyPhoneInterface {
        void onPhoneNumberVerified(String phoneNo);

        void onVerificationCancel();
    }

    private VerifyPhoneInterface listener = null;

    public void SetVerifyPhoneInterface(VerifyPhoneInterface listener) {
        this.listener = listener;
    }

    // ----------------------- Globals ----------------------- //
    // UI
    private EditText etOtp = null;
    private Button btnVerify = null, btnCancel = null, btnResend = null;
    private TextView tvDescriptor = null;

    private KeyboardRunnable keyboardRunnable = null;
    private static String generatedOtp = "";
    private String phoneNumber = "";
    private VerifyPhoneResendRunnable resendRunnable = null;
    private SmsReceiver smsReceiver = null;

    // ----------------------- Constructor ----------------------- //

    public RlVerifyPhone(Context context) {
        super(context);
        InitView(context);
    }

    public RlVerifyPhone(Context context, AttributeSet attrs) {
        super(context, attrs);
        InitView(context);
    }

    public RlVerifyPhone(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        InitView(context);
    }

    // ----------------------- Overrides ----------------------- //
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_verify: {
                ButtonClickVerify(view);
                break;
            }
            case R.id.btn_cancel: {
                ButtonClickCancel(view);
                break;
            }
            case R.id.btn_resend: {
                ButtonClickResend(view);
                break;
            }
        }
    }

    @Override
    public void onSmsPermissionSuccess() {
        // Register Receiver
        smsReceiver.Register(getContext());

        // Send SMS
        SendVerificationSms();
    }

    @Override
    public void onSmsPermissionFailure() {
        // Show keyboard since user will enter OTP manually
        keyboardRunnable.Post(0);

        // Send SMS without receiver
        SendVerificationSms();
    }

    @Override
    public void onSmsSuccess() {
        // Restart Resend Runnable
        resendRunnable.Post(0);
    }

    @Override
    public void onSmsFailure() {
        // Hide keyboard
        KeyboardRunnable.Hide(getContext());

        // Stop Resend Runnable
        resendRunnable.Unpost();

        // Unregister receiver
        smsReceiver.UnregisterReceiver(getContext());

        // Call cancel listener
        if (listener != null) {
            listener.onVerificationCancel();
        }
    }

    @Override
    public void onSmsReceived(String text) {
        // Check if message text is same as sent text
        if (text.equals(generatedOtp + VERIFICATION_SMS_TEXT)) {
            OtpVerified();
        }
    }

    // ----------------------- Public APIs ----------------------- //
    public void Init(String phoneNumber) {
        this.phoneNumber = phoneNumber;
        this.generatedOtp = OtpGenerator.GetOtp();

        // Set Description text
        tvDescriptor.setText(DESCRIPTION_START + phoneNumber + DESCRIPTION_END);

        // Check permission
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.RECEIVE_SMS) == PermissionChecker.PERMISSION_GRANTED) {
            onSmsPermissionSuccess();
        } else {
            BaseActivity currentActivity = App.GetCurrentActivity();

            if (currentActivity != null) {
                currentActivity.SetSmsPermissionListener(this);
                currentActivity.RequestPermission(new String[]{Manifest.permission.RECEIVE_SMS});
            } else {
                Dbg.error(TAG, "Current Activity is null. Cannot ask permission");
            }
        }
    }

    public void ButtonClickCancel(View view) {
        // Hide keyboard
        KeyboardRunnable.Hide(getContext());

        // Stop Resend Runnable
        resendRunnable.Unpost();

        // Unregister receievr
        smsReceiver.UnregisterReceiver(getContext());

        // Call cancel listener
        if (listener != null) {
            listener.onVerificationCancel();
        }
    }

    // ----------------------- Private APIs ----------------------- //
    private void InitView(Context context) {
        // Inflate Layout
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.custom_rl_verify_phone, this, true);

        // Init UI
        etOtp = (EditText) view.findViewById(R.id.et_otp);
        btnVerify = (Button) view.findViewById(R.id.btn_verify);
        btnCancel = (Button) view.findViewById(R.id.btn_cancel);
        btnResend = (Button) view.findViewById(R.id.btn_resend);
        tvDescriptor = (TextView) view.findViewById(R.id.tv_verify_phone_descriptor);

        // Disable Resend and Verify button
        btnVerify.setEnabled(false);
        btnResend.setEnabled(false);

        // Init Globals
        smsReceiver = new SmsReceiver();
        resendRunnable = new VerifyPhoneResendRunnable(btnResend);
        keyboardRunnable = new KeyboardRunnable(getContext(), etOtp);

        // Set listeners
        btnVerify.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
        btnResend.setOnClickListener(this);
        smsReceiver.SetSmsReceiverInterface(this);
        etOtp.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.toString().length() == 6) {
                    btnVerify.setEnabled(true);
                } else {
                    btnVerify.setEnabled(false);
                }
            }
        });
    }

    // Button Click APIs
    private void ButtonClickVerify(View view) {
        // Get OTP Entered
        String otp = etOtp.getText().toString();

        // Verify OTP
        if (otp.equals(generatedOtp)) {
            OtpVerified();
        } else {
            Dbg.Toast(getContext(), "Invalid OTP...", Toast.LENGTH_SHORT);
        }
    }

    private void ButtonClickResend(View view) {
        // Send SMS again
        SendVerificationSms();
    }

    // Helper APIs
    private void OtpVerified() {
        // Show toast
        Dbg.Toast(getContext(), "Phone Number Verified...", Toast.LENGTH_SHORT);

        // Reset Otp
        OtpGenerator.ResetOtp();

        // Hide keyboard
        KeyboardRunnable.Hide(getContext());

        // Stop Resend Runnable
        resendRunnable.Unpost();

        // Unregister receievr
        smsReceiver.UnregisterReceiver(getContext());

        // Call listener
        if (listener != null) {
            listener.onPhoneNumberVerified(phoneNumber);
        }
    }

    private void SendVerificationSms() {
        String smsText = generatedOtp + VERIFICATION_SMS_TEXT;

        if (Constants.App.DEBUG) {
            // Skip SMS sending SMS
            onSmsReceived(smsText);
        } else {
            // Send SMS through server
            OtpSmsTask smsTask = new OtpSmsTask(getContext(), phoneNumber, smsText);
            smsTask.SetListener(this);
            smsTask.execute();
        }
    }
}
