package com.lcc.uestc.widget.Recycler;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by lcc_luffy on 2016/1/11.
 */
public abstract class NiceAdapter<T> extends RecyclerView.Adapter<NiceViewHolder>{
    final protected List<T> data;

    private int dataPreviewSize;

    final List<ItemView> headers;
    final List<ItemView> footers;

    private EventDelegate eventDelegate;

    private OnItemClickListener mItemClickListener;
    private OnItemLongClickListener mItemLongClickListener;
    /**
     * Lock used to modify the content of {@link #data}. Any write operation
     * performed on the array should be synchronized on this lock.
     */
    private final Object mLock = new Object();


    public Context getContext() {
        return context;
    }

    protected Context context;

    public NiceAdapter(Context context)
    {
        this.context = context;
        data = new ArrayList<>();
        headers = new ArrayList<>();
        footers = new ArrayList<>();

        eventDelegate = new DefaultEventDelegate(this);
    }

    public void clear(boolean notifyDataSetChanged)
    {
        data.clear();
        if(notifyDataSetChanged)
        {
            notifyDataSetChanged();
        }
    }

    public boolean isDataEmpty()
    {
        return data.isEmpty();
    }
    public int getDataSize()
    {
        return data.size();
    }

    public void initData(Collection<T> collection)
    {
        dataPreviewSize = data.size();
        data.clear();
        if(collection == null || collection.isEmpty())
        {
            checkIsDataChanged();

            notifyDataSetChanged();
            return;
        }
        synchronized (mLock)
        {
            data.addAll(collection);
            checkIsDataChanged();

            notifyDataSetChanged();
        }
    }

    private void checkIsDataChanged()
    {
        if(data.size() != dataPreviewSize)
        {
            eventDelegate.onDataChanged(data.size() - dataPreviewSize);
        }
    }

    public void addData(Collection<T> collection)
    {
        if(collection != null)
        {
            synchronized (mLock)
            {
                if(data.isEmpty())
                {
                    initData(collection);
                }
                else
                {
                    int beforeSize = data.size() + headers.size();

                    dataPreviewSize = data.size();
                    data.addAll(collection);
                    checkIsDataChanged();

                    notifyItemRangeInserted(beforeSize,collection.size());
                }

            }
        }
    }

    public void addData(T item)
    {
        addData(data.size(),item);
    }
    public void addData(int position,T item)
    {
        if(item != null)
        {
            synchronized (mLock)
            {
                /**
                 * 0  headers   0
                 * 1  headers   1
                 * 2   data     0
                 * 3   data     1
                 * 4
                 *
                 */
                dataPreviewSize = data.size();
                data.add(position,item);
                checkIsDataChanged();

                notifyItemInserted(position + headers.size());
            }
        }
    }

    public boolean isShowEmptyDataIsEmpty() {
        return showEmptyDataIsEmpty;
    }

    private boolean showEmptyDataIsEmpty = true;
    public void showEmptyDataIsEmpty(boolean show)
    {
        showEmptyDataIsEmpty = show;
    }

    private View onCreateHeaderFooterViewByType(ViewGroup parent, int viewType)
    {
        for (ItemView headerView:headers){
            if (headerView.hashCode() == viewType){
                View view = headerView.onCreateView(parent);
                StaggeredGridLayoutManager.LayoutParams layoutParams = new StaggeredGridLayoutManager.LayoutParams(view.getLayoutParams());
                layoutParams.setFullSpan(true);
                view.setLayoutParams(layoutParams);
                return view;
            }
        }
        for (ItemView footerView:footers){
            if (footerView.hashCode() == viewType){
                View view = footerView.onCreateView(parent);
                StaggeredGridLayoutManager.LayoutParams layoutParams = new StaggeredGridLayoutManager.LayoutParams(view.getLayoutParams());
                layoutParams.setFullSpan(true);
                view.setLayoutParams(layoutParams);
                return view;
            }
        }
        return null;
    }
    @Override
    final public NiceViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        View view = onCreateHeaderFooterViewByType(parent,viewType);
        if(view != null)
        {
            return new EmptyViewHolder(view);
        }
        final NiceViewHolder niceViewHolder = onCreateNiceViewHolder(parent,viewType);
        if (mItemClickListener!=null) {
            niceViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mItemClickListener.onItemClick(niceViewHolder.getAdapterPosition() - headers.size());
                }
            });
        }

        if (mItemLongClickListener!=null){
            niceViewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    return mItemLongClickListener.onItemClick(niceViewHolder.getAdapterPosition() - headers.size());
                }
            });
        }

        return niceViewHolder;
    }

    abstract protected NiceViewHolder onCreateNiceViewHolder(ViewGroup parent, int viewType);

    @Override
    final public void onBindViewHolder(NiceViewHolder holder, int position) {
        holder.itemView.setId(position);
        if (headers.size()!=0 && position<headers.size()){
            headers.get(position).onBindViewHolder(position);
            return ;
        }

        int i = position - headers.size() - data.size();
        if (footers.size()!=0 && i>=0){
            footers.get(i).onBindViewHolder(position);
            return ;
        }
        holder.onBindData(data.get(position-headers.size()));
    }

    @Override
    final public int getItemViewType(int position) {
        if(!headers.isEmpty() && position < headers.size())
        {
            return headers.get(position).hashCode();
        }

        if(!footers.isEmpty())
        {
            /*
            eg:
            0:header1
            1:header2   2
            2:object1
            3:object2
            4:object3
            5:object4
            6:footer1   6(position) - 2 - 4 = 0
            7:footer2
             */
            int i = position - headers.size() - data.size();
            if (i >= 0){
                return footers.get(i).hashCode();
            }
        }
        return getViewType(position - headers.size());
    }

    protected int getViewType(int position) {
        return 0;
    }

    public void addFooterView(ItemView v)
    {
        if (v==null)
            throw new NullPointerException("ItemView can't be null");
        footers.add(v);
    }

    public void addHeaderView(ItemView v)
    {
        if (v==null)
            throw new NullPointerException("ItemView can't be null");
        headers.add(v);
    }

    private class EmptyViewHolder extends NiceViewHolder {
        public EmptyViewHolder(View itemView) {
            super(itemView);
        }
    }

    /**
     * 这个函数包含了头部和尾部view的个数，不是真正的item个数。
     * @return
     */
    @Override
    public int getItemCount() {
        return data.size() + footers.size() + headers.size();
    }
    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public interface OnItemLongClickListener {
        boolean onItemClick(int position);
    }


    public void setOnItemClickListener(OnItemClickListener listener){
        this.mItemClickListener = listener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener listener){
        this.mItemLongClickListener = listener;
    }

    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener)
    {
        eventDelegate.setOnLoadMoreListener(onLoadMoreListener);
    }

    public void setOnErrorViewClickListener(View.OnClickListener onErrorViewClickListener)
    {
        eventDelegate.setOnErrorViewClickListener(onErrorViewClickListener);
    }

    public GridLayoutManager.SpanSizeLookup obtainGridSpanSizeLookUp(int maxSpan)
    {
        return new GridLayoutSpanSizeLookup(maxSpan);
    }
    public void showErrorView()
    {
        eventDelegate.showErrorView();
    }
    public void showNoMoreView()
    {
        eventDelegate.showNoMoreView();
    }
    public void showLoadMoreView()
    {
        eventDelegate.showLoadMoreView();
    }

    private class GridLayoutSpanSizeLookup extends GridLayoutManager.SpanSizeLookup
    {
        private int maxSpan = 1;
        public GridLayoutSpanSizeLookup(int maxSpan) {
            this.maxSpan = maxSpan;
        }
        @Override
        public int getSpanSize(int position)
        {
            if(!headers.isEmpty() && position < headers.size())
            {
                return maxSpan;
            }

            if(!footers.isEmpty() && position >= headers.size() + data.size())
            {
                return maxSpan;
            }

            return 1;
        }
    }

    public interface ItemView
    {
        View onCreateView(ViewGroup parent);
        void onBindViewHolder(int realPosition);
    }

    public interface OnLoadMoreListener
    {
        void onLoadMore();
    }

    public interface EventDelegate
    {
        void onDataChanged(int offset);
        void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener);
        void setOnErrorViewClickListener(View.OnClickListener onErrorViewClickListener);
        void showErrorView();
        void showNoMoreView();
        void showLoadMoreView();

    }


}
