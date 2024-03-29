package com.biz.navimate.objects;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.net.Uri;
import android.os.Environment;
import android.os.Looper;
import android.view.View;
import android.widget.Toast;

import com.biz.navimate.activities.BaseActivity;
import com.biz.navimate.debug.Dbg;
import com.google.android.gms.maps.model.LatLng;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

/**
 * Created by Siddharth on 27-09-2017.
 */

public class Statics {
    // ----------------------- Constants ----------------------- //
    private static final String TAG = "STATICS";

    // ----------------------- Globals ----------------------- //
    // Screen size
    public static Point SCREEN_SIZE = null;

    // Screen Density
    public static float SCREEN_DENSITY = 0f;

    // Flag to indicate if Db Got Upgraded while app start
    public static boolean bDbUpgraded = false;

    // ----------------------- Public APIs ----------------------- //
    // Check if this thread is UI thread
    public static boolean IsOnUiThread()
    {
        return (Looper.myLooper() == Looper.getMainLooper());
    }

    // Convert Dip into Pixels
    public static int GetPxFromDip(int dip)
    {
        int px = (int) Math.ceil(dip * SCREEN_DENSITY);
        return px;
    }

    public static double round(double in, int places) {
        double factor = Math.pow(10, places);
        return ((double) Math.round(in * factor)) / factor;
    }

    // Get API key from Manifest's Meta data TAG
    public static String GetApiKey(Context context)
    {
        String key = "";

        try
        {
            // Get Application Info
            ApplicationInfo appInfo = context.getPackageManager().getApplicationInfo(
                                        context.getPackageName(),
                                        PackageManager.GET_META_DATA);

            // Get From Meta Data
            if (appInfo.metaData != null)
            {
                key = appInfo.metaData.getString("com.google.android.geo.API_KEY");
            }
        }
        catch (PackageManager.NameNotFoundException e)
        {
            Dbg.error(TAG, "Error while retrieving API Key");
            Dbg.stack(e);
        }

        return key;
    }

    // Position validity check
    public static boolean IsPositionValid(LatLng position)
    {
        if (position != null)
        {
            if ((position.latitude != 0) && (position.longitude != 0))
            {
                return true;
            }
        }

        return false;
    }

    // Distacne Calculation API
    public static double deg2rad(double deg)
    {
        return (deg * Math.PI / 180.0);
    }

    public static double rad2deg(double rad)
    {
        return (rad * 180 / Math.PI);
    }

    public static int GetDistanceBetweenCoordinates(LatLng point1, LatLng point2)
    {
        double lat1 = point1.latitude;
        double lat2 = point2.latitude;
        double lon1 = point1.longitude;
        double lon2 = point2.longitude;

        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;

        int distanceM = (int) (dist * 1609.344);

        return distanceM;
    }

    // API to open playstore link
    public static void OpenPlayStoreLink(BaseActivity parentActivity)
    {
        // Get App's package name
        final String appPackageName = parentActivity.getPackageName();

        try
        {
            // Try opening Play Store link
            parentActivity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
        }
        catch (android.content.ActivityNotFoundException anfe)
        {
            // If play store is not available, open browser
            parentActivity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + appPackageName)));
        }
    }

    // File related APIs
    public static File CreatePicture(Context context) {
        // Create a jpg picture with UUID Name
        return CreateFile(context, UUID.randomUUID().toString() + ".jpg", Environment.DIRECTORY_PICTURES);
    }

    public static File CreateDocument(Context context, String ext) {
        // Create a document with UUID Name
        return CreateFile(context, UUID.randomUUID().toString() + "." + ext, Environment.DIRECTORY_DOCUMENTS);
    }

    public static File CreateFile(Context ctx, String nameWithExt, String dir) {
        // Get storage directory file
        File storageDir = ctx.getExternalFilesDir(dir);

        // Create an image file name
        File file = new File(storageDir, nameWithExt);

        return file;
    }

    public static String GetAbsolutePath(Context context, String filename, String directory) {
        File storageDir = context.getExternalFilesDir(directory);
        String absPath = storageDir.getAbsolutePath() + "/" + filename;
        return absPath;
    }

    public static String GetFileFromView(View view) {
        Context context = view.getContext();

        // Create Bitmap
        Bitmap bitmap = Bitmap.createBitmap(    view.getWidth(),
                                                view.getHeight(),
                                                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawColor(Color.WHITE);
        view.layout(view.getLeft(),
                    view.getTop(),
                    view.getRight(),
                    view.getBottom());
        view.draw(canvas);

        // Save bitmap to file
        File file = CreatePicture(context);
        if (file == null) {
            Dbg.Toast(context, "Could not create Image... ", Toast.LENGTH_SHORT);
            return null;
        }
        FileOutputStream fOut = null;
        try {
            fOut = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
            fOut.flush();
        } catch (IOException e) {
            Dbg.error(TAG, "Error while saving to compressed file");
            Dbg.stack(e);
        }
        finally {
            try {
                fOut.close();
            } catch (IOException e) {
                Dbg.error(TAG, "Error while closing file output stream");
                Dbg.stack(e);
            }

            bitmap.recycle();
        }

        // Scale File
         String compressedFile = ScaleImageFile(context, file.getAbsolutePath());

        return compressedFile;
    }

    public static String ScaleImageFile(Context context, String path) {
        File dir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File origFile = new File(path);
        long maxSizeB = 512 * 1024;

        // Validate File
        if (!origFile.exists()) {
            return null;
        }

        // Get current File size
        long origFileSize = origFile.length();

        // Find out required scaling factor to bring size in range
        int inSampleSize = 1;
        long scaledFileSize = origFileSize;
        for (int i = 1; scaledFileSize > maxSizeB; i++) {
            inSampleSize = (int) Math.pow(2, i);
            scaledFileSize = origFileSize / (inSampleSize * inSampleSize);
        }

        // Decode bitmap from file usign inSampleSize
        BitmapFactory.Options bmpOptions = new BitmapFactory.Options();
        bmpOptions.inSampleSize = inSampleSize;
        Bitmap bitmap = BitmapFactory.decodeFile(path, bmpOptions);

        // Save contents to new file
        String compressedName = "FLL_" + origFile.getName();
        File compressedFile = new File(dir, compressedName);
        FileOutputStream fOut = null;
        try {
            fOut = new FileOutputStream(compressedFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, fOut);
            fOut.flush();
            origFile.delete();
        } catch (IOException e) {
            Dbg.error(TAG, "Error while saving to compressed file");
            Dbg.stack(e);
        }
        finally {
            try {
                fOut.close();
            } catch (IOException e) {
                Dbg.error(TAG, "Error while closing file output stream");
                Dbg.stack(e);
            }

            bitmap.recycle();
        }

        return compressedName;
    }

    public static File CopyFileFromUri(Context context, File file, Uri uri) {
        try {
            // Get input stream
            InputStream inputStream = context.getContentResolver().openInputStream(uri);

            // Create output stream for file
            OutputStream output = new FileOutputStream(file);

            // Copy bytes into output stream
            byte[] buffer = new byte[4 * 1024]; // or other buffer size
            int read;
            while ((read = inputStream.read(buffer)) != -1) {
                output.write(buffer, 0, read);
            }

            output.flush();
            output.close();
        } catch (IOException e) {
            Dbg.error(TAG, "Exception while creating file form input stream");
        }

        return file;
    }

    public static String GetFileExt(String absPath) {
        if (absPath.indexOf("?") > -1) {
            absPath = absPath.substring(0, absPath.indexOf("?"));
        }
        if (absPath.lastIndexOf(".") == -1) {
            return null;
        } else {
            String ext = absPath.substring(absPath.lastIndexOf(".") + 1);
            if (ext.indexOf("%") > -1) {
                ext = ext.substring(0, ext.indexOf("%"));
            }
            if (ext.indexOf("/") > -1) {
                ext = ext.substring(0, ext.indexOf("/"));
            }
            return ext.toLowerCase();
        }
    }

    public static boolean isFileExist(Context ctx, String name, String dir) {
        // get absolute path of file
        String absPath = GetAbsolutePath(ctx, name, dir);

        // create file object
        File file = new File(absPath);

        // Check file existence
        return file.exists();
    }

    // Formatting helper APIs
    public static String GetFormattedDate(long timeMs, String format)
    {
        Date date = new Date(timeMs);
        SimpleDateFormat formatter = new SimpleDateFormat(format);
        return formatter.format(date);
    }

    public static boolean IsOlderThanToday(long timestamp)
    {
        if (timestamp > System.currentTimeMillis())
        {
            return false;
        }

        Calendar cToday = Calendar.getInstance();
        Calendar cOlder = Calendar.getInstance();
        cOlder.setTimeInMillis(timestamp);

        if (cOlder.get(Calendar.YEAR) < cToday.get(Calendar.YEAR))
        {
            return true;
        }
        else if (cOlder.get(Calendar.DAY_OF_YEAR) < cToday.get(Calendar.DAY_OF_YEAR))
        {
            return true;
        }
        else
        {
            return false;
        }
    }
}
