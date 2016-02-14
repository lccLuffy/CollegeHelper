package com.lcc.uestc.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;

import com.lcc.uestc.R;

import butterknife.Bind;
import butterknife.OnClick;

public class WebViewActivity extends BaseActivity {

    @Bind(R.id.webView)
    WebView webview;

    private String url;

    @Bind(R.id.action_prev)
    ImageButton prev;

    @Bind(R.id.action_next)
    ImageButton next;

    @Bind(R.id.action_reload)
    ImageButton reload;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        url = getIntent().getStringExtra("url");
        init();
    }
    @Override
    protected int getLayoutView() {
        return R.layout.activity_webview;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_web_view, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id)
        {
            case R.id.action_open:
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                break;
            case android.R.id.home:
                finish();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return super.onOptionsItemSelected(item);
    }

    @OnClick({R.id.action_prev,R.id.action_reload,R.id.action_next})
    public void click(View v)
    {
        switch (v.getId()) {
            case R.id.action_prev:
                webview.goBack();
                break;
            case R.id.action_next:
                webview.goForward();
                break;
            case R.id.action_reload:
                webview.reload();
                break;
            default:
                break;
        }
    }

    private void init() {
        webview.setWebChromeClient(new MyWebChromeClient());
        webview.setWebViewClient(new MyWebViewClient());
        WebSettings webSettings = webview.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setDisplayZoomControls(false);

        webview.loadUrl(url);
    }

    class MyWebChromeClient extends WebChromeClient
    {
        @Override
        public void onProgressChanged(WebView view, int newProgress) {

            super.onProgressChanged(view, newProgress);
        }

        @Override
        public void onReceivedTitle(WebView view, String title) {
            super.onReceivedTitle(view, title);
            setTitle(title);
        }
    }
    class MyWebViewClient extends WebViewClient
    {
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            WebViewActivity.this.url = url;
            super.onPageStarted(view, url, favicon);
        }
        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
        }
    }

    @Override
    public void onBackPressed() {
        if(webview.canGoBack())
        {
            webview.goBack();
            return;
        }
        super.onBackPressed();
    }
}
