package com.formakers.fomes.analysis.contract;

import android.util.Pair;

import com.formakers.fomes.common.mvp.BaseView;
import com.formakers.fomes.dagger.ApplicationComponent;
import com.formakers.fomes.model.CategoryUsage;

import java.util.List;

import rx.Completable;

public interface CurrentAnalysisReportContract {
    interface Presenter {
        Completable loading();
        List<Pair<CategoryUsage,Integer>> getPercentage(List<CategoryUsage> categoryUsages, int start, int end);
    }

    interface View extends BaseView<Presenter> {
        ApplicationComponent getApplicationComponent();
        void bindMyGenreViews(List<CategoryUsage> genres);
    }
}
