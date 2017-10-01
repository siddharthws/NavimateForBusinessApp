package com.biz.navimate.misc;

import android.content.Context;
import android.graphics.Bitmap;

import com.google.maps.android.ui.IconGenerator;

/**
 * Created by Siddharth on 01-10-2017.
 */

public class IconGen {
    private static IconGenerator iconGen = null;

    public static void Init(Context context) {
        iconGen = new IconGenerator(context);
    }

    public static Bitmap GetMarkerIcon(String text) {
        return iconGen.makeIcon(text);
    }
}
