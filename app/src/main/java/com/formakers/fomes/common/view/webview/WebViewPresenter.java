package com.formakers.fomes.common.view.webview;

import android.net.Uri;

import com.formakers.fomes.common.constant.FomesConstants;
import com.formakers.fomes.common.helper.FomesUrlHelper;
import com.formakers.fomes.common.util.Log;

import javax.inject.Inject;

@WebViewDagger.Scope
public class WebViewPresenter implements WebViewConstract.Presenter {

    private static final String TAG = "WebViewPresenter";
    private static final String SCHEME_HTTP = "http";
    public static final String HTML_MIMETYPE = "text/html; charset=UTF-8";

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

    @Override
    public void interpreteDeepLink(Uri deeplinkUri) {
        String host = deeplinkUri.getHost();

        if (FomesConstants.DeepLink.HOST_WEB.equals(host)) {
            String title = deeplinkUri.getQueryParameter(FomesConstants.DeepLink.QUERY_PARAM_TITLE);
            String contents = deeplinkUri.getQueryParameter(FomesConstants.DeepLink.QUERY_PARAM_URL);

            if (FomesConstants.DeepLink.PATH_EXTERNAL.equals(deeplinkUri.getPath())) {
                contents = fomesUrlHelper.interpretUrlParams(contents);
                this.view.throwDeepLink(Uri.parse(contents));
            } else {
                this.view.initialize(title, contents);
            }
        } else {
            throw new IllegalArgumentException("This DeepLink is not associated with WebView !!!! (" + deeplinkUri + ")");
        }
    }

    @Override
    public void loadContents(String contents) {
        if (contents.toLowerCase().startsWith(SCHEME_HTTP)) {
            this.view.loadUrl(fomesUrlHelper.interpretUrlParams(contents));
        } else {
            this.view.loadHtml(contents);
        }
    }
}
