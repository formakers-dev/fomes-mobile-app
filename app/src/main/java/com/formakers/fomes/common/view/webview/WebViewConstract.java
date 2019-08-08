package com.formakers.fomes.common.view.webview;

import android.net.Uri;
import android.os.Bundle;

import com.formakers.fomes.common.mvp.BaseView;

public interface WebViewConstract {
    interface Presenter {
        boolean isFromDeeplink(Uri uri);
        Bundle getInterpretedDeeplinkBundle(Uri deeplinkUri);
    }

    interface View extends BaseView<Presenter> {

    }
}
