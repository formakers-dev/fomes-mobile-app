package com.formakers.fomes.common.view.webview;

import android.net.Uri;

import com.formakers.fomes.common.mvp.BaseView;

public interface WebViewConstract {
    interface Presenter {
        boolean isFromDeeplink(Uri uri);
        void interpreteDeepLink(Uri deeplinkUri);
        void loadContents(String contents);
    }

    interface View extends BaseView<Presenter> {
        void initialize(String title, String contents);

        void throwDeepLink(Uri deeplinkUri);
        void loadUrl(String url);
        void loadHtml(String html);
    }
}
