package com.biz.navimate.views.compound;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TimePicker;

import com.biz.navimate.R;
import com.biz.navimate.constants.Constants;
import com.biz.navimate.viewholders.CustomViewHolder;
import com.biz.navimate.views.TvCalibri;
import com.biz.navimate.views.custom.NvmImageButton;

import java.util.Calendar;

public class DateEditorView     extends     LinearLayout
                                implements  View.OnClickListener,
                                            DatePickerDialog.OnDateSetListener,
                                            TimePickerDialog.OnTimeSetListener {
    // ----------------------- Constants ----------------------- //
    private static final String TAG = "DATE_EDITOR_VIEW";

    // ----------------------- Interfaces ----------------------- //
    public interface IfaceDateEditor {
        void onDateChanged(Calendar cal);
    }
    private IfaceDateEditor listener = null;
    public void SetListener(IfaceDateEditor listener)
    {
        this.listener = listener;
    }

    // ----------------------- Globals ----------------------- //
    private Context ctx = null;
    private CustomViewHolder.DateEditor ui = new CustomViewHolder.DateEditor();
    private boolean bEditable = true;
    private Calendar cal = null;
    private DatePickerDialog datePickerDialog = null;
    private TimePickerDialog timePickerDialog = null;

    // ----------------------- Constructors ----------------------- //
    public DateEditorView(Context context) {
        super(context);
        Init(context, null);
    }

    public DateEditorView(Context context, AttributeSet attrs) {
        super(context, attrs);
        Init(context, attrs);
    }

    public DateEditorView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        Init(context, attrs);
    }

    // ----------------------- Overrides ----------------------- //
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ib_clear:
                // Set the current date to null
                Set(null);
                break;
            case R.id.ib_edit:
                // Show time picker dialog
                ShowDatePicker();
                break;
        }
    }

    private int tempYear, tempMonth, tempDay;
    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
        // Update the cached date
        tempYear    = year;
        tempMonth   = month;
        tempDay     = dayOfMonth;

        // Show time picker dialog
        ShowTimePicker();
    }

    @Override
    public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {
        // Update the cached date
        if (cal == null) { cal = Calendar.getInstance(); }
        cal.set(Calendar.YEAR, tempYear);
        cal.set(Calendar.MONTH, tempMonth);
        cal.set(Calendar.DAY_OF_MONTH, tempDay);
        cal.set(Calendar.HOUR_OF_DAY, hourOfDay);
        cal.set(Calendar.MINUTE, minute);

        // Update Text View
        RefreshText();

        // Update Clear Icon visibility
        RefreshClear();

        // Trigger listener
        if (listener != null) {
            listener.onDateChanged(cal);
        }
    }

    // ----------------------- Public APIs ----------------------- //
    // Methods to set and get date
    public void Set(Calendar newCal) {
        // Set cache
        this.cal = newCal;

        // Update Text
        RefreshText();

        // Update Clear Icon visibility
        RefreshClear();
    }

    public Calendar Get() {
        return cal;
    }

    // method to set the view as read only or in editing mode
    public void SetEditable(boolean bEditable) {
        this.bEditable = bEditable;

        // Show / Hide editing buttons
        if (bEditable) {
            ui.ibEdit.setVisibility(VISIBLE);
            ui.ibClear.setVisibility(VISIBLE);
        } else {
            ui.ibEdit.setVisibility(GONE);
            ui.ibClear.setVisibility(GONE);
        }
    }

    public boolean IsEditable() {
        return bEditable;
    }

    // ----------------------- Private APIs ----------------------- //
    // Method to refresh the text view based on selected date
    private void RefreshText() {
        if (cal == null) {
            // Set blank text
            ui.tvText.setText("Date not set...");
        } else {
            // Format date and set
            ui.tvText.setText(Constants.Formatters.DATE_LONG.format(cal.getTime()));
        }
    }

    private void RefreshClear() {
        if (cal == null) {
            ui.ibClear.setVisibility(GONE);
        } else {
            ui.ibClear.setVisibility(VISIBLE);
        }
    }

    // Methods to launch picker dialogs
    private void ShowDatePicker() {
        // Get time to init the dialog with
        Calendar calCache = cal;
        if (calCache == null) { calCache = Calendar.getInstance(); }

        // Init Dialog
        datePickerDialog = new DatePickerDialog(ctx, R.style.DialogTheme, this,
                                                calCache.get(Calendar.YEAR),
                                                calCache.get(Calendar.MONTH),
                                                calCache.get(Calendar.DAY_OF_MONTH));

        // Show Dialog
        datePickerDialog.show();

    }

    private void ShowTimePicker() {
        // Get time to init the dialog with
        Calendar calCache = cal;
        if (calCache == null) { calCache = Calendar.getInstance(); }

        // Init Dialog
        timePickerDialog = new TimePickerDialog(ctx, R.style.DialogTheme, this,
                                                calCache.get(Calendar.HOUR_OF_DAY),
                                                calCache.get(Calendar.MINUTE),
                                                false);

        // Show Dialog
        timePickerDialog.show();
    }

    // Method to initialize view
    private void Init(Context ctx, AttributeSet attrs) {
        this.ctx = ctx;

        // Inflate Layout
        LayoutInflater inflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.custom_date_editor, this, true);

        // Set UI properties
        SetUiAttributes();

        // Init UI
        ui.tvText   = (TvCalibri)       findViewById(R.id.tv_text);
        ui.ibClear  = (NvmImageButton)  findViewById(R.id.ib_clear);
        ui.ibEdit   = (NvmImageButton)  findViewById(R.id.ib_edit);

        // Set listeners
        ui.ibClear.setOnClickListener(this);
        ui.ibEdit.setOnClickListener(this);
    }

    // Method to set any extra attributes
    private void SetUiAttributes() {
        // Set Layout Params
        LinearLayout.LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER_VERTICAL;
        setLayoutParams(params);

        // Set orientation
        setOrientation(LinearLayout.HORIZONTAL);
    }
}
