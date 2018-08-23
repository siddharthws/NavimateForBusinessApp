package com.biz.navimate.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.biz.navimate.R;

/**
 * Created by Siddharth on 27-09-2017.
 */

public class RlDialogTitle extends RelativeLayout {
    // ----------------------- Constants ----------------------- //
    public static final String TAG = "RL_DIALOG_TITLE";

    // ----------------------- Classes ---------------------------//
    // ----------------------- Interfaces ----------------------- //
    // ----------------------- Globals ----------------------- //
    // UI
    private View vwSeparator = null;
    private ImageView ivIcon = null;
    private TextView tvText = null;

    // ----------------------- Constructor ----------------------- //

    public RlDialogTitle(Context context)
    {
        super(context);
        InitView(context, null);
    }

    public RlDialogTitle(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        TypedArray attrArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.DialogTitle, 0, 0);
        InitView(context, attrArray);
    }

    public RlDialogTitle(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        TypedArray attrArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.DialogTitle, 0, 0);
        InitView(context, attrArray);
    }

    // ----------------------- Overrides ----------------------- //
    // ----------------------- Public APIs ----------------------- //
    // ----------------------- Private APIs ----------------------- //
    private void InitView(Context context, TypedArray attrs)
    {
        // Inflate Layout
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.custom_rl_dialog_title, this, true);

        // Init UI
        vwSeparator = view.findViewById(R.id.vw_dialog_title_separator);
        ivIcon = (ImageView) view.findViewById(R.id.iv_title_icon);
        tvText = (TextView)  view.findViewById(R.id.tv_title_text);

        // Set gradient to separator
        vwSeparator.setBackground(new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, new int[]{0, android.R.color.black, 0}));

        // Set attributes
        if (attrs != null)
        {
            Drawable src = attrs.getDrawable(R.styleable.DialogTitle_image);
            String text = attrs.getString(R.styleable.DialogTitle_text);

            // Set Icon
            if (src != null)
            {
                ivIcon.setImageDrawable(src);
            }

            // Set Text
            tvText.setText(text);
        }
    }
}
