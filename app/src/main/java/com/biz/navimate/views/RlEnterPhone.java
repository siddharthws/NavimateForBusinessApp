package com.biz.navimate.views;

import android.content.Context;
import android.text.InputFilter;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.biz.navimate.R;
import com.biz.navimate.debug.Dbg;
import com.biz.navimate.misc.PhoneNumberFormatter;
import com.biz.navimate.runnables.KeyboardRunnable;

/**
 * Created by Siddharth on 28-09-2017.
 */

public class RlEnterPhone   extends     RelativeLayout
                            implements  View.OnClickListener
{
    // ----------------------- Constants ----------------------- //
    public static final String TAG = "RL_ENTER_PHONE";

    // ----------------------- Classes ---------------------------//
    // ----------------------- Interfaces ----------------------- //
    public interface IfaceEnterPhone
    {
        void onValidPhoneNumberEntered(String formattedNumber);
    }
    private IfaceEnterPhone listener = null;
    public void SetEnterPhoneListener(IfaceEnterPhone listener)
    {
        this.listener = listener;
    }

    // ----------------------- Globals ----------------------- //
    // UI
    private EditText etPhoneNumber, etCountryCode = null;
    private Button btnVerify = null;

    private KeyboardRunnable keyboardRunnable = null;

    // ----------------------- Constructor ----------------------- //

    public RlEnterPhone(Context context)
    {
        super(context);
        InitView(context);
    }

    public RlEnterPhone(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        InitView(context);
    }

    public RlEnterPhone(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        InitView(context);
    }

    // ----------------------- Overrides ----------------------- //
    @Override
    public void onClick(View view)
    {
        // Get country code and phone number
        String countryCode, phoneNumber = "";
        if (etCountryCode.getText().toString().length() == 0)
        {
            countryCode = "91";
        }
        else
        {
            countryCode = etCountryCode.getText().toString();
        }
        phoneNumber = etPhoneNumber.getText().toString();

        // Validate Phone number
        String formattedNumber = PhoneNumberFormatter.GetFormattedPhoneNo(getContext(), phoneNumber, countryCode);
        if (formattedNumber.length() == 0)
        {
            Dbg.Toast(getContext(), "Invalid phone number", Toast.LENGTH_SHORT);
            return;
        }

        // Hide keyboard
        KeyboardRunnable.Hide(getContext());

        // Call listener
        if (listener != null)
        {
            listener.onValidPhoneNumberEntered(formattedNumber);
        }
    }

    // ----------------------- Public APIs ----------------------- //
    // ----------------------- Private APIs ----------------------- //
    private void InitView(Context context)
    {
        // Inflate Layout
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.custom_rl_enter_phone, this, true);

        // Init UI
        etCountryCode   = (EditText)    view.findViewById(R.id.et_country_code);
        etPhoneNumber   = (EditText)    view.findViewById(R.id.et_phone_number);
        btnVerify       = (Button)      view.findViewById(R.id.btn_verify);

        // Set listeners
        btnVerify.setOnClickListener(this);

        // Set maximum filter on country <code>
        InputFilter[] FilterArray = new InputFilter[1];
        FilterArray[0] = new InputFilter.LengthFilter(4);
        etCountryCode.setFilters(FilterArray);

        // Init other globals
        keyboardRunnable = new KeyboardRunnable(getContext(), etPhoneNumber);

        // Display Keyboard
        keyboardRunnable.Post(0);
    }
}
