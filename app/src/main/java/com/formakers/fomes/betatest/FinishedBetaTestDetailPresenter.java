package com.formakers.fomes.betatest;

import com.formakers.fomes.common.dagger.AnalyticsModule;
import com.formakers.fomes.common.helper.ImageLoader;
import com.formakers.fomes.common.network.BetaTestService;
import com.formakers.fomes.common.network.vo.AwardRecord;
import com.formakers.fomes.common.util.Log;

import javax.inject.Inject;

import retrofit2.adapter.rxjava.HttpException;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

@FinishedBetaTestDetailDagger.Scope
class FinishedBetaTestDetailPresenter implements FinishedBetaTestDetailContract.Presenter {

    private static final String TAG = "FinishedBetaTestDetailPresenter";

    private FinishedBetaTestDetailContract.View view;
    private AnalyticsModule.Analytics analytics;
    private ImageLoader imageLoader;
    private BetaTestService betaTestService;

    @Inject
    public FinishedBetaTestDetailPresenter(FinishedBetaTestDetailContract.View view,
                                           AnalyticsModule.Analytics analytics,
                                           ImageLoader imageLoader,
                                           BetaTestService betaTestService) {
        this.view = view;
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
    public void requestAwardRecordOfBest(String betaTestId) {
        this.betaTestService.getAwardRecords(betaTestId)
                .toObservable()
                .flatMap(Observable::from)
                .filter(awardRecord -> AwardRecord.TYPE_BEST.equals(awardRecord.getType()))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(awardRecord -> this.view.bindAwards(awardRecord));
    }

    @Override
    public void requestEpilogue(String betaTestId) {
        this.betaTestService.getEpilogue(betaTestId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(epilogue -> this.view.bindEpilogue(epilogue),
                        e -> {
                            Log.e(TAG, String.valueOf(e));
                            if (e instanceof HttpException) {
                                HttpException httpException = (HttpException) e;
                                if (httpException.code() == 404) {
                                    this.view.disableEpilogue();
                                }
                            }
                        });
    }
}
