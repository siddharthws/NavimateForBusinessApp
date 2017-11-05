package com.biz.navimate.activities;

import android.view.View;
import android.widget.Toast;

import com.biz.navimate.R;
import com.biz.navimate.constants.Constants;
import com.biz.navimate.debug.Dbg;
import com.biz.navimate.interfaces.IfaceServer;
import com.biz.navimate.misc.AnimHelper;
import com.biz.navimate.server.RegisterTask;
import com.biz.navimate.viewholders.ActivityHolder;
import com.biz.navimate.views.RlDialog;
import com.biz.navimate.views.RlEnterName;
import com.biz.navimate.views.RlEnterPhone;
import com.biz.navimate.views.RlVerifyPhone;

public class RegistrationActivity   extends     BaseActivity
                                    implements  RlEnterPhone.IfaceEnterPhone,
                                                RlVerifyPhone.VerifyPhoneInterface,
                                                RlEnterName.IfaceEnterName,
                                                IfaceServer.Register
{
    // ----------------------- Constants ----------------------- //
    private static final String TAG = "REGISTRATION_ACTIVITY";


    // ----------------------- Classes ---------------------------//
    // ----------------------- Interfaces ----------------------- //
    // ----------------------- Globals ----------------------- //
    private ActivityHolder.Registration ui = null;

    // Variable to hold information received from different registration views
    private String phoneNumber, name = null;

    // Animation Helper
    private AnimHelper animHelper = null;

    // ----------------------- Constructor ----------------------- //
    // ----------------------- Abstracts ----------------------- //
    // ----------------------- Overrides ----------------------- //
    // Init Overrides

    @Override
    protected void InflateLayout() {
        // Set content view
        setContentView(R.layout.activity_registration);
    }

    @Override
    protected void FindViews() {
        // Init view holder
        ui = new ActivityHolder.Registration();
        holder = ui;

        // Activity View
        ui.rlEnterPhone     = (RlEnterPhone)    findViewById(R.id.rl_enter_phone);
        ui.rlVerifyPhone    = (RlVerifyPhone)   findViewById(R.id.rl_verify_phone);
        ui.rlEnterName      = (RlEnterName)     findViewById(R.id.rl_enter_name);
    }

    @Override
    protected void SetViews() {
        // Init Anim Helper
        animHelper = new AnimHelper(this);

        // Set Listeners
        ui.rlEnterPhone.SetEnterPhoneListener(this);
        ui.rlVerifyPhone.SetVerifyPhoneInterface(this);
        ui.rlEnterName.SetEnterNameListener(this);
    }

    @Override
    public void onBackPressed()
    {
        // Hide any dialog box if visible
        RlDialog.Hide();

        if (ui.rlEnterPhone.getVisibility() == View.VISIBLE)
        {
            // For register phone view, show toast
            Dbg.Toast(this, "Please enter phone number to register...", Toast.LENGTH_SHORT);
        }
        else if (ui.rlVerifyPhone.getVisibility() == View.VISIBLE)
        {
            // For verify phone view, go back to register phone
            ui.rlVerifyPhone.ButtonClickCancel(null);
        }
    }

    @Override
    public void onValidPhoneNumberEntered(String formattedNumber)
    {
        this.phoneNumber = formattedNumber;
        ui.rlVerifyPhone.Init(phoneNumber);
        animHelper.Swap(ui.rlEnterPhone, ui.rlVerifyPhone);
    }

    @Override
    public void onPhoneNumberVerified(String phoneNo)
    {
        animHelper.Swap(ui.rlVerifyPhone, ui.rlEnterName);
    }

    @Override
    public void onVerificationCancel()
    {
        // Hide Verify Phone and show Register Phone View
        animHelper.SwapReverse(ui.rlVerifyPhone, ui.rlEnterPhone);
    }

    @Override
    public void onNameEntered(String name) {
        this.name = name;

        // Attempt user registration
        RegisterTask registerTask = new RegisterTask(this, phoneNumber, name);
        registerTask.SetListener(this);
        registerTask.execute();
    }

    @Override
    public void onRegisterSuccess() {
        // Finish task
        setResult(RESULT_OK);
        finish();
    }

    // ----------------------- Public APIs ----------------------- //
    public static void Start(BaseActivity activity) {
        BaseActivity.Start(activity, RegistrationActivity.class, -1, null, Constants.RequestCodes.REGISTRATION, null);
    }

    // ----------------------- Private APIs ----------------------- //
}
