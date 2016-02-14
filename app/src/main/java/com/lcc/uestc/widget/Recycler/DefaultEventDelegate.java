package com.lcc.uestc.widget.Recycler;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.lcc.uestc.R;


/**
 * Created by lcc_luffy on 2016/1/11.
 */
public class DefaultEventDelegate implements NiceAdapter.EventDelegate{

    public static final int IS_SHOW_MORE_VIEW = 0;
    public static final int IS_SHOW_NO_MORE_VIEW = 1;
    public static final int IS_SHOW_ERROR_VIEW = 2;


    private boolean enableReload = true;


    private NiceAdapter.OnLoadMoreListener onLoadMoreListener;
    private View.OnClickListener onErrorClickListener;

    private int state = IS_SHOW_MORE_VIEW;

    private FooterView footer;

    private NiceAdapter niceAdapter;
    public DefaultEventDelegate(NiceAdapter niceAdapter)
    {
        this.niceAdapter = niceAdapter;
        footer = new FooterView(niceAdapter.getContext());
        niceAdapter.addFooterView(footer);
    }

    @Override
    public void setOnLoadMoreListener(NiceAdapter.OnLoadMoreListener onLoadMoreListener) {
        this.onLoadMoreListener = onLoadMoreListener;
    }

    @Override
    public void setOnErrorViewClickListener(View.OnClickListener onErrorViewClickListener) {
        onErrorClickListener = onErrorViewClickListener;
    }

    @Override
    public void showErrorView() {
        footer.showErrorView();
    }

    @Override
    public void showNoMoreView() {
        footer.showNoMoreView();
    }

    @Override
    public void showLoadMoreView() {
        footer.showMoreView();
    }

    @Override
    public void onDataChanged(int offset) {

    }


    public void onMoreViewShowed()
    {
        if(onLoadMoreListener != null && !niceAdapter.isDataEmpty())
            onLoadMoreListener.onLoadMore();
    }

    public void onErrorViewShowed()
    {

    }

    public void onNoMoreViewShowed()
    {

    }

    private class FooterView implements NiceAdapter.ItemView
    {
        private FrameLayout container;
        private View loadMoreView;
        private View noMoreView;
        private View errorView;

        FooterView(Context context)
        {
            loadMoreView = LayoutInflater.from(context).inflate(R.layout.footer,null);
            noMoreView = LayoutInflater.from(context).inflate(R.layout.no_more_view,null);
            errorView = LayoutInflater.from(context).inflate(R.layout.error_view,null);

            errorView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onErrorClickListener != null) {
                        onErrorClickListener.onClick(v);
                    }
                    if(enableReload)
                    {
                        showMoreView();
                        onMoreViewShowed();
                    }
                }
            });

            container = new FrameLayout(context);
            container.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

            showMoreView();
        }
        @Override
        public View onCreateView(ViewGroup parent) {
            return container;
        }

        @Override
        public void onBindViewHolder(int realPosition) {
            switch (state)
            {
                case IS_SHOW_MORE_VIEW:
                    onMoreViewShowed();
                    break;
                case IS_SHOW_ERROR_VIEW:
                    onErrorViewShowed();
                    break;
                case IS_SHOW_NO_MORE_VIEW:
                    onNoMoreViewShowed();
                    break;
            }
        }

        private void showView(View view)
        {
            if (view!=null)
            {
                if (container.getVisibility() != View.VISIBLE)
                    container.setVisibility(View.VISIBLE);
                if (view.getParent()==null)
                    container.addView(view);

                for (int i = 0; i < container.getChildCount(); i++)
                {
                    if (container.getChildAt(i) == view)view.setVisibility(View.VISIBLE);
                    else container.getChildAt(i).setVisibility(View.GONE);
                }
            }
            else
            {
                container.setVisibility(View.GONE);
            }
        }

        public void showErrorView(){
            showView(errorView);
            state = IS_SHOW_ERROR_VIEW;
        }
        public void showMoreView(){
            showView(loadMoreView);
            state = IS_SHOW_MORE_VIEW;
        }
        public void showNoMoreView(){
            showView(noMoreView);
            state = IS_SHOW_NO_MORE_VIEW;
        }

    }
}
