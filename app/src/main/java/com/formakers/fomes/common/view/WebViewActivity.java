package com.formakers.fomes.common.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.formakers.fomes.R;

import butterknife.BindView;

public class WebViewActivity extends FomesBaseActivity {

    public static final String EXTRA_TITLE = "EXTRA_TITLE";
    public static final String EXTRA_CONTENTS = "EXTRA_CONTENTS";

    @BindView(R.id.webview) WebView webView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.setContentView(R.layout.activity_webview);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        String title = getIntent().getStringExtra(EXTRA_TITLE);
        String contents = getIntent().getStringExtra(EXTRA_CONTENTS);

        if (TextUtils.isEmpty(contents)) {
            throw new IllegalArgumentException("There aren't any contents");
        }

        getSupportActionBar().setTitle(title);

        // TODO : 추후 필요에 따라 로딩 프로그래스바가 필요 할 수도 있겠음

        webView.setInitialScale(1);
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);

        if (contents.toLowerCase().startsWith("http")) {
            webView.loadUrl(contents);
        } else {
            webView.loadData(contents, "text/html; charset=UTF-8", null);
        }
    }
}
