package com.biz.navimate.activities;

import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;

import com.biz.navimate.R;
import com.biz.navimate.constants.Constants;
import com.biz.navimate.database.DbHelper;
import com.biz.navimate.debug.Dbg;
import com.biz.navimate.lists.GenericListAdapter;
import com.biz.navimate.objects.Data;
import com.biz.navimate.objects.Dialog;
import com.biz.navimate.objects.Form;
import com.biz.navimate.objects.Lead;
import com.biz.navimate.objects.ListItem;
import com.biz.navimate.objects.Statics;
import com.biz.navimate.objects.Task;
import com.biz.navimate.server.SyncFormsTask;
import com.biz.navimate.viewholders.ActivityHolder;
import com.biz.navimate.views.RlDialog;

import java.io.File;
import java.util.concurrent.CopyOnWriteArrayList;

public class ViewPhotoActivity extends BaseActivity {
    // ----------------------- Constants ----------------------- //
    private static final String TAG = "VIEW_PHOTO_ACTIVITY";

    // ----------------------- Interfaces ----------------------- //
    // ----------------------- Globals ----------------------- //
    private ActivityHolder.ViewPhoto  ui                    = null;

    // ----------------------- Constructor ----------------------- //
    // ----------------------- Overrides ----------------------- //

    @Override
    protected void InflateLayout() {
        // Set content view
        setContentView(R.layout.activity_view_photo);
    }

    @Override
    protected void FindViews() {
        // Init view holder
        ui = new ActivityHolder.ViewPhoto();
        holder = ui;

        // Activity View
        ui.ivImage              = (ImageView)        findViewById(R.id.iv_image);
    }

    @Override
    protected void SetViews() {
        // Extract image name from extra
        String imageName = "";
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            imageName = extras.getString(Constants.Extras.IMAGE_NAME);
        }

        // Add to imageview if file is existing
        if (imageName.length() > 0) {
            String absPath = Statics.GetAbsolutePath(this, imageName);
            Dbg.info(TAG, "Path = " + absPath);
            File imageFile = new File(absPath);
            if (imageFile.exists()) {
                // Set image view to visible
                ui.ivImage.setVisibility(View.VISIBLE);

                // Set bitmap to image view
                ui.ivImage.setImageBitmap(BitmapFactory.decodeFile(absPath));
            }
        }
    }

    // ----------------------- Public APIs ----------------------- //
    public static void Start(BaseActivity parentActivity, String filename)
    {
        Bundle extras = new Bundle();
        extras.putString(Constants.Extras.IMAGE_NAME, filename);
        BaseActivity.Start(parentActivity, ViewPhotoActivity.class, -1, extras, Constants.RequestCodes.INVALID, null);
    }

    // Button Click APIs
    public void ButtonClickBack(View view)
    {
        super.onBackPressed();
    }

    // ----------------------- Private APIs ----------------------- //
}
