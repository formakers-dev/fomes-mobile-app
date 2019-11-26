package com.formakers.fomes.common.view.webview;

import android.net.Uri;
import android.text.TextUtils;

import com.formakers.fomes.common.constant.FomesConstants;
import com.formakers.fomes.common.helper.FomesUrlHelper;
import com.formakers.fomes.common.util.Log;

import javax.inject.Inject;

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
                && FomesConstants.DeepLink.SCHEME.equals(uri.getScheme())
                && FomesConstants.DeepLink.HOST_WEB.equals(uri.getHost());
    }

    public void interpreteDeepLink(Uri deeplinkUri) {
        String host = deeplinkUri.getHost();

        if (FomesConstants.DeepLink.HOST_WEB.equals(host)) {
            String title = deeplinkUri.getQueryParameter(FomesConstants.DeepLink.QUERY_PARAM_TITLE);
            String contents = deeplinkUri.getQueryParameter(FomesConstants.DeepLink.QUERY_PARAM_URL);
            String appended = deeplinkUri.getQueryParameter(FomesConstants.DeepLink.QUERY_PARAM_APPENDED_URL);

            contents = fomesUrlHelper.interpretUrlParams(contents);

            // TODO : 레거시코드. 크리티컬 릴리즈때 지워야한다
            if (!TextUtils.isEmpty(appended)) {
                // TODO : 아래를 유지해야하나 말아야하나 고민됨
                contents += appended;
            }

            if (FomesConstants.DeepLink.PATH_EXTERNAL.equals(deeplinkUri.getPath())) {
                this.view.throwDeepLink(Uri.parse(contents));
            } else {
                this.view.initialize(title, contents);
            }
        } else {
            throw new IllegalArgumentException("This DeepLink is not associated with WebView !!!! (" + deeplinkUri + ")");
        }
    }
}
