package com.formakers.fomes.analysis.presenter;

import com.formakers.fomes.analysis.contract.CurrentAnalysisReportContract;
import com.formakers.fomes.helper.AppUsageDataHelper;
import com.formakers.fomes.network.AppStatService;

import javax.inject.Inject;

import rx.Completable;

public class CurrentAnalysisReportPresenter implements CurrentAnalysisReportContract.Presenter {

    @Inject AppUsageDataHelper appUsageDataHelper;
    @Inject AppStatService appStatService;

    CurrentAnalysisReportContract.View view;

    public CurrentAnalysisReportPresenter(CurrentAnalysisReportContract.View view) {
        this.view = view;
        this.view.getApplicationComponent().inject(this);
    }

    public CurrentAnalysisReportPresenter(CurrentAnalysisReportContract.View view, AppUsageDataHelper appUsageDataHelper, AppStatService appStatService) {
        this.appUsageDataHelper = appUsageDataHelper;
        this.appStatService = appStatService;
    }

    @Override
    public Completable requestPostUsages() {
        return appStatService.sendAppUsages(appUsageDataHelper.getAppUsagesFor(AppUsageDataHelper.DEFAULT_APP_USAGE_DURATION_DAYS));
    }
}
