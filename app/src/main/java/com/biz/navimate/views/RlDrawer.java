package com.biz.navimate.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.biz.navimate.R;
import com.biz.navimate.interpolators.BackInterpolator;
import com.biz.navimate.interpolators.PowerInterpolator;
import com.biz.navimate.lists.DrawerListAdapter;
import com.biz.navimate.misc.AnimHelper;
import com.biz.navimate.objects.Anim;
import com.biz.navimate.objects.ListItem;
import com.biz.navimate.objects.Statics;

/**
 * Created by Siddharth on 17-04-2017.
 */

public class RlDrawer extends     RelativeLayout
                      implements  AdapterView.OnItemClickListener,
                                  View.OnClickListener {
    // ----------------------- Constants ----------------------- //
    public static final String TAG = "RL_DRAWER";

    // Macros for action IDs for drawer items
    public static final int DRAWER_ACTION_NONE          = 0;
    public static final int DRAWER_ACTION_EXIT          = 1;

    // Drawer List Items
    public static final ListItem.Drawer[] DRAWER_ITEMS =
            {
                    new ListItem.Drawer(ListItem.TYPE_DRAWER_ITEM,       DRAWER_ACTION_EXIT,                 "Exit",         R.mipmap.icon_exit_grey)
            };

    // ----------------------- Classes ---------------------------//
    // ----------------------- Interfaces ----------------------- //
    public interface DrawerItemClickListener
    {
        void onDrawerItemClick(int actionId);
    }
    private DrawerItemClickListener listener = null;
    public void SetItemClickListener(DrawerItemClickListener listener)
    {
        this.listener = listener;
    }

    // ----------------------- Globals ----------------------- //
    // UI
    private View vwCover = null;
    private ListView lvDrawer = null;
    private RelativeLayout rlListContainer = null;
    private ImageButton ibBack = null;
    private AnimHelper animHelper = null;

    private DrawerListAdapter listAdapter   = null;

    // ----------------------- Constructor ----------------------- //
    public RlDrawer(Context context)
    {
        super(context);
        InitView(context);
    }

    public RlDrawer(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        InitView(context);
    }

    public RlDrawer(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        InitView(context);
    }

    // ----------------------- Overrides ----------------------- //

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
    {
        // Get Action
        int action = ((ListItem.Drawer) listAdapter.getItem(i)).action;

        // Call listener
        if (listener != null)
        {
            listener.onDrawerItemClick(action);
        }

        // Close Drawer
        Close();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ib_drawer_back : {
                Close();
                break;
            }
        }
    }

    public boolean IsShowing()
    {
        return (vwCover.getVisibility() == VISIBLE);
    }

    // ----------------------- Public APIs ----------------------- //
    public void Open()
    {
        // Slide in List
        int startTrans = -1 * GetListWidth();
        int endTrans = -1 * Statics.GetPxFromDip(50); // Padding Height
        animHelper.Animate(new Anim.Slide(rlListContainer, new BackInterpolator(false), startTrans, endTrans, Anim.Slide.SLIDE_AXIS_X, null));

        // Fade in cover
        animHelper.Animate(new Anim.Base(Anim.TYPE_FADE_IN, vwCover, new PowerInterpolator(false, 1), null));
    }

    public void Close()
    {
        // Don't perform hide if already hidden
        if (vwCover.getVisibility() != VISIBLE)
        {
            return;
        }

        // Fade out cover
        animHelper.Animate(new Anim.Base(Anim.TYPE_FADE_OUT, vwCover, new PowerInterpolator(true, 1), null));

        // Slide out list
        int endTrans = -1 * GetListWidth(); //Drawer Width
        int startTrans = -1 * Statics.GetPxFromDip(50); // Padding Height
        animHelper.Animate(new Anim.Slide(rlListContainer, new BackInterpolator(true), startTrans, endTrans, Anim.Slide.SLIDE_AXIS_X, null));
    }

    // ----------------------- Private APIs ----------------------- //
    private void InitView(Context context)
    {
        // Inflate Layout
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.custom_rl_drawer, this, true);

        // Init UI
        vwCover = (View) view.findViewById(R.id.vw_drawer_cover);
        rlListContainer = (RelativeLayout) view.findViewById(R.id.rl_drawer_list_container);
        lvDrawer = (ListView) view.findViewById(R.id.lv_drawer);
        ibBack = (ImageButton) view.findViewById(R.id.ib_drawer_back);

        // Hide drawer list usign translation property
        rlListContainer.setTranslationX(-1 * GetListWidth());

        // Initialize List adapter & add data
        listAdapter = new DrawerListAdapter(context, lvDrawer);
        for (int i = 0; i < DRAWER_ITEMS.length; i++)
        {
            listAdapter.Add(DRAWER_ITEMS[i]);
        }

        // Init animations
        animHelper = new AnimHelper(getContext());

        // Set listeners
        ibBack.setOnClickListener(this);
        lvDrawer.setOnItemClickListener(this);
        vwCover.setOnTouchListener(new OnTouchListener()
        {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent)
            {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN)
                {
                    Close();
                }
                return false;
            }
        });
    }

    private int GetListWidth()
    {
        if (getContext() != null)
        {
            return getContext().getResources().getDimensionPixelSize(R.dimen.drawer_list_width);
        }

        return 0;
    }
}