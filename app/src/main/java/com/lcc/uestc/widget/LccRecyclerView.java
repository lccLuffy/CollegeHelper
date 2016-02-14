package com.lcc.uestc.widget;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * Created by wanli on 2015/8/26.
 */
public class LccRecyclerView extends RecyclerView{

    private boolean isLoadingData = false;

    private LayoutManager layoutManager;

    private LoadMoreListener loadMoreListener;



    private AdapterWrap adapterWrap;

    private Context context;

    private LinearLayout footerView;

    public LccRecyclerView(Context context) {
        this(context, null);
    }

    public LccRecyclerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LccRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context=context;
        init();
    }

    private void init() {

    }

    @Override
         public void setLayoutManager(LayoutManager layout) {
        super.setLayoutManager(layout);
        layoutManager = layout;
    }

    @Override
    public void onScrollStateChanged(int state)
    {
        if(state != SCROLL_STATE_IDLE || isLoadingData || loadMoreListener == null)
            return;
        int lastVisibleItemPosition = 0;
        if(layoutManager instanceof LinearLayoutManager)
        {
            lastVisibleItemPosition = ((LinearLayoutManager)layoutManager).findLastVisibleItemPosition();
        }

        if(layoutManager.getChildCount() > 0 && lastVisibleItemPosition == layoutManager.getItemCount() - 1)
        {
            isLoadingData = true;
            footerView.setVisibility(View.VISIBLE);
            loadMoreListener.onLoadMore();
        }

    }

    public Adapter getAdapterWrap() {
        return adapterWrap;
    }

    @Override
    public void setAdapter(Adapter adapter) {
        adapterWrap = new AdapterWrap(adapter);
        super.setAdapter(adapterWrap);
    }

    public void setLoadMoreListener(LoadMoreListener loadMoreListener) {
        this.loadMoreListener = loadMoreListener;
    }

    public static interface LoadMoreListener
    {
        void onLoadMore();
    }

    public void loadMoreFinish()
    {
        isLoadingData = false;
        footerView.setVisibility(View.GONE);
    }

    private class AdapterWrap extends Adapter<ViewHolder>
    {
        Adapter adapter;
        private static final int IS_FOOTER = 1;
        private static final int NOT_FOOTER = 0;

        AdapterWrap(Adapter adapter)
        {
            this.adapter = adapter;
            footerView = new LinearLayout(context);
            footerView.setGravity(Gravity.CENTER);//17
            footerView.setLayoutParams(new ViewGroup.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
            footerView.addView(new ProgressBar(context, null, android.R.attr.progressBarStyleSmall));
            TextView text = new TextView(context);
            text.setText("正在加载...");
            footerView.addView(text);
        }

        @Override
        public int getItemViewType(int position) {
            if(position == getItemCount() - 1) {
                return IS_FOOTER;
            }
            return NOT_FOOTER;
        }


        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if(viewType == NOT_FOOTER)
            {
                return adapter.onCreateViewHolder(parent,viewType);
            }
            return new MyViewHolder(footerView);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            if(position != getItemCount() - 1)
            {
                adapter.onBindViewHolder(holder,position);
            }
        }

        @Override
        public int getItemCount() {
            return adapter == null ? 1 : adapter.getItemCount() + 1;
        }
        class MyViewHolder extends ViewHolder{
            public MyViewHolder(View itemView) {
                super(itemView);
            }
        }
    }
}
