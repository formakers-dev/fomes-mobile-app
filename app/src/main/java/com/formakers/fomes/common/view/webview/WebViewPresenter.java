package com.formakers.fomes.common.view.webview;

import android.net.Uri;
import android.text.TextUtils;

import com.formakers.fomes.common.constant.FomesConstants;
import com.formakers.fomes.common.helper.FomesUrlHelper;
import com.formakers.fomes.common.network.PostService;
import com.formakers.fomes.common.util.Log;

import javax.inject.Inject;

import rx.android.schedulers.AndroidSchedulers;

@WebViewDagger.Scope
public class WebViewPresenter implements WebViewConstract.Presenter {

    private static final String TAG = "WebViewPresenter";
    private static final String SCHEME_HTTP = "http";
    public static final String HTML_MIMETYPE = "text/html; charset=UTF-8";

    private FomesUrlHelper fomesUrlHelper;
    private WebViewConstract.View view;
    private PostService postService;

    @Inject
    public WebViewPresenter(WebViewConstract.View view, FomesUrlHelper fomesUrlHelper, PostService postService) {
        this.view = view;
        this.fomesUrlHelper = fomesUrlHelper;
        this.postService = postService;
    }

    @Override
    public boolean isFromDeeplink(Uri uri) {
        Log.d(TAG, "isFromDeeplink) " + uri);
        return uri != null && FomesConstants.DeepLink.SCHEME.equals(uri.getScheme());
    }

    @Override
    public void interpreteDeepLink(Uri deeplinkUri) {
        String host = deeplinkUri.getHost();
        String path = deeplinkUri.getPath();

        if (FomesConstants.DeepLink.HOST_WEB.equals(host)) {
            String title = deeplinkUri.getQueryParameter(FomesConstants.DeepLink.QUERY_PARAM_TITLE);
            String contents = deeplinkUri.getQueryParameter(FomesConstants.DeepLink.QUERY_PARAM_URL);

            if (FomesConstants.DeepLink.PATH_EXTERNAL.equals(path)) {
                contents = fomesUrlHelper.interpretUrlParams(contents);
                this.view.throwDeepLink(Uri.parse(contents));
            } else {
                this.view.initialize(title, contents);
            }
        } else if (FomesConstants.DeepLink.HOST_POST.equals(host)
                && FomesConstants.DeepLink.PATH_DETAIL.equals(path)) {
            String id = deeplinkUri.getQueryParameter(FomesConstants.DeepLink.QUERY_PARAM_ID);

            this.view.getCompositeSubscription().add(
                    postService.getPromotion(id)
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(post -> {
                                if (!TextUtils.isEmpty(post.getDeeplink())) {
                                    this.view.throwDeepLink(Uri.parse(post.getDeeplink()));
                                } else {
                                    this.view.initialize(post.getTitle(), post.getContents());
                                }
                            }, e -> Log.e(TAG, "postService getPromotion onError= " + e))
            );
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
