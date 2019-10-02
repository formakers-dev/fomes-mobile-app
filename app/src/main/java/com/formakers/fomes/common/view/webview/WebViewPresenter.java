package com.formakers.fomes.common.view.webview;

import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;

import com.formakers.fomes.common.util.Log;
import com.formakers.fomes.common.helper.FomesUrlHelper;

import javax.inject.Inject;

import static com.formakers.fomes.common.constant.FomesConstants.WebView.EXTRA_CONTENTS;
import static com.formakers.fomes.common.constant.FomesConstants.WebView.EXTRA_TITLE;

@WebViewDagger.Scope
public class WebViewPresenter implements WebViewConstract.Presenter {

    private static final String TAG = "WebViewPresenter";

    private FomesUrlHelper fomesUrlHelper;
    private WebViewConstract.View view;

    @Inject
    public WebViewPresenter(WebViewConstract.View view, FomesUrlHelper fomesUrlHelper) {
        this.view = view;
        this.fomesUrlHelper = fomesUrlHelper;
    }

    @Override
    public boolean isFromDeeplink(Uri uri) {
        Log.d(TAG, "isFromDeeplink) " + uri);
        return uri != null
                && uri.getScheme().equals("fomes")
                && uri.getHost().equals("web")
                && uri.getPath().equals("/internal");
    }

    @Override
    public Bundle getInterpretedDeeplinkBundle(Uri deeplinkUri) {
        String title = deeplinkUri.getQueryParameter("title");
        String contents = deeplinkUri.getQueryParameter("url");
        String appended = deeplinkUri.getQueryParameter("appendedUrl");

        contents = fomesUrlHelper.interpretUrlParams(contents);

        // TODO : 레거시코드. 크리티컬 릴리즈때 지워야한다
        if (!TextUtils.isEmpty(appended)) {
            // TODO : 아래를 유지해야하나 말아야하나 고민됨
            contents += appended;
        }

        Bundle bundle = new Bundle();
        bundle.putString(EXTRA_TITLE, title);
        bundle.putString(EXTRA_CONTENTS, contents);
        return bundle;
    }


}
