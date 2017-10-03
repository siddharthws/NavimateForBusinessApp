package com.biz.navimate.misc;

import android.content.Context;
import android.support.v4.content.ContextCompat;

import com.biz.navimate.R;

/**
 * Created by Siddharth on 03-10-2017.
 */

public class ColorHelper {
    public static int primaryDark = 0;

    public static void Init(Context context) {
        primaryDark = ContextCompat.getColor(context, R.color.colorPrimaryDark);
    }
}
