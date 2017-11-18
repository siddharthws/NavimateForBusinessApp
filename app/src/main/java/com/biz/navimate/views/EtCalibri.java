package com.biz.navimate.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatEditText;
import android.util.AttributeSet;

import com.biz.navimate.R;
import com.biz.navimate.misc.TypefaceHelper;

/**
 * Created by Siddharth on 27-09-2017.
 */

public class EtCalibri extends AppCompatEditText {
    // ----------------------- Constructors ----------------------- //
    public EtCalibri(Context context)
    {
        super(context);
        InitView(context, null);
    }

    public EtCalibri(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        TypedArray attrArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.Text, 0, 0);
        InitView(context, attrArray);
    }

    public EtCalibri(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        TypedArray attrArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.Text, 0, 0);
        InitView(context, attrArray);
    }

    // ----------------------- Private APIs ----------------------- //
    // ----------------------- Private APIs ----------------------- //
    private void InitView(Context context, TypedArray attrs)
    {
        Typeface calibri = TypefaceHelper.GetCalibri(getContext());

        if (calibri == null)
        {
            calibri = Typeface.createFromAsset(getContext().getAssets(), "fonts/calibri.otf");
        }

        // Set font based on style properties
        if (attrs.getBoolean(R.styleable.Text_bold, false) && (attrs.getBoolean(R.styleable.Text_italic, false)))
        {
            setTypeface(calibri, Typeface.BOLD_ITALIC);
        }
        else if (attrs.getBoolean(R.styleable.Text_bold, false))
        {
            setTypeface(calibri, Typeface.BOLD);
        }
        else if (attrs.getBoolean(R.styleable.Text_italic, false))
        {
            setTypeface(calibri, Typeface.ITALIC);
        }
        else
        {
            setTypeface(calibri);
        }

        setTextColor(ContextCompat.getColor(getContext(), android.R.color.black));
    }
}
