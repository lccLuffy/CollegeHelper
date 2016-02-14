package com.lcc.uestc.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.lcc.uestc.R;

/**
 * Created by lcc_luffy on 2016/1/9.
 */
public abstract class LoadMoreAdapter<T> extends BaseAdapter<T>{

    private static final int TYPE_ITEM = 0;
    private static final int TYPE_FOOTER = 1;
    private boolean mShowFooter = true;

    @Override
    public int getItemViewType(int position) {
        // 最后一个item设置为footerView
        if (!mShowFooter)
        {
            return TYPE_ITEM;
        }
        if(position + 1 == getItemCount())
        {
            return TYPE_FOOTER;
        }
        else
            return TYPE_ITEM;
    }

    public T getItem(int position) {
        return data == null ? null : data.get(position);
    }

    public void isShowFooter(boolean showFooter) {
        this.mShowFooter = showFooter;
        notifyDataSetChanged();
    }

    public boolean isShowFooter() {
        return this.mShowFooter;
    }

    @Override
    public int getItemCount() {
        int begin = mShowFooter ? 1 : 0;
        if(data == null) {
            return begin;
        }
        return data.size() + begin;
    }

    public LoadMoreAdapter(Activity context) {
        super(context);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (TYPE_ITEM == viewType)
        {
            return getViewHolder(parent);
        }
        else
        {
            View view = mInflater.inflate(R.layout.footer,null);
            view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            return new FooterViewHolder(view);
        }
    }


    public class  FooterViewHolder extends RecyclerView.ViewHolder
    {
        public FooterViewHolder(View itemView)
        {
            super(itemView);
        }
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder
    {
        public ItemViewHolder(View itemView)
        {
            super(itemView);
        }
        public void bindData(T data)
        {

        }
    }

    public abstract ItemViewHolder getViewHolder(ViewGroup parent);

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof LoadMoreAdapter.ItemViewHolder)
        {
            ((ItemViewHolder) holder).bindData(data.get(position));
            setListener(holder.itemView,data.get(position));
        }
    }
}
