package com.formakers.fomes.betatest;

import com.formakers.fomes.common.dagger.AnalyticsModule;
import com.formakers.fomes.common.helper.ImageLoader;

import javax.inject.Inject;

@FinishedBetaTestDetailDagger.Scope
class FinishedBetaTestDetailPresenter implements FinishedBetaTestDetailContract.Presenter {

    private FinishedBetaTestDetailContract.View view;
    private AnalyticsModule.Analytics analytics;
    private ImageLoader imageLoader;

    @Inject
    public FinishedBetaTestDetailPresenter(FinishedBetaTestDetailContract.View view,
                                           AnalyticsModule.Analytics analytics,
                                           ImageLoader imageLoader) {
        this.view = view;
        this.analytics = analytics;
        this.imageLoader = imageLoader;
    }

    @Override
    public AnalyticsModule.Analytics getAnalytics() {
        return analytics;
    }

    @Override
    public ImageLoader getImageLoader() {
        return imageLoader;
    }
}
