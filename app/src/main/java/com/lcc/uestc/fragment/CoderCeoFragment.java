package com.lcc.uestc.fragment;

import android.widget.TextView;

import com.lcc.uestc.R;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import butterknife.Bind;
import okhttp3.Request;
import rx.Observable;
import rx.functions.Func1;

public class CoderCeoFragment extends BaseFragment {

    public static final String URL = "http://www.codeceo.com/";
    @Bind(R.id.text)
    TextView textView;
    @Override
    protected void initInOnCreate() {
        OkHttpUtils.get().url(URL).build().execute(new StringCallback() {
            @Override
            public void onError(Request request, Exception e) {
            }

            @Override
            public void onResponse(String response) {
                parser(response);

            }
        });

    }

    public void parser(String html)
    {
        Observable.just(html)
                .map(new Func1<String, Object>() {
                    @Override
                    public Object call(String s) {
                        Document document = Jsoup.parse(s);
                        Elements nav = document.getElementsByAttributeValue("class","nav");



                        return null;
                    }
                });
    }

    @Override
    protected int getLayoutView() {
        return R.layout.fragment_coder_ceo;
    }
}
