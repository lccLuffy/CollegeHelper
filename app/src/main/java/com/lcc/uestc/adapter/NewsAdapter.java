package com.lcc.uestc.adapter;

import android.app.Activity;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.lcc.uestc.R;
import com.lcc.uestc.bean.UestcNewsBean;
import com.lcc.uestc.utils.ActivityJumpUtils;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by wanli on 2015/7/13.
 */
public class NewsAdapter extends BaseAdapter<UestcNewsBean>{

    private RecyclerView.Adapter adapterWrap;

    @Override
    public void setData(List<UestcNewsBean> list) {
        data.clear();
        if(list == null)
        {
            notifyDataSetChanged();
            return;
        }
        data.addAll(list);
        adapterWrap.notifyDataSetChanged();
    }

    @Override
    public void addData(List<UestcNewsBean> list) {
        if(this.data == null)
        {
            setData(list);
            return;
        }
        int l = data.size();
        data.addAll(list);
        adapterWrap.notifyItemRangeInserted(l, list.size());
    }

    public void setAdapterWrap(RecyclerView.Adapter adapterWrap)
    {
        this.adapterWrap = adapterWrap;
    }
    public NewsAdapter(final Activity context)
    {
        super(context);
        setOnItemClickListener(new OnItemClickListener<UestcNewsBean>() {
            @Override
            public void onItemClick(UestcNewsBean mItem) {
                ActivityJumpUtils.toWebView(context,mItem.getUrl());
            }
        });
    }

    @Override
     public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = mInflater.inflate(R.layout.item_news_layout,null);
        return new Holder(v);
    }
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        Holder mHolder = (Holder)holder;
        UestcNewsBean uestcNewsBean = data.get(position);
        mHolder.bindData(uestcNewsBean);

        setListener(holder.itemView,uestcNewsBean);
    }

    class Holder extends RecyclerView.ViewHolder
    {
        @Bind(R.id.imageView)
        SimpleDraweeView imageView;

        @Bind(R.id.title)
        TextView title;

        @Bind(R.id.content)
        TextView content;

        public Holder(View itemView)
        {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void bindData(UestcNewsBean uestcNewsBean)
        {
            title.setText(uestcNewsBean.getTitle()+uestcNewsBean.getTime());
            content.setText(uestcNewsBean.getContent());
            if(uestcNewsBean.getPic() != null) {
                imageView.setImageURI(Uri.parse(uestcNewsBean.getPic()));
            }
            else
            {
                imageView.setVisibility(View.INVISIBLE);
            }
        }
    }
}
