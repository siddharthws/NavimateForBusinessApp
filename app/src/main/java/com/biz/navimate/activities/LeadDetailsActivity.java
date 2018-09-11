package com.biz.navimate.activities;

import com.biz.navimate.R;
import com.biz.navimate.constants.Constants;
import com.biz.navimate.viewholders.ActivityHolder;

public class LeadDetailsActivity    extends     BaseActivity {
    // ----------------------- Constants ----------------------- //
    private static final String TAG = "LEAD_DETAILS_ACTIVITY";

    // ----------------------- Interfaces ----------------------- //
    // ----------------------- Globals ----------------------- //
    protected ActivityHolder.LeadDetails ui = null;

    // ----------------------- Constructor ----------------------- //
    public static void Start(BaseActivity parentActivity) {
        BaseActivity.Start(parentActivity, LeadDetailsActivity.class, -1, null, Constants.RequestCodes.INVALID, null);
    }

    // ----------------------- Abstracts ----------------------- //
    // ----------------------- Overrides ----------------------- //
    @Override
    protected void InflateLayout() {
        // Set content view
        setContentView(R.layout.activity_lead_details);
    }

    @Override
    protected void FindViews() {
        // Init view holder
        ui = new ActivityHolder.LeadDetails();
        holder = ui;
    }

    @Override
    protected void SetViews() {
        // Placeholder to set views
    }

    @Override
    public void onToolbarButtonClick(int id) {
        switch (id) {
            default:
                super.onToolbarButtonClick(id);
        }
    }

    // ----------------------- Public APIs ----------------------- //
    // ----------------------- Private APIs ----------------------- //
}
