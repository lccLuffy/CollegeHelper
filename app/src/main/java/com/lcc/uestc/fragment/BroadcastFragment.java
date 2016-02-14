package com.lcc.uestc.fragment;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;

import com.lcc.uestc.R;
import com.lcc.uestc.adapter.SoftNewsAdapter;
import com.lcc.uestc.bean.ActionBarCommands;
import com.lcc.uestc.bean.Command;
import com.lcc.uestc.bean.SoftNews;
import com.lcc.uestc.utils.CommonUtils;
import com.lcc.uestc.widget.Recycler.StateRecyclerView;
import com.zhy.http.okhttp.OkHttpUtils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import okhttp3.Response;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class BroadcastFragment extends BaseFragment {
    public static final String URL = "http://www.is.uestc.edu.cn";

    @Bind(R.id.stateRecyclerView)
    StateRecyclerView niceRecyclerView;

    LinearLayoutManager layoutManager;

    SoftNewsAdapter adapter;

    Document document;

    @Override
    protected void initInOnCreate() {
        actionBarCommands = new BroadcastCommands();
        layoutManager = new LinearLayoutManager(activity);
        adapter = new SoftNewsAdapter(activity);
        niceRecyclerView.setAdapter(adapter,true);
        niceRecyclerView.setLayoutManager(layoutManager);
        niceRecyclerView.setColorSchemeColors(getColorPrimary());
        niceRecyclerView.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener(){
            @Override
            public void onRefresh() {
                actionBarCommands.refresh(true);
            }
        });
        actionBarCommands.refresh(true);
    }



    public Document getDocument() throws IOException {
        Response response = OkHttpUtils.get()
                .url(URL)
                .build()
                .execute();
        return document = Jsoup.parse(response.body().string());
    }

    class BroadcastCommand extends Command
    {
        private List<SoftNews> data = new ArrayList();
        private String tag;
        BroadcastCommand(String title,String tag)
        {
            super(title);
            this.tag=tag;
        }
        @Override
        public void action() {
            fetchData(tag,this);
        }
    }

    Subscription fetchData(final String tag, final BroadcastCommand broadcastCommand)
    {
        Observable<List<SoftNews>> observable = Observable.just(tag)
                .map(new Func1<String, Document>() {
                    @Override
                    public Document call(String s) {
                        if(document == null)
                            try
                            {
                                document = getDocument();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        return document;
                    }
                }).map(new Func1<Document, List<SoftNews>>() {
            @Override
            public List<SoftNews> call(Document document) {
                return parseDocument(document,tag,broadcastCommand);
            }
        });
        return observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<List<SoftNews>>() {
            @Override
            public void onCompleted() {
                niceRecyclerView.setRefreshing(false);
            }

            @Override
            public void onError(Throwable e) {
                niceRecyclerView.setRefreshing(false);
                CommonUtils.toast("BroadcastFragment :"+e.toString());
            }

            @Override
            public void onNext(List<SoftNews> softNewses) {
                adapter.setData(softNewses);
            }
        });
    }

    private List<SoftNews> parseDocument(Document document, String id, BroadcastCommand broadcastCommand)
    {
        if(!broadcastCommand.data.isEmpty())
        {
            return broadcastCommand.data;
        }
        List<SoftNews> list = new ArrayList<>();
        Elements uls= document.getElementById(id).getElementsByTag("ul");
        for(Element ul : uls)
        {
            Elements lis = ul.getElementsByTag("li");
            for(Element li : lis)
            {
                SoftNews softNews = new SoftNews();
                softNews.setTitle(li.text());
                softNews.setUrl(URL+"/"+li.getElementsByTag("a").attr("href"));
                list.add(softNews);
            }
        }
        return list;
    }

    class BroadcastCommands extends ActionBarCommands
    {
        BroadcastCommands()
        {
            commands.add(new BroadcastCommand("最新公告","last"));
            commands.add(new BroadcastCommand("院办","office"));
            commands.add(new BroadcastCommand("组织人事","organization"));
            commands.add(new BroadcastCommand("学生科","stu"));
            commands.add(new BroadcastCommand("教务科","teach"));
            commands.add(new BroadcastCommand("研管科","graduate"));

            commands.add(new BroadcastCommand("对外合作","crop"));
            commands.add(new BroadcastCommand("科研","research"));
            commands.add(new BroadcastCommand("国际交流","international"));
            commands.add(new BroadcastCommand("实验中心","experiment"));
            commands.add(new BroadcastCommand("创新工坊","cxgf"));
            commands.add(new BroadcastCommand("创新工坊","outter"));
        }


        @Override
        public void refresh(boolean force) {
            niceRecyclerView.setRefreshing(true);
            document = null;
            commands.get(select).action();
        }
    }


    @Override
    public String toString() {
        return "部门公告";
    }

    @Override
    protected int getLayoutView() {
        return R.layout.fragment_broadcast;
    }
}
