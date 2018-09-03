package com.biz.navimate.views.custom;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.view.ViewGroup;

import com.biz.navimate.R;

public class NvmCardView extends CardView {
    // ----------------------- Constants ----------------------- //
    private static final String TAG = "NVM_CARD_VIEW";

    // ----------------------- Interfaces ----------------------- //
    // ----------------------- Globals ----------------------- //
    // ----------------------- Constructors ----------------------- //
    public NvmCardView(Context context) {
        super(context);
        Init(context, null);
    }

    public NvmCardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        Init(context, attrs);
    }

    public NvmCardView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        Init(context, attrs);
    }

    // ----------------------- Overrides ----------------------- //
    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();

        // Set margins to show shadow
        int mXs = (int) getResources().getDimension(R.dimen.m_xs);
        ViewGroup.MarginLayoutParams lp = (MarginLayoutParams) getLayoutParams();
        lp.setMargins(lp.leftMargin, lp.topMargin, lp.rightMargin + mXs, lp.bottomMargin + mXs);
        setLayoutParams(lp);
    }

    // ----------------------- Public APIs ----------------------- //
    // ----------------------- Private APIs ----------------------- //
    // Method to initialize view
    private void Init(Context ctx, AttributeSet a) {
        // Set content padding & radius
        int mXs = (int) getResources().getDimension(R.dimen.m_xs);
        setContentPadding(mXs, mXs, mXs, mXs);
        setRadius(mXs);
    }
}
