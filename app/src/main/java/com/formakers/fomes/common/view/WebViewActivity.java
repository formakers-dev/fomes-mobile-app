package com.formakers.fomes.common.view;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.formakers.fomes.R;
import com.formakers.fomes.common.util.Log;

import java.net.URISyntaxException;

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

        webView.setInitialScale(1);
        webView.setWebViewClient(new FomesWebViewClient());

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

    private class FomesWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {

            String url = String.valueOf(request.getUrl());

            // 예외 상황 1. URL이 없을 때
            if (TextUtils.isEmpty(url)) {
                Log.e(TAG, "shouldOverrideUrlLoading) Invalid url");
                return super.shouldOverrideUrlLoading(view, url);
            }

            Uri uri = Uri.parse(url);
            Log.d(TAG, "shouldOverrideUrlLoading) uri=" + uri);
            String scheme = uri.getScheme();

            // 예외 상황 3. 인텐트 일 때
            if ("intent".equals(scheme)) {
                Log.d(TAG, "shouldOverrideUrlLoading) intent case");

                Intent intent;
                try {
                    intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME);
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                    return super.shouldOverrideUrlLoading(view, request);
                }

                if (view.getContext().getPackageManager().queryIntentActivities(intent, 0).size() > 0) {
                    startActivity(intent);
                } else {
                    Log.e(TAG, "shouldOverrideUrlLoading) it isn't installed. it need to install from market (pkg= " + intent.getPackage() + ")");

                    Intent marketIntent = new Intent(Intent.ACTION_VIEW);
                    marketIntent.setData(Uri.parse("market://details?id=" + intent.getPackage()));
                    try {
                        startActivity(marketIntent);
                    } catch(ActivityNotFoundException e) {
                        Log.e(TAG, "shouldOverrideUrlLoading. " + e);
                    }
                }
                return true;
            }

            return super.shouldOverrideUrlLoading(view, request);
        }

        // TODO : 추후 필요에 따라 로딩 프로그래스바가 필요 할 수도 있겠음

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
        }
    }
}
