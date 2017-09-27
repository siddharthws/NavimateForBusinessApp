package com.biz.navimate.misc;

import android.content.Context;
import android.graphics.Typeface;

/**
 * Created by Siddharth on 27-09-2017.
 */

public class TypefaceHelper {
    // APi to get calibri typeface
    public static Typeface GetCalibri (Context context) {
        return Typeface.createFromAsset(context.getAssets(), "fonts/calibri.otf");
    }
}
