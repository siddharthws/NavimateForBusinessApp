package com.biz.navimate.views;

import android.animation.Animator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

import com.biz.navimate.R;
import com.biz.navimate.debug.Dbg;
import com.biz.navimate.dialogs.AddTaskDialog;
import com.biz.navimate.dialogs.AlertDialog;
import com.biz.navimate.dialogs.BaseDialog;
import com.biz.navimate.dialogs.ConfirmDialog;
import com.biz.navimate.dialogs.LeadDialog;
import com.biz.navimate.dialogs.MapSettingsDialog;
import com.biz.navimate.dialogs.ProductViewerDialog;
import com.biz.navimate.dialogs.ProgressDialog;
import com.biz.navimate.dialogs.RouteBuilderDialog;
import com.biz.navimate.dialogs.SubmitFormDialog;
import com.biz.navimate.dialogs.TaskInfoDialog;
import com.biz.navimate.dialogs.WaitingDialog;
import com.biz.navimate.interpolators.PowerInterpolator;
import com.biz.navimate.misc.AnimHelper;
import com.biz.navimate.objects.Anim;
import com.biz.navimate.objects.Dialog;
import com.biz.navimate.objects.Statics;

/**
 * Created by Siddharth on 27-09-2017.
 */

public class RlDialog   extends     RelativeLayout
                        implements  View.OnClickListener,
                                    Animator.AnimatorListener
{
    // ----------------------- Constants ----------------------- //
    public static final String TAG = "RL_DIALOG";

    // ----------------------- Classes ---------------------------//
    // ----------------------- Interfaces ----------------------- //
    // ----------------------- Globals ----------------------- //
    // UI
    private static RlDialog     rlDialogView = null;
    private View vwCover             = null;
    private RelativeLayout rlDialogContainer = null;

    // Current Data
    private Dialog.Base         currentData     = null;
    private BaseDialog currentDialog   = null;

    // Caching variables
    private boolean          bAnimating      = false;
    private boolean          bCacheUpdated   = false;
    private Dialog.Base      cacheData       = null;

    // Misc
    private AnimHelper animHelper = null;

    // ----------------------- Constructor ----------------------- //

    public RlDialog(Context context)
    {
        super(context);
        InitView(context);
    }

    public RlDialog(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        InitView(context);
    }

    public RlDialog(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        InitView(context);
    }

    // ----------------------- Overrides ----------------------- //

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.vw_cover:
            {
                if ((currentData != null) && (currentData.bCancellable))
                {
                    HideDialogBox();
                }
                break;
            }
        }
    }

    // Animator Overrides
    @Override
    public void onAnimationStart(Animator animation)
    {
        bAnimating = true;
    }

    @Override
    public void onAnimationEnd(Animator animation)
    {
        bAnimating = false;

        // Get chiild count
        int childCount = rlDialogContainer.getChildCount();

        if (childCount == 1)
        {
            if (currentData == null)
            {
                rlDialogContainer.removeAllViews();
            }
        }
        else if (childCount == 2)
        {
            // Remove view at 0 index
            rlDialogContainer.removeViewAt(0);
        }
        else if (childCount == 0)
        {
            Dbg.error(TAG, "Illegal State. Child count = 0");
        }

        // Check if a cache update came while animation was happening
        if (bCacheUpdated)
        {
            // Hide / Show again based on data
            if (cacheData == null)
            {
                HideDialogBox();
            }
            else
            {
                ShowDialogBox(cacheData);
            }

            // Reset Cache
            bCacheUpdated = false;
            cacheData = null;
        }
    }

    @Override
    public void onAnimationCancel(Animator animation)
    {
        bAnimating = false;
    }

    @Override
    public void onAnimationRepeat(Animator animation) {}

    // ----------------------- Public APIs ----------------------- //
    // Dialog Usage APIs
    public static void Set(RlDialog instance)
    {
        rlDialogView = instance;
    }

    public static void Show(Dialog.Base dialog)
    {
        if (!Statics.IsOnUiThread())
        {
            Dbg.error(TAG, "Attempt to trigger dialog on non-UI thread.");
            return;
        }

        if (rlDialogView == null)
        {
            Dbg.error(TAG, "Cannot show dialog on a null view");
            return;
        }

        rlDialogView.ShowDialogBox(dialog);
    }

    public static void Hide()
    {
        if (!Statics.IsOnUiThread())
        {
            Dbg.error(TAG, "Attempt to trigger dialog on non-UI thread.");
            return;
        }

        if (rlDialogView == null)
        {
            Dbg.error(TAG, "Cannot hide dialog on a null view");
            return;
        }

        rlDialogView.HideDialogBox();
    }

    public static boolean IsShowing()
    {
        if (rlDialogView != null)
        {
            return rlDialogView.IsDialogShowing();
        }

        return false;
    }

    // ----------------------- Private APIs ----------------------- //
    private void InitView(Context context)
    {
        // Inflate Layout as per reverse attribute
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = inflater.inflate(R.layout.custom_rl_dialog_view, this, true);

        // Init UI
        vwCover             = (View)            dialogView.findViewById(R.id.vw_cover);
        rlDialogContainer = (RelativeLayout)    dialogView.findViewById(R.id.rl_dialog_container);

        // Set Listeners
        vwCover.setOnClickListener(this);

        // Init Animation Helper
        animHelper = new AnimHelper(getContext());
    }


    private void ShowDialogBox(final Dialog.Base newDialogData)
    {
        if (bAnimating)
        {
            // Cache data if an animation on going
            cacheData = newDialogData;
            bCacheUpdated = true;
            return;
        }

        // Get child count
        int childCount = rlDialogContainer.getChildCount();

        // Check if no dialog is displayed currently.
        if (childCount == 0)
        {
            // Update Cache
            this.currentData = newDialogData;

            // Initialize Dialog
            InitDialog();

            if (currentDialog != null)
            {
                // Set dialog view in container
                currentDialog.SetView(rlDialogContainer, currentData);
            }
            else
            {
                return;
            }

            if (rlDialogContainer.getChildCount() == 1)
            {
                // Fade in the cover
                animHelper.Animate(new Anim.Base(Anim.TYPE_FADE_IN, vwCover, new PowerInterpolator(false, 1), null));


                // Slide the dialog itno focus
                animHelper.Animate(new Anim.Slide(rlDialogContainer.getChildAt(0), new PowerInterpolator(false, 3), Statics.SCREEN_SIZE.x, 0.0f, Anim.Slide.SLIDE_AXIS_X, this));
            }
            else
            {
                Dbg.error(TAG, "No children in Dialog cotnainer");
                return;
            }
        }
        // Check if a dialog is already displayed
        else if (childCount == 1)
        {
            // Check if the displayed dialog as same
            if (currentData.type == newDialogData.type)
            {
                // Update Cache
                this.currentData = newDialogData;

                // Only update the UI without animations
                currentDialog.SetView(rlDialogContainer, currentData);
            }
            else
            {
                // Update Cache
                this.currentData = newDialogData;

                // Re-initialize dialog
                InitDialog();

                // Get Dialog View
                if (currentDialog != null)
                {
                    currentDialog.SetView(rlDialogContainer, currentData);
                }
                else
                {
                    Dbg.error(TAG, "Null dialog returned. No view displayed.");
                    return;
                }

                if (rlDialogContainer.getChildCount() == 2)
                {
                    // Play in and out animations on both dialogs so that new dialog comes in focus whle old dialog slides away.
                    animHelper.Animate(new Anim.Slide(rlDialogContainer.getChildAt(1), new PowerInterpolator(false, 3), Statics.SCREEN_SIZE.x, 0.0f, Anim.Slide.SLIDE_AXIS_X, null));
                    animHelper.Animate(new Anim.Slide(rlDialogContainer.getChildAt(0), new PowerInterpolator(false, 3), 0.0f, -1 * Statics.SCREEN_SIZE.x, Anim.Slide.SLIDE_AXIS_X, this));
                }
                else
                {
                    Dbg.error(TAG, "Illegal child count in container : " + childCount + " Cannot play aniamtions");
                    return;
                }
            }
        }
    }

    private void HideDialogBox()
    {
        if (bAnimating)
        {
            // Update Cache if an animation on going
            // Changes will be picked up when animation ends
            cacheData = null;
            bCacheUpdated = true;
            return;
        }

        // Get number of dialogs in container
        int childCount = rlDialogContainer.getChildCount();

        if (childCount == 0)
        {
            // Ignore
        }
        else if (childCount == 1)
        {
            // Update Current Data
            currentData = null;
            currentDialog = null;

            // Fade out cover
            animHelper.Animate(new Anim.Base(Anim.TYPE_FADE_OUT, vwCover, new PowerInterpolator(false, 1), null));

            // Start Slide Animation
            animHelper.Animate(new Anim.Slide(rlDialogContainer.getChildAt(0), new PowerInterpolator(false, 3), 0.0f, -1.0f * Statics.SCREEN_SIZE.x, Anim.Slide.SLIDE_AXIS_X, this));
        }
    }

    private boolean IsDialogShowing()
    {
        return (rlDialogContainer.getChildCount() != 0);
    }

    private void InitDialog()
    {
        // Init dialog view based on data type
        switch (currentData.type)
        {
            case Dialog.TYPE_ALERT : {
                currentDialog = new AlertDialog(getContext());
                break;
            }
            case Dialog.TYPE_CONFIRM : {
                currentDialog = new ConfirmDialog(getContext());
                break;
            }
            case Dialog.TYPE_WAITING : {
                currentDialog = new WaitingDialog(getContext());
                break;
            }
            case Dialog.TYPE_PROGRESS : {
                currentDialog = new ProgressDialog(getContext());
                break;
            }
            case Dialog.TYPE_SUBMIT_FORM : {
                currentDialog = new SubmitFormDialog(getContext());
                break;
            }
            case Dialog.TYPE_TASK_INFO : {
                currentDialog = new TaskInfoDialog(getContext());
                break;
            }
            case Dialog.TYPE_ROUTE_BUILDER : {
                currentDialog = new RouteBuilderDialog(getContext());
                break;
            }
            case Dialog.TYPE_MAP_SETTINGS : {
                currentDialog = new MapSettingsDialog(getContext());
                break;
            }

            case Dialog.TYPE_LEAD : {
                currentDialog = new LeadDialog(getContext());
                break;
            }

            case Dialog.TYPE_PRODUCT_VIEWER : {
                currentDialog = new ProductViewerDialog(getContext());
                break;
            }

            case Dialog.TYPE_ADD_TASK : {
                currentDialog = new AddTaskDialog(getContext());
                break;
            }
        }
    }
}
