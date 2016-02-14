package com.lcc.uestc.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wanli on 2015/7/1.
 */
public abstract class BaseAdapter<T> extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    public final List<T> data;
    final Activity context;
    final LayoutInflater mInflater;
    private OnItemClickListener<T> mOnItemClickListener;
    private OnItemLongClickListener<T> mOnItemLongClickListener;
    public BaseAdapter(Activity context)
    {
        this.context = context;
        mInflater = LayoutInflater.from(context);
        data = new ArrayList<T>();
    }
    @Override
    public int getItemCount() {
        return data.size();
    }

    public void insertOne(int position,T item)
    {
        data.add(position, item);
        notifyItemInserted(position);
    }
    public void insertRange(List<T> list)
    {
        if(list != null && !list.isEmpty())
        {
            int start = data.size();
            data.addAll(list);
            notifyItemRangeInserted(start,list.size());
        }

    }
    public void removeOne(int position)
    {
        data.remove(position);
        notifyItemRemoved(position);
    }

    public void setData(List<T> list)
    {
        data.clear();
        if(list != null)
        {
            data.addAll(list);
        }
        notifyDataSetChanged();
    }

    public void setDataWithRange(List<T> list)
    {
        if(list == null)
        {
            notifyDataSetChanged();
            return;
        }
        int before = data.size();
        int add = list.size() - before;
        data.clear();
        data.addAll(list);
        notifyItemRangeInserted(before,add);
    }

    public void addData(List<T> list)
    {
        if(this.data == null)
        {
            setData(list);
            return;
        }
        int l = data.size();
        data.addAll(list);
        notifyItemRangeInserted(l, list.size());
    }

    public void setOnItemClickListener(BaseAdapter.OnItemClickListener l)
    {
        this.mOnItemClickListener = l;
    }
    public void setOnItemLongClickListener(BaseAdapter.OnItemLongClickListener l)
    {
        this.mOnItemLongClickListener = l;
    }
    protected void setListener(View itemView,final T mItem)
    {
        if(mOnItemClickListener != null)
        {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnItemClickListener.onItemClick(mItem);
                }
            });
        }
        if(mOnItemLongClickListener != null)
        {
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    return mOnItemLongClickListener.onItemLongClick(mItem);
                }
            });
        }
    }
    public interface OnItemLongClickListener<T>
    {
        boolean onItemLongClick(T mItem);
    }
    public static interface OnItemClickListener<T>
    {
        void onItemClick(T mItem);
    }
}
