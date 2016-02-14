package com.lcc.uestc.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lcc.uestc.R;
import com.lcc.uestc.activity.WebViewActivity;
import com.lcc.uestc.bean.SoftNews;
import com.lcc.uestc.utils.ActivityJumpUtils;

/**
 * Created by lcc_luffy on 2016/1/7.
 */
public class SoftNewsAdapter extends BaseAdapter<SoftNews>{

    public SoftNewsAdapter(final Activity context) {
        super(context);
        setOnItemClickListener(new OnItemClickListener<SoftNews>() {
            @Override
            public void onItemClick(SoftNews mItem) {
                ActivityJumpUtils.toWebView(context,mItem.getUrl(), WebViewActivity.class);
            }
        });
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = mInflater.inflate(R.layout.soft_item,parent,false);
        return new Holder(v);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Holder holder1 = (Holder) holder;
        holder1.textView.setText(data.get(position).getTitle());
        setListener(holder.itemView,data.get(position));
    }
    private class Holder extends RecyclerView.ViewHolder
    {
        TextView textView;
        public Holder(View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.text);
        }

    }
}
