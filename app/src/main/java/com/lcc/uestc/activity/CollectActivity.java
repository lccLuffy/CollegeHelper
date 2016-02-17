package com.lcc.uestc.activity;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;

import com.lcc.uestc.R;
import com.lcc.uestc.adapter.CodeAdapter;
import com.lcc.uestc.bean.CodeBean;
import com.lcc.uestc.widget.Recycler.StateRecyclerView;

import java.util.List;

import butterknife.Bind;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;

public class CollectActivity extends BaseActivity {

    @Bind(R.id.stateRecyclerView)
    StateRecyclerView niceRecyclerView;


    LinearLayoutManager layoutManager;
    CodeAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    private void init() {
        layoutManager = new LinearLayoutManager(this);
        adapter = new CodeAdapter(this);
        niceRecyclerView.setAdapter(adapter,true);
        niceRecyclerView.setLayoutManager(layoutManager);
        niceRecyclerView.setColorSchemeColors(getColorPrimary());

        niceRecyclerView.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getCollectionList();
            }
        });

        getCollectionList();
        adapter.showNoMoreView();
    }

    void getCollectionList()
    {
        BmobQuery<CodeBean> query = new BmobQuery<>();
        query.setLimit(100);
        query.findObjects(this, new FindListener<CodeBean>() {
            @Override
            public void onSuccess(List<CodeBean> list) {
                adapter.initData(list);
                niceRecyclerView.setRefreshing(false);
            }

            @Override
            public void onError(int i, String s) {
                niceRecyclerView.setRefreshing(false);
            }
        });
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_collect;
    }

}
