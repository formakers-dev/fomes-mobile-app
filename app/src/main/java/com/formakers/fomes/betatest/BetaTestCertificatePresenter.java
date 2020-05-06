package com.formakers.fomes.betatest;

import com.formakers.fomes.common.dagger.AnalyticsModule;
import com.formakers.fomes.common.helper.ImageLoader;
import com.formakers.fomes.common.network.BetaTestService;
import com.formakers.fomes.common.util.Log;

import javax.inject.Inject;
import javax.inject.Named;

import rx.Single;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

@BetaTestCertificateDagger.Scope
class BetaTestCertificatePresenter implements BetaTestCertificateContract.Presenter {

    private static final String TAG = "BetaTestCertificatePresenter";

    private BetaTestCertificateContract.View view;
    private Single<String> userNickName;
    private AnalyticsModule.Analytics analytics;
    private ImageLoader imageLoader;
    private BetaTestService betaTestService;

    @Inject
    public BetaTestCertificatePresenter(BetaTestCertificateContract.View view,
                                        @Named("userNickName") Single<String> userNickName,
                                        AnalyticsModule.Analytics analytics,
                                        ImageLoader imageLoader,
                                        BetaTestService betaTestService) {
        this.view = view;
        this.userNickName = userNickName;
        this.analytics = analytics;
        this.imageLoader = imageLoader;
        this.betaTestService = betaTestService;
    }

    @Override
    public AnalyticsModule.Analytics getAnalytics() {
        return analytics;
    }

    @Override
    public ImageLoader getImageLoader() {
        return imageLoader;
    }

    @Override
    public void requestBetaTestDetail(String betaTestId) {
        this.betaTestService.getDetailBetaTest(betaTestId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(betaTest -> this.view.bindBetaTest(betaTest),
                        e -> Log.e(TAG, String.valueOf(e)));
    }

    @Override
    public void requestUserNickName() {
        this.userNickName
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(nickName -> this.view.bindUserNickName(nickName),
                        e -> Log.e(TAG, String.valueOf(e)));
    }
}
