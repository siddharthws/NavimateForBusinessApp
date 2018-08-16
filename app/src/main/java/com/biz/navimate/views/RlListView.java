package com.biz.navimate.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.biz.navimate.R;
import com.biz.navimate.lists.BaseListAdapter;

public class RlListView extends     RelativeLayout
                        implements  View.OnClickListener {
    // ----------------------- Constants ----------------------- //

    public static final String TAG = "RL_LIST_VIEW";

    public static final String MSG_ERROR    = "Error while getting data...";
    public static final String MSG_BLANK    = "Nothing to show...";
    public static final String MSG_WAITING  = "Getting data...";
    // ----------------------- Interfaces ----------------------- //
    public interface LoadMoreListener {
        void onLoadMore();
    }
    private LoadMoreListener loadMoreListener = null;
    public void SetLoadMoreListener(LoadMoreListener listener) {
        this.loadMoreListener = listener;
    }

    // ----------------------- Globals ----------------------- //
    // UI
    private ListView lvList;
    private TvCalibri tvMessage;
    private ProgressBar pbLoader;
    private Button btnLoadMore;

    // List Adapter
    public BaseListAdapter adapter = null;

    // ----------------------- Constructor ----------------------- //
    public RlListView(Context context) {
        super(context);
        InitView(context);
    }

    public RlListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        InitView(context);
    }

    public RlListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        InitView(context);
    }

    // ----------------------- Overrides ----------------------- //
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_load_more:
                if (loadMoreListener != null) {
                    loadMoreListener.onLoadMore();
                }
                break;
        }
    }

    // ----------------------- Public APIs ----------------------- //
    // Method to toggle Load More Visibility
    public void ToggleLoadMore(boolean bVisible) {
        if (bVisible) {
            btnLoadMore.setVisibility(VISIBLE);
        } else {
            btnLoadMore.setVisibility(GONE);
        }
    }

    // Methods to show error / blank / waiting messages
    public void ShowError() {
        lvList.setVisibility(GONE);
        btnLoadMore.setVisibility(GONE);
        pbLoader.setVisibility(GONE);
        tvMessage.setVisibility(VISIBLE);
        tvMessage.setText(MSG_ERROR);
    }

    public void ShowBlank() {
        lvList.setVisibility(GONE);
        btnLoadMore.setVisibility(GONE);
        pbLoader.setVisibility(GONE);
        tvMessage.setVisibility(VISIBLE);
        tvMessage.setText(MSG_BLANK);
    }

    public void ShowWaiting() {
        lvList.setVisibility(GONE);
        btnLoadMore.setVisibility(GONE);
        pbLoader.setVisibility(VISIBLE);
        tvMessage.setVisibility(VISIBLE);
        tvMessage.setText(MSG_WAITING);
    }

    public void ShowList() {
        lvList.setVisibility(VISIBLE);
        pbLoader.setVisibility(GONE);
        tvMessage.setVisibility(GONE);
    }

    // Method to set adapter
    public ListView GetListView() {
        return lvList;
    }
    // ----------------------- Private APIs ----------------------- //
    private void InitView(Context context) {
        // Find Views
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.rl_list_view, this, true);

        // Init UI
        lvList      = (ListView)    view.findViewById(R.id.lv_list);
        tvMessage   = (TvCalibri)   view.findViewById(R.id.tv_list_message);
        pbLoader    = (ProgressBar) view.findViewById(R.id.pb_list);
        btnLoadMore = (Button)      view.findViewById(R.id.btn_load_more);

        // Set Listeners
        btnLoadMore.setOnClickListener(this);
    }
}
