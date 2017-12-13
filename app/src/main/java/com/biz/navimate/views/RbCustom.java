package com.biz.navimate.views;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatRadioButton;
import android.util.AttributeSet;

import com.biz.navimate.R;

/**
 * Created by Siddharth on 23-10-2017.
 */

public class RbCustom extends AppCompatRadioButton {
    public RbCustom(Context context) {
        super(context);
        Init();
    }

    public RbCustom(Context context, AttributeSet attrs) {
        super(context, attrs);
        Init();
    }

    public RbCustom(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        Init();
    }

    private void Init() {
        ColorStateList colorStateList = new ColorStateList(
                new int[][]{new int[]{-android.R.attr.state_enabled}, //disabled
                            new int[]{android.R.attr.state_enabled} //enabled
                },
                new int[] { ContextCompat.getColor(getContext(), R.color.darkGrey),
                            ContextCompat.getColor(getContext(), R.color.colorPrimary)
                }
        );
        setSupportButtonTintList(colorStateList);
    }
}
