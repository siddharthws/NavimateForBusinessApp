package com.biz.navimate.views;

import android.content.Context;
import android.content.res.ColorStateList;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatCheckBox;
import android.util.AttributeSet;

import com.biz.navimate.R;

/**
 * Created by Siddharth on 06-11-2017.
 */

public class CbCustom extends AppCompatCheckBox {
    public CbCustom(Context context) {
        super(context);
        Init();
    }

    public CbCustom(Context context, AttributeSet attrs) {
        super(context, attrs);
        Init();
    }

    public CbCustom(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        Init();
    }

    private void Init() {
        ColorStateList colorStateList = new ColorStateList(
                new int[][]{new int[]{-android.R.attr.state_enabled}, //disabled
                            new int[]{android.R.attr.state_enabled} //enabled
                },
                new int[] { ContextCompat.getColor(getContext(), R.color.grey_5),
                            ContextCompat.getColor(getContext(), R.color.colorPrimary)
                }
        );
        setSupportButtonTintList(colorStateList);
    }
}
