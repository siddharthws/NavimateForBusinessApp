package com.biz.navimate.dialogs;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.biz.navimate.R;
import com.biz.navimate.lists.GenericListAdapter;
import com.biz.navimate.misc.Preferences;
import com.biz.navimate.objects.Dialog;
import com.biz.navimate.objects.ListItem;
import com.biz.navimate.viewholders.DialogHolder;
import com.biz.navimate.views.RlDialog;
import com.google.android.gms.maps.GoogleMap;

/**
 * Created by Siddharth on 03-12-2017.
 */

public class MapSettingsDialog  extends     BaseDialog
                                implements  AdapterView.OnItemClickListener,
                                            View.OnClickListener {
    // ----------------------- Constants ----------------------- //
    private static final String TAG = "MAP_SETTINGS_DIALOG";

    // ----------------------- Classes ---------------------------//
    // ----------------------- Interfaces ----------------------- //
    // ----------------------- Globals ----------------------- //
    private DialogHolder.MapSettings ui = null;
    private GenericListAdapter listAdapter = null;

    // ----------------------- Constructor ----------------------- //
    public MapSettingsDialog(Context context)
    {
        super(context);
    }


    // ----------------------- Overrides ----------------------- //
    @Override
    protected void SetViewHolder(LayoutInflater inflater, ViewGroup container)
    {
        // Init Holder
        ui = new DialogHolder.MapSettings();
        holder = ui;

        // Inflate View
        ui.dialogView = inflater.inflate(R.layout.dialog_map_settings, container);

        // Find Views
        ui.lvMapType = (ListView)   ui.dialogView.findViewById(R.id.lv_map_type);
        ui.llTraffic = (LinearLayout) ui.dialogView.findViewById(R.id.ll_settings_traffic);
        ui.ivTrafficCheck = (ImageView) ui.dialogView.findViewById(R.id.iv_traffic_check);
        ui.btnDone       = (Button)     ui.dialogView.findViewById(R.id.btn_done);
    }

    @Override
    protected void SetContentView()
    {
        // Init list adapter and add data
        listAdapter = new GenericListAdapter(context, ui.lvMapType);
        InitTypeList();

        // Init Traffic overlay check
        InitTrafficCheck();

        // Set Listeners
        ui.btnDone.setOnClickListener(this);
        ui.llTraffic.setOnClickListener(this);
        ui.lvMapType.setOnItemClickListener(this);
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.btn_done: {
                // Hide this dialog box
                RlDialog.Hide();
                break;
            }
            case R.id.ll_settings_traffic: {
                // Get current setting
                boolean bTrafficOverlay = Preferences.GetMapTrafficOverlay();

                // Toggle setting
                Preferences.SetMapTrafficOverlay(context, !bTrafficOverlay);

                // Re-Init UI
                InitTrafficCheck();

                // Call Listener
                Dialog.MapSettings dialogData = (Dialog.MapSettings) data;
                if (dialogData.listener != null) {
                    dialogData.listener.onSettingsUpdated();
                }
                break;
            }
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        // Get clicked Item
        ListItem.Generic clickedItem = (ListItem.Generic) listAdapter.getItem(position);

        // Set Preferences setting
        Preferences.SetMapType(context, clickedItem.id);

        // Re-Init UI
        InitTypeList();

        // Call Listener
        Dialog.MapSettings dialogData = (Dialog.MapSettings) data;
        if (dialogData.listener != null) {
            dialogData.listener.onSettingsUpdated();
        }
    }
    // ----------------------- Public APIs ----------------------- //

    // ----------------------- Private APIs ----------------------- //
    private void InitTypeList() {
        // Clear list adapter
        listAdapter.Clear();

        // Create List Item objects for map type
        ListItem.Generic liNormal = new ListItem.Generic(GoogleMap.MAP_TYPE_NORMAL, "Normal View", 0, 0, 0);
        ListItem.Generic liSatellite = new ListItem.Generic(GoogleMap.MAP_TYPE_HYBRID, "Satellite View", 0, 0, 0);
        ListItem.Generic liTerrain = new ListItem.Generic(GoogleMap.MAP_TYPE_TERRAIN, "Terrain View", 0, 0, 0);

        // Check currently selected item
        int mapType = Preferences.GetMapType();
        if (mapType == GoogleMap.MAP_TYPE_NORMAL) {
            liNormal.endImageId = R.mipmap.icon_tick_grey;
        } else if (mapType == GoogleMap.MAP_TYPE_HYBRID) {
            liSatellite.endImageId = R.mipmap.icon_tick_grey;
        } else if (mapType == GoogleMap.MAP_TYPE_TERRAIN) {
            liTerrain.endImageId = R.mipmap.icon_tick_grey;
        }

        // Add list items to adapter
        listAdapter.Add(liNormal);
        listAdapter.Add(liSatellite);
        listAdapter.Add(liTerrain);
    }

    private void InitTrafficCheck() {
        // Get Traffic check settting
        boolean bTrafficOverlay = Preferences.GetMapTrafficOverlay();

        // Set check image
        if (bTrafficOverlay) {
            ui.ivTrafficCheck.setImageDrawable(ContextCompat.getDrawable(context, R.mipmap.icon_tick_grey));
        } else {
            ui.ivTrafficCheck.setImageDrawable(ContextCompat.getDrawable(context, R.mipmap.icon_cross_grey));
        }
    }
}
