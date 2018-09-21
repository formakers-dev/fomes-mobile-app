package com.formakers.fomes.analysis.contract;

import android.util.Pair;

import com.formakers.fomes.common.mvp.BaseView;
import com.formakers.fomes.common.network.vo.Usage;
import com.formakers.fomes.dagger.ApplicationComponent;

import java.util.List;

import rx.Completable;

public interface RecentAnalysisReportContract {
    interface Presenter {
        Completable loading();
        List<Pair<Usage,Integer>> getPercentage(List<Usage> categoryUsages, int start, int end);
    }

    interface View extends BaseView<Presenter> {
        ApplicationComponent getApplicationComponent();
        void bindMyGenreViews(List<Usage> categoryUsages);
        void bindPeopleGenreViews(List<Usage> genderAgeUsages, List<Usage> jobUsages);
    }
}