package com.formakers.fomes.common.view.webview;

import android.app.Activity;
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

import com.formakers.fomes.FomesApplication;
import com.formakers.fomes.R;
import com.formakers.fomes.common.view.FomesNoticeDialog;
import com.formakers.fomes.common.constant.FomesConstants;
import com.formakers.fomes.common.util.Log;
import com.formakers.fomes.common.view.DeeplinkActivity;
import com.formakers.fomes.common.view.FomesBaseActivity;

import java.net.URISyntaxException;

import javax.inject.Inject;

import butterknife.BindView;
import rx.subscriptions.CompositeSubscription;

import static com.formakers.fomes.common.constant.FomesConstants.WebView.EXTRA_CONTENTS;
import static com.formakers.fomes.common.constant.FomesConstants.WebView.EXTRA_IS_PREVENT_BACK_PRESSED;
import static com.formakers.fomes.common.constant.FomesConstants.WebView.EXTRA_TITLE;

public class WebViewActivity extends FomesBaseActivity implements WebViewConstract.View {

    public static final String TAG = "WebViewActivity";

    private static final int REQUEST_CODE_FILE_CHOOSER = 1001;

    @BindView(R.id.webview) WebView webView;
    @BindView(R.id.loading_bar) ProgressBar loadingBar;

    @Inject WebViewConstract.Presenter presenter;

    private boolean isDoubleCheckBackPressed = false;   // 임시변수 - 설문용 액티비티를 따로 빼서 리팩토링 할 예정
    private ValueCallback<Uri[]> selectedFilePathCallback;

    private CompositeSubscription compositeSubscription = new CompositeSubscription();

    @Override
    public void setPresenter(WebViewConstract.Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.setContentView(R.layout.activity_webview);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        DaggerWebViewDagger_Component.builder()
                .applicationComponent(FomesApplication.get(this).getComponent())
                .module(new WebViewDagger.Module(this))
                .build()
                .inject(this);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        Intent intent = getIntent();
        Uri uri = intent.getData();

        if (presenter.isFromDeeplink(uri)) {
            presenter.interpreteDeepLink(uri);
        } else {
            String title = getIntent().getStringExtra(EXTRA_TITLE);
            String contents = getIntent().getStringExtra(EXTRA_CONTENTS);
            isDoubleCheckBackPressed = getIntent().getBooleanExtra(EXTRA_IS_PREVENT_BACK_PRESSED, false);

            initialize(title, contents);
        }
    }

    @Override
    public void initialize(String title, String contents) {
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

        this.presenter.loadContents(contents);
    }

    @Override
    protected boolean isDifferentBetweenUpAndBack() {
        setResultForSurvey();
        return true;
    }

    private void goBack() {
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
    public void onBackPressed() {
        setResultForSurvey();

        if (isDoubleCheckBackPressed) {
            FomesNoticeDialog fomesNoticeDialog = new FomesNoticeDialog();

            Bundle bundle = new Bundle();
            bundle.putString(FomesNoticeDialog.EXTRA_TITLE, getString(R.string.survey_back_dialog_title));
            bundle.putString(FomesNoticeDialog.EXTRA_SUBTITLE, getString(R.string.survey_back_dialog_contents));
            bundle.putString(FomesNoticeDialog.EXTRA_DESCRIPTION, getString(R.string.survey_back_dialog_guide));

            fomesNoticeDialog.setArguments(bundle);
            fomesNoticeDialog.setPositiveButton(getString(R.string.survey_back_dialog_back), v -> {
                goBack();
            });
            fomesNoticeDialog.setNeutralButton(getString(R.string.survey_back_dialog_finish), v -> {
                finish();
            });
            fomesNoticeDialog.setNegativeButton(getString(R.string.survey_back_dialog_cancel), v -> {
                fomesNoticeDialog.dismiss();
            });
            fomesNoticeDialog.show(this.getSupportFragmentManager(), "Test");
        } else {
            goBack();
        }
    }

    // survey 인 경우에만 해당되는 로직으로서 리팩토링시 분리될 필요있음
    private void setResultForSurvey() {
        Intent intent = new Intent();
        intent.putExtra(FomesConstants.WebView.EXTRA_MISSION_ID, getIntent().getStringExtra(FomesConstants.WebView.EXTRA_MISSION_ID));
        setResult(Activity.RESULT_OK, intent);

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

    @Override
    public void throwDeepLink(Uri deeplinkUri) {
        Intent externalIntent = new Intent(Intent.ACTION_VIEW, deeplinkUri);
        startActivity(externalIntent);
        finish();
    }

    @Override
    public void loadUrl(String url) {
        webView.loadUrl(url);
    }

    @Override
    public void loadHtml(String html) {
        webView.loadData(html, WebViewPresenter.HTML_MIMETYPE, null);
    }

    @Override
    public CompositeSubscription getCompositeSubscription() {
        return compositeSubscription;
    }

    private class FomesWebChromeClient extends WebChromeClient {
        @Override
        public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
            if (selectedFilePathCallback != null) {
                selectedFilePathCallback.onReceiveValue(null);
                selectedFilePathCallback = null;
            }

            selectedFilePathCallback = filePathCallback;

            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/* video/*");
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
                finish();
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
