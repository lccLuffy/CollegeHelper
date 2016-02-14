package com.lcc.uestc.widget.Recycler;

import android.content.Context;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by lcc_luffy on 2016/1/30.
 */
public class StateRecyclerView extends StateLayout {
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;

    private SwipeRefreshLayout.OnRefreshListener onRefreshListener;

    public StateRecyclerView(Context context) {
        super(context);
        init();
    }

    public StateRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public StateRecyclerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init()
    {
        initViews();

        setErrorAction("重试", new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onRefreshListener != null) {
                    onRefreshListener.onRefresh();
                }
            }
        });

        setEmptyAction("重试", new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onRefreshListener != null) {
                    onRefreshListener.onRefresh();
                }
            }
        });

    }

    private void initViews()
    {
        swipeRefreshLayout = new SwipeRefreshLayout(getContext());
        swipeRefreshLayout.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        addContentView(swipeRefreshLayout);

        recyclerView = new RecyclerView(getContext());
        recyclerView.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        swipeRefreshLayout.addView(recyclerView);
    }
    public void setAdapter(RecyclerView.Adapter adapter)
    {
        setAdapter(adapter,false);
    }
    public void setAdapter(RecyclerView.Adapter adapter,boolean showProgress)
    {
        if(adapter == null)
        {
            showEmptyView();
            return;
        }
        if(showProgress)
        {
            showProgressView();
        }
        else
        {
            showRecyclerView();
        }

        recyclerView.setAdapter(adapter);
        adapter.registerAdapterDataObserver(new UpdateListener());

    }



    public void setEnabled(boolean enable)
    {
        swipeRefreshLayout.setEnabled(enable);
    }
    /**
     * Set the layout manager to the recycler
     *
     * @param manager
     */
    public void setLayoutManager(RecyclerView.LayoutManager manager) {
        recyclerView.setLayoutManager(manager);
    }

    /**
     * Set the colors for the SwipeRefreshLayout states
     *
     * @param colRes1
     * @param colRes2
     * @param colRes3
     * @param colRes4
     */
    public void setRefreshingColorResources(@ColorRes int colRes1, @ColorRes int colRes2, @ColorRes int colRes3, @ColorRes int colRes4) {
        swipeRefreshLayout.setColorSchemeResources(colRes1, colRes2, colRes3, colRes4);
    }
    /**
     * RecyclerView can perform several optimizations if it can know in advance that changes in
     * adapter content cannot change the size of the RecyclerView itself.
     * If your use of RecyclerView falls into this category, set this to true.
     *
     * @param hasFixedSize true if adapter changes cannot affect the size of the RecyclerView.
     */
    public void setHasFixedSize(boolean hasFixedSize) {
        recyclerView.setHasFixedSize(hasFixedSize);
    }
    /**
     * Set the colors for the SwipeRefreshLayout states
     *
     * @param col1
     * @param col2
     * @param col3
     * @param col4
     */
    public void setRefreshingColor(int col1, int col2, int col3, int col4) {
        swipeRefreshLayout.setColorSchemeColors(col1, col2, col3, col4);
    }

    /**
     * Set the color resources used in the progress animation from color resources.
     * The first color will also be the color of the bar that grows in response
     * to a user swipe gesture.
     *
     * @param colorResIds
     */
    public void setColorSchemeResources(@ColorRes int... colorResIds) {
        swipeRefreshLayout.setColorSchemeResources(colorResIds);
    }
    /**
     * Set the colors used in the progress animation. The first
     * color will also be the color of the bar that grows in response to a user
     * swipe gesture.
     *
     * @param colors
     */
    @ColorInt
    public void setColorSchemeColors(int... colors) {
        swipeRefreshLayout.setColorSchemeColors(colors);
    }

    public void setOnRefreshListener(SwipeRefreshLayout.OnRefreshListener listener)
    {
        this.onRefreshListener = listener;
        swipeRefreshLayout.setOnRefreshListener(listener);
    }

    public void setRefreshing(boolean refreshing)
    {
        swipeRefreshLayout.setRefreshing(refreshing);
    }


    /**
     * Add the onItemTouchListener for the recycler
     *
     * @param listener
     */
    public void addOnItemTouchListener(RecyclerView.OnItemTouchListener listener) {
        recyclerView.addOnItemTouchListener(listener);
    }

    /**
     * Remove the onItemTouchListener for the recycler
     *
     * @param listener
     */
    public void removeOnItemTouchListener(RecyclerView.OnItemTouchListener listener) {
        recyclerView.removeOnItemTouchListener(listener);
    }

    /**
     * @return the recycler adapter
     */
    public RecyclerView.Adapter getAdapter() {
        return recyclerView.getAdapter();
    }


    public void setOnTouchListener(OnTouchListener listener) {
        recyclerView.setOnTouchListener(listener);
    }

    public void setItemAnimator(RecyclerView.ItemAnimator animator) {
        recyclerView.setItemAnimator(animator);
    }

    public void addItemDecoration(RecyclerView.ItemDecoration itemDecoration) {
        recyclerView.addItemDecoration(itemDecoration);
    }

    public void addItemDecoration(RecyclerView.ItemDecoration itemDecoration, int index) {
        recyclerView.addItemDecoration(itemDecoration, index);
    }

    public void removeItemDecoration(RecyclerView.ItemDecoration itemDecoration) {
        recyclerView.removeItemDecoration(itemDecoration);
    }

    @Override
    protected void onHideContentView() {
        swipeRefreshLayout.setRefreshing(false);
        swipeRefreshLayout.setEnabled(false);
    }
    public void showRecyclerView() {
        showContentView();
        swipeRefreshLayout.setEnabled(true);
    }


    private class UpdateListener extends RecyclerView.AdapterDataObserver
    {
        @Override
        public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
            super.onItemRangeMoved(fromPosition, toPosition, itemCount);
            update();
        }

        @Override
        public void onChanged() {
            super.onChanged();
            update();
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount) {
            super.onItemRangeChanged(positionStart, itemCount);
            update();
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount, Object payload) {
            super.onItemRangeChanged(positionStart, itemCount, payload);
            update();
        }

        @Override
        public void onItemRangeInserted(int positionStart, int itemCount) {
            super.onItemRangeInserted(positionStart, itemCount);
            update();
        }

        @Override
        public void onItemRangeRemoved(int positionStart, int itemCount) {
            super.onItemRangeRemoved(positionStart, itemCount);
            update();
        }
        private void update()
        {
            if (recyclerView.getAdapter() instanceof NiceAdapter)
            {
                NiceAdapter niceAdapter = ((NiceAdapter) recyclerView.getAdapter());

                if(niceAdapter.isShowEmptyDataIsEmpty())
                {
                    if(niceAdapter.isDataEmpty())
                    {
                        showEmptyView();
                    }
                    else
                        showRecyclerView();
                }
                else
                {
                    if(niceAdapter.getItemCount() == 0)
                    {
                        showEmptyView();
                    }
                    else
                        showRecyclerView();
                }


            }
            else
            {
                if (recyclerView.getAdapter().getItemCount() == 0)
                {

                    showEmptyView();
                }
                else
                {
                    showRecyclerView();
                }
            }
        }
    }
}
