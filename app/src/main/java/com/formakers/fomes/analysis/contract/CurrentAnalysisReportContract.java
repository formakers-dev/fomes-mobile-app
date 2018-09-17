package com.formakers.fomes.analysis.contract;

import com.formakers.fomes.common.mvp.BaseView;
import com.formakers.fomes.dagger.ApplicationComponent;

import rx.Completable;

public interface CurrentAnalysisReportContract {
    interface Presenter {
        Completable requestPostUsages();
    }

    interface View extends BaseView<Presenter> {
        ApplicationComponent getApplicationComponent();
    }
}
