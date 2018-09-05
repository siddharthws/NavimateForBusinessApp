package com.biz.navimate.views.custom;

import android.content.Context;
import android.os.Handler;
import android.support.v7.widget.AppCompatEditText;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;

public class NvmEditText    extends     AppCompatEditText
                            implements  TextWatcher,
                                        Runnable {
    // ----------------------- Constants ----------------------- //
    private static final String TAG = "NVM_EDITEXT";

    // Debounce Interval
    private static final int DEBOUNCE_INTERVAL_MS = 500;

    // ----------------------- Interfaces ----------------------- //
    public interface IfaceEditText {
        void onTextChanged(String text);
        void onTextChangedDebounced(String text);
    }
    private IfaceEditText listener = null;
    public void SetListener(IfaceEditText listener) { this.listener = listener; }

    // ----------------------- Globals ----------------------- //
    private Handler handler = new Handler();
    private int debounceMs = DEBOUNCE_INTERVAL_MS;

    // ----------------------- Constructors ----------------------- //
    public NvmEditText(Context context) {
        super(context);
        Init(context, null);
    }

    public NvmEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        Init(context, attrs);
    }

    public NvmEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        Init(context, attrs);
    }

    // ----------------------- Overrides ----------------------- //
    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

    @Override
    public void afterTextChanged(Editable editable) {
        // Remove callbacks
        handler.removeCallbacks(this);

        // Scehdule callback after debounce interval
        handler.postDelayed(this, debounceMs);

        // Trigger immediate listener if any
        if (listener != null) {
            listener.onTextChanged(getText().toString());
        }
    }

    @Override
    public void run() {
        // Trigger listener
        if (listener != null) {
            listener.onTextChangedDebounced(getText().toString());
        }
    }

    // ----------------------- Public APIs ----------------------- //
    // Method to set debounce interval
    public void SetDebounceInterval(int intervalMs) {
        this.debounceMs = intervalMs;
    }

    // ----------------------- Private APIs ----------------------- //
    private void Init(Context ctx, AttributeSet attrs) {
        addTextChangedListener(this);
    }
}
