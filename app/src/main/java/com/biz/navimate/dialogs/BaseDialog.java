package com.biz.navimate.dialogs;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.biz.navimate.debug.Dbg;
import com.biz.navimate.objects.Dialog;
import com.biz.navimate.viewholders.DialogHolder;

/**
 * Created by Siddharth on 27-09-2017.
 */

public abstract class BaseDialog {
    // ----------------------- Constants ----------------------- //
    private static final String TAG = "BASE_DIALOG";

    // ----------------------- Classes ---------------------------//
    // ----------------------- Interfaces ----------------------- //
    // ----------------------- Globals ----------------------- //
    protected Context               context = null;
    protected Dialog.Base           data    = null;
    protected DialogHolder.Base     holder  = null;

    // ----------------------- Constructor ----------------------- //
    public BaseDialog(Context context)
    {
        this.context = context;
    }

    // ----------------------- Abstracts ----------------------- //
    protected abstract void SetViewHolder(LayoutInflater inflater, ViewGroup container);
    protected abstract void SetContentView();

    public void SetView(ViewGroup container, Dialog.Base data)
    {
        // Check if view initialization is required
        if (holder == null)
        {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            // Init Content View
            SetViewHolder(inflater, container);

            // Validate Holder
            if ((holder == null) || (holder.dialogView == null))
            {
                Dbg.error(TAG, "Cannot initialize View Holder");
                return;
            }
        }

        // Update Cache
        this.data = data;

        // Update Content View
        SetContentView();
    }

    // ----------------------- Public APIs ----------------------- //
    // ----------------------- Private APIs ----------------------- //
}
