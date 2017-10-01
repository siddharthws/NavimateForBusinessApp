package com.biz.navimate.misc;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.v4.content.ContextCompat;

import com.biz.navimate.objects.Statics;
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

    public static Bitmap GetScaledIcon(Context context, int iconId, int dpX, int dpY)
    {
        BitmapDrawable markerDrawable = (BitmapDrawable) ContextCompat.getDrawable(context, iconId);
        Bitmap unscaledMarkerIcon = markerDrawable.getBitmap();
        Bitmap scaleMarkerIcon = Bitmap.createScaledBitmap(unscaledMarkerIcon, Statics.GetPxFromDip(dpX), Statics.GetPxFromDip(dpY), false);
        return scaleMarkerIcon;
    }
}
