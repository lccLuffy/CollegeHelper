package com.lcc.uestc.fragment;


import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;

import com.lcc.uestc.Config;
import com.lcc.uestc.R;
import com.lcc.uestc.adapter.NewsAdapter;
import com.lcc.uestc.bean.Command;
import com.lcc.uestc.bean.ActionBarCommands;
import com.lcc.uestc.bean.UestcNewsBean;
import com.lcc.uestc.data.SimpleCallback;
import com.lcc.uestc.data.UESTC;
import com.lcc.uestc.widget.DividerItemDecoration;
import com.lcc.uestc.widget.LccRecyclerView;

import java.util.List;
import java.util.Map;

import butterknife.Bind;

/**
 * A simple {@link Fragment} subclass.
 */
public class NewsFragment extends BaseFragment implements LccRecyclerView.LoadMoreListener{
    @Bind(R.id.recyclerView)
    LccRecyclerView recyclerView;

    @Bind(R.id.swipeRefreshLayout)
    SwipeRefreshLayout swipeRefreshLayout;

    private int type = Config.jdxw,page = 1,oldPage = 0;
    NewsAdapter adapter;
    LinearLayoutManager layoutManager;
    @Override
    public void onDestroy() {
        super.onDestroy();
    }
    @Override
    protected int getLayoutView() {
        return R.layout.fragment_news;
    }

    @Override
    protected void initInOnCreate() {
        actionBarCommands = new NewsCommands();
        swipeRefreshLayout.setColorSchemeColors(getColorPrimary());
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                page = 1;
                get(type,page);
            }
        });

        layoutManager = new LinearLayoutManager(activity);
        adapter = new NewsAdapter(activity);
        recyclerView.setAdapter(adapter);
        adapter.setAdapterWrap(recyclerView.getAdapterWrap());
        recyclerView.addItemDecoration(new DividerItemDecoration(activity,DividerItemDecoration.VERTICAL_LIST));

        recyclerView.setLoadMoreListener(this);
        recyclerView.setLayoutManager(layoutManager);
        get(type, page);
    }

    boolean progress = false;
    private void get(final int type,final int page)
    {
        if(progress)
            return;
        progress = true;
        if(page == 1)
            swipeRefreshLayout.setRefreshing(true);
        UESTC.getNews(type, page, new SimpleCallback<List<UestcNewsBean>>() {
            @Override
            public void onSuccess(List<UestcNewsBean> data) {
                progress = false;
                if (page == 1) {
                    swipeRefreshLayout.setRefreshing(false);
                    adapter.setData(data);
                } else {
                    recyclerView.loadMoreFinish();
                    adapter.addData(data);
                }
            }

            @Override
            public void onFailed(String failReason) {
                swipeRefreshLayout.setRefreshing(false);
                progress = false;
                showSnackBar(failReason);
            }
        });
    }
    @Override
    public void onLoadMore() {
        oldPage = page;
        page ++;
        get(type,page);
    }

    class NewsCommand extends Command
    {
        private String title;
        private final int type;

        public NewsCommand(String title, int semester) {
            super(title);
            this.type = semester;
        }
        @Override
        public String toString() {
            return title;
        }
        @Override
        public void action() {
            page = 1;
            NewsFragment.this.type = type;
            get(type, page);
        }
    }

    class NewsCommands extends ActionBarCommands
    {
        NewsCommands()
        {
            for (Map.Entry<Integer,String> kv : Config.NEWS.entrySet())
            {
                commands.add(new NewsCommand(kv.getValue(),kv.getKey()));
            }
        }
        @Override
        public void refresh(boolean force) {
            page = 1;
            get(type,page);
        }
    }
}
