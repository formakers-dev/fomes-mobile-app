package com.formakers.fomes.common.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.formakers.fomes.R;

import butterknife.BindView;

public class WebViewActivity extends FomesBaseActivity {

    public static final String EXTRA_LINK_URL = "EXTRA_LINK_URL";

    @BindView(R.id.webview) WebView webView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.setContentView(R.layout.activity_webview);

        getSupportActionBar().setTitle("이벤트");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        String url = getIntent().getStringExtra(EXTRA_LINK_URL);

        webView.setInitialScale(1);
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);

        if (url.contains("http")) {
            webView.loadUrl(url);
        } else {
            webView.loadData(url, "text/html; charset=UTF-8", null);
        }
    }
}
