package com.formakers.fomes.common.view.webview;

import android.net.Uri;

import com.formakers.fomes.common.mvp.BaseView;

public interface WebViewConstract {
    interface Presenter {
        boolean isFromDeeplink(Uri uri);
        void interpreteDeepLink(Uri deeplinkUri);
    }

    interface View extends BaseView<Presenter> {
        void initialize(String title, String contents);

        void throwDeepLink(Uri deeplinkUri);
    }
}
