package com.biz.navimate.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.biz.navimate.R;
import com.biz.navimate.debug.Dbg;
import com.biz.navimate.runnables.KeyboardRunnable;

/**
 * Created by Siddharth on 05-11-2017.
 */

public class RlEnterName extends RelativeLayout implements View.OnClickListener
{
    // ----------------------- Constants ----------------------- //
    public static final String TAG = "RL_ENTER_NAME";

    // ----------------------- Classes ---------------------------//
    // ----------------------- Interfaces ----------------------- //
    public interface IfaceEnterName
    {
        void onNameEntered(String name);
    }
    private IfaceEnterName listener = null;
    public void SetEnterNameListener(IfaceEnterName listener)
    {
        this.listener = listener;
    }

    // ----------------------- Globals ----------------------- //
    // UI
    private EditText etName = null;
    private Button btnRegister = null;

    private KeyboardRunnable keyboardRunnable = null;

    // ----------------------- Constructor ----------------------- //

    public RlEnterName(Context context)
    {
        super(context);
        InitView(context);
    }

    public RlEnterName(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        InitView(context);
    }

    public RlEnterName(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        InitView(context);
    }

    // ----------------------- Overrides ----------------------- //
    @Override
    public void onClick(View view)
    {
        // Get country code and phone number
        String name = etName.getText().toString();

        // Validate Name
        if (name.length() == 0)
        {
            Dbg.Toast(getContext(), "Invalid name...", Toast.LENGTH_SHORT);
            return;
        }

        // Hide keyboard
        KeyboardRunnable.Hide(getContext());

        // Call listener
        if (listener != null)
        {
            listener.onNameEntered(name);
        }
    }

    // ----------------------- Public APIs ----------------------- //
    // ----------------------- Private APIs ----------------------- //
    private void InitView(Context context)
    {
        // Inflate Layout
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.custom_rl_enter_name, this, true);

        // Init UI
        etName          = (EditText)    view.findViewById(R.id.et_name);
        btnRegister     = (Button)      view.findViewById(R.id.btn_register);

        // Set listeners
        btnRegister.setOnClickListener(this);

        // Init other globals
        keyboardRunnable = new KeyboardRunnable(getContext(), etName);

        // Display Keyboard
        keyboardRunnable.Post(0);
    }
}
