package com.lcc.uestc.fragment;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;

import com.lcc.uestc.R;
import com.lcc.uestc.adapter.CodeAdapter;
import com.lcc.uestc.bean.ActionBarCommands;
import com.lcc.uestc.bean.CodeBean;
import com.lcc.uestc.bean.Command;
import com.lcc.uestc.utils.CommonUtils;
import com.lcc.uestc.widget.Recycler.NiceAdapter;
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
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class CodeFragment extends BaseFragment {

    public static final String URL = "http://jcodecraeer.com";

    @Bind(R.id.stateRecyclerView)
    StateRecyclerView niceRecyclerView;

    RecyclerView.LayoutManager layoutManager;

    CodeAdapter adapter;
    Subscription subscription;
    @Override
    protected void initInOnCreate() {
        actionBarCommands = new CodeCommands();
        layoutManager = new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL);
        adapter = new CodeAdapter(activity);
        niceRecyclerView.setAdapter(adapter,true);
        niceRecyclerView.setLayoutManager(layoutManager);
        adapter.setOnLoadMoreListener(new NiceAdapter.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                if(actionBarCommands.isEmpty())
                    return;
                ((CodeCommand) actionBarCommands.commands.get(actionBarCommands.getSelect())).loadMore();
            }
        });


        niceRecyclerView.setColorSchemeColors(getColorPrimary());
        niceRecyclerView.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener(){
            @Override
            public void onRefresh() {
                actionBarCommands.refresh(true);
            }
        });
        adapter.showLoadMoreView();
        actionBarCommands.refresh(false);

    }


    void parseMenu(final String html)
    {
        Document document = Jsoup.parse(html);
        Element ul = document.getElementById("slidebar-category-list");
        Elements lis = ul.getElementsByTag("li");
        int i = 0;
        for (Element li : lis)
        {
            if(i == 0);
            else
            {
                String title = li.text();
                title = title.substring(0,title.indexOf(' ')).trim();
                int index = title.indexOf('(');
                if(index != -1)
                    title = title.substring(0,index);
                actionBarCommands.commands.add(new CodeCommand(title.trim(),li.getElementsByTag("a").attr("href")));
            }
            i++;
        }
        niceRecyclerView.post(new Runnable() {
            @Override
            public void run() {
                actionBarCommands.notifyDataSetChanged();
            }
        });
    }


    public List<CodeBean> parseCodeList(String html, final CodeCommand codeCommand)
    {
        final List<CodeBean> finalCodeList = new ArrayList<>();

        Document document = Jsoup.parse(html);
        Element ul = document.getElementById("codelist");
        Elements lis = ul.getElementsByTag("li");
        for (Element li : lis)
        {
            Elements elements = li.getElementsByAttributeValue("class","codeli-name");
            if(elements.isEmpty())
            {
                break;
            }
            CodeBean codeBean = new CodeBean();
            codeBean.setTitle(elements.text());
            codeBean.setUrl(URL+elements.get(0).getElementsByTag("a").attr("href"));
            codeBean.setDescription(li.getElementsByTag("p").text());
            codeBean.setPicture(URL+li.getElementsByTag("img").attr("src"));
            finalCodeList.add(codeBean);
        }

        if(codeCommand != null && codeCommand.totalCode == -1)
        {
            Elements element = document.getElementsByAttributeValue("class","pagination");
            for (Element pages : element)
            {
                Element page_span= pages.getElementsByTag("span").get(0);
                String page_info = page_span.text();

                int last = page_info.indexOf('条');
                int first = 0;
                for (int i=last-1;i>=0;i--)
                {
                    if(!Character.isDigit(page_info.charAt(i)))
                    {
                        first = i;
                        break;
                    }
                }
                int total = 0;
                try
                {
                    total = Integer.parseInt(page_info.substring(first+1,last));
                }
                catch (NumberFormatException e)
                {
                    e.printStackTrace();
                }
                codeCommand.setTotalCode(total);
            }
        }


        return finalCodeList;
    }



    @Override
    protected int getLayoutView()
    {
        return R.layout.fragment_broadcast;
    }


    public class CodeCommand extends Command
    {
        public static final int CODE_PER_PAGE = 10;


        public List<CodeBean> codeList = new ArrayList<>();


        public void setTotalCode(int totalCode) {
            this.totalCode = totalCode;
            totalPage = totalCode / CODE_PER_PAGE + 1;
        }


        private int totalCode = -1;
        private int totalPage = -1;
        public int currentPage = 1;
        String id;
        CodeCommand(String title,String id)
        {
            super(title);
            this.id=id;
        }
        boolean sure = false;
        CodeCommand(String title,String id,boolean sure)
        {
            super(title);
            this.id=id;
            this.sure = sure;
        }
        public void loadMore()
        {
            if(totalCode == -1)
                return;
            CommonUtils.i(" load "+title+" data ,total " + totalCode+" ,has loaded "+adapter.getDataSize());
            if(totalCode > adapter.getDataSize())
            {

                adapter.showLoadMoreView();
                currentPage++;
                fetchData(id,currentPage,this);

            }
            else
            {
                adapter.showNoMoreView();
                CommonUtils.i("no more data");
            }

        }
        @Override
        public void action() {
            currentPage = 1;
            adapter.showLoadMoreView();
            niceRecyclerView.setRefreshing(true);

            fetchData(id,currentPage,this);
        }
    }


    class CodeCommands extends ActionBarCommands
    {
        CodeCommands()
        {
            commands.add(new CodeCommand("全部代码","/plus/list.php?tid=31",true));
        }
        @Override
        public void refresh(boolean force) {
            if(!commands.isEmpty())
            {
                CodeCommand codeCommand = (CodeCommand)commands.get(getSelect());
                codeCommand.codeList.clear();
                codeCommand.action();
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if(subscription != null && subscription.isUnsubscribed())
        {
            CommonUtils.i("onPause :unsubscribe");
            subscription.unsubscribe();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(subscription != null && subscription.isUnsubscribed())
        {
            CommonUtils.i("onDestroy :unsubscribe");
            subscription.unsubscribe();
        }
    }

    private String getString(final String id, int page) throws IOException {
        return OkHttpUtils.get().url(URL+id+"&PageNo="+page).build().execute().body().string();
    }
    void fetchData(final String id, final int page, final CodeCommand codeCommand)
    {
        if(subscription != null && subscription.isUnsubscribed())
        {
            CommonUtils.i("fetchData :unsubscribe");
            subscription.unsubscribe();
        }
        Observable<List<CodeBean>> observable = Observable.just(id)
                .map(new Func1<String, String>() {
                    @Override
                    public String call(String id) {
                        try
                        {
                            return getString(id,page);
                        }
                        catch (IOException e)
                        {
                            e.printStackTrace();
                        }
                        return null;
                    }
                })
                .map(new Func1<String, List<CodeBean>>() {
                    @Override
                    public List<CodeBean> call(String s) {
                        if(s == null)
                            return null;

                        if(actionBarCommands.count() < 2)
                        {
                            parseMenu(s);
                        }

                        return parseCodeList(s,codeCommand);
                    }
                });

        subscription =  observable
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<CodeBean>>() {

                    @Override
                    public void onCompleted() {
                        niceRecyclerView.setRefreshing(false);
                    }

                    @Override
                    public void onError(Throwable e) {
                        niceRecyclerView.setRefreshing(false);
                        niceRecyclerView.showErrorView();
                        CommonUtils.toast(e.toString());
                    }

                    @Override
                    public void onNext(List<CodeBean> codeBeen) {
                        if(page == 1)
                        {
                            adapter.initData(codeBeen);
                        }
                        else
                        {
                            adapter.addData(codeBeen);
                        }
                    }
                });
    }

    @Override
    public String toString() {
        return "代码";
    }
}
