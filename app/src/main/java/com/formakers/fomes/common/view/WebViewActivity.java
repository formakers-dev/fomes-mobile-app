package com.formakers.fomes.common.view;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.formakers.fomes.R;
import com.formakers.fomes.common.util.Log;

import java.net.URISyntaxException;

import butterknife.BindView;

public class WebViewActivity extends FomesBaseActivity {

    public static final String TAG = "WebViewActivity";

    public static final String EXTRA_TITLE = "EXTRA_TITLE";
    public static final String EXTRA_CONTENTS = "EXTRA_CONTENTS";

    private static final int REQUEST_CODE_FILE_CHOOSER = 1001;

    @BindView(R.id.webview) WebView webView;
    @BindView(R.id.loading_bar) ProgressBar loadingBar;

    // TODO : MVP 로 분리해라
//    @Inject UserDAO userDAO;

    private ValueCallback<Uri[]> selectedFilePathCallback;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.setContentView(R.layout.activity_webview);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected boolean isFromDeeplink() {
        Intent intent = getIntent();
        Uri deeplinkUri = intent.getData();
        Log.d(TAG, String.valueOf(deeplinkUri));

        return deeplinkUri != null
                && deeplinkUri.getScheme().equals("fomes")
                && deeplinkUri.getHost().equals("web")
                && deeplinkUri.getPath().equals("/internal");
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        Intent intent = getIntent();
        String title;
        String contents;

        // TODO : 커밋후 리턴타입 고민하기
        if (isFromDeeplink()) {
            Uri deeplinkUri = intent.getData();

            title = deeplinkUri.getQueryParameter("title");
            contents = deeplinkUri.getQueryParameter("url");
            String appended = deeplinkUri.getQueryParameter("appendedUrl");

            if (!TextUtils.isEmpty(appended)) {
                // appendedUrl 파람의 예약어는 여기에 정의하자
                contents += appended.replace("{email}", userDAO2.getUserInfo().toBlocking().value().getEmail());
            }
        } else {
            title = getIntent().getStringExtra(EXTRA_TITLE);
            contents = getIntent().getStringExtra(EXTRA_CONTENTS);
        }

        if (TextUtils.isEmpty(contents)) {
            throw new IllegalArgumentException("There aren't any contents");
        }

        getSupportActionBar().setTitle(title);

        webView.setInitialScale(1);
        webView.setWebViewClient(new FomesWebViewClient());

        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);

        webView.setWebChromeClient(new FomesWebChromeClient());

        if (contents.toLowerCase().startsWith("http")) {
            webView.loadUrl(contents);
        } else {
            webView.loadData(contents, "text/html; charset=UTF-8", null);
        }
    }

    @Override
    protected boolean isDifferentBetweenUpAndBack() {
        return true;
    }

    @Override
    public void onBackPressed() {
        try {
            if (webView.canGoBack()) {
                webView.goBack();
            } else {
                super.onBackPressed();
            }
        } catch (Exception e) {
            Log.e(TAG, "Exception) " + e.getClass() + "\nmessage=" + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        Log.d(TAG, "onActivityResult(" + requestCode + ", " + resultCode + ", " + data + ")");

        if (requestCode == REQUEST_CODE_FILE_CHOOSER) {
            selectedFilePathCallback.onReceiveValue(WebChromeClient.FileChooserParams.parseResult(resultCode, data));
            selectedFilePathCallback = null;
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private class FomesWebChromeClient extends WebChromeClient {
        @Override
        public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
            if (selectedFilePathCallback != null) {
                selectedFilePathCallback.onReceiveValue(null);
                selectedFilePathCallback = null;
            }

            selectedFilePathCallback = filePathCallback;
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("image/*");
            startActivityForResult(Intent.createChooser(intent, "File Chooser"), REQUEST_CODE_FILE_CHOOSER);


            return true;
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

            // 예외 상황 2. 포메스 딥링크 일 때
            if (DeeplinkActivity.SCHEME_FOMES.equals(scheme)) {
                Log.d(TAG, "shouldOverrideUrlLoading) deeplink case");

//                Intent intent = new Intent(view.getContext(), DeeplinkActivity.class);
//                intent.setData(uri);
//                startActivity(intent);

                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
                return true;
            }

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

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            Log.v(TAG, "onPageStarted=" + url);
            super.onPageStarted(view, url, favicon);

            if (loadingBar != null) {
                loadingBar.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            Log.v(TAG, "onPageFinished=" + url);
            super.onPageFinished(view, url);

            if (loadingBar != null) {
                loadingBar.setVisibility(View.GONE);
            }
        }


        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            Log.e(TAG, "onReceivedError on " + failingUrl);
            Log.e(TAG, "onReceivedError [" + errorCode + "]" + description);
            super.onReceivedError(view, errorCode, description, failingUrl);

            if (loadingBar != null) {
                loadingBar.setVisibility(View.GONE);
            }
        }

        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
            Log.e(TAG, "onReceivedError [" + error.getErrorCode() + "]" + error.getDescription());
            super.onReceivedError(view, request, error);

            if (loadingBar != null) {
                loadingBar.setVisibility(View.GONE);
            }
        }

        @Override
        public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
            Log.e(TAG, "onReceivedHttpError on [" + request.getMethod() + "] " +request.getUrl());
            Log.e(TAG, "onReceivedHttpError [" + errorResponse.getStatusCode() + "]" + errorResponse.getReasonPhrase());
            super.onReceivedHttpError(view, request, errorResponse);

            if (loadingBar != null) {
                loadingBar.setVisibility(View.GONE);
            }
        }
    }
}
