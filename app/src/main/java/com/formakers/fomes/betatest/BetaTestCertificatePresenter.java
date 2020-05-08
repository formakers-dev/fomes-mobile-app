package com.formakers.fomes.betatest;

import android.util.Pair;

import com.formakers.fomes.common.dagger.AnalyticsModule;
import com.formakers.fomes.common.helper.ImageLoader;
import com.formakers.fomes.common.network.BetaTestService;
import com.formakers.fomes.common.network.vo.AwardRecord;
import com.formakers.fomes.common.network.vo.BetaTest;
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
                .zipWith(this.betaTestService.getAwardRecord(betaTestId)
                        .onErrorReturn(throwable -> null), Pair::new)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(pair -> {
                        BetaTest betaTest = pair.first;
                        AwardRecord awardRecord = pair.second;

                        if (betaTest.isCompleted()) {
                            this.view.bindBetaTestDetail(betaTest);
                            this.view.bindCertificate(betaTest, awardRecord);
                        } else {
                            //TODO : 잘못된 접근 - 참여완료기록 없음
                        }
                    },
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
