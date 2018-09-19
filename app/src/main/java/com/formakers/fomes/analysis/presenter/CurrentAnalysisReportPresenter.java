package com.formakers.fomes.analysis.presenter;

import android.util.Log;
import android.util.Pair;

import com.formakers.fomes.analysis.contract.CurrentAnalysisReportContract;
import com.formakers.fomes.helper.AppUsageDataHelper;
import com.formakers.fomes.model.CategoryUsage;
import com.formakers.fomes.common.network.AppStatService;
import com.formakers.fomes.common.network.api.StatAPI;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import rx.Completable;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class CurrentAnalysisReportPresenter implements CurrentAnalysisReportContract.Presenter {

    public static final String TAG = CurrentAnalysisReportPresenter.class.getSimpleName();

    @Inject AppUsageDataHelper appUsageDataHelper;
    @Inject AppStatService appStatService;

    CurrentAnalysisReportContract.View view;

    public CurrentAnalysisReportPresenter(CurrentAnalysisReportContract.View view) {
        this.view = view;
        this.view.getApplicationComponent().inject(this);
    }

    CurrentAnalysisReportPresenter(CurrentAnalysisReportContract.View view, AppUsageDataHelper appUsageDataHelper, AppStatService appStatService) {
        this.view = view;
        this.appUsageDataHelper = appUsageDataHelper;
        this.appStatService = appStatService;
    }

    private Completable requestPostUsages() {
        return appStatService.sendAppUsages(appUsageDataHelper.getAppUsagesFor(AppUsageDataHelper.DEFAULT_APP_USAGE_DURATION_DAYS));
    }

    private Observable<List<CategoryUsage>> requestCategoryUsage() {
        return appStatService.requestCategoryUsage().observeOn(AndroidSchedulers.mainThread());
    }

    private Observable<Map<String, List<CategoryUsage>>> requestPeopleCategoryUsage() {
        Observable<Pair<String, List<CategoryUsage>>> genderAge = appStatService.requestPeopleCategoryUsage(StatAPI.PeopleGroupFilter.GENDER_AND_AGE).map(categoryUsages -> new Pair(StatAPI.PeopleGroupFilter.GENDER_AND_AGE, categoryUsages));
        Observable<Pair<String, List<CategoryUsage>>> job = appStatService.requestPeopleCategoryUsage(StatAPI.PeopleGroupFilter.JOB).map(categoryUsages -> new Pair(StatAPI.PeopleGroupFilter.JOB, categoryUsages));

        return Observable.zip(genderAge, job, (o1, o2) -> {
            Map<String, List<CategoryUsage>> map = new HashMap<>();
            map.put(o1.first, o1.second);
            map.put(o2.first, o2.second);
            return map;
        }).subscribeOn(Schedulers.io());
    }

    @Override
    public Completable loading() {
        return Completable.concat(
                this.requestPostUsages()
                , Observable.zip(this.requestCategoryUsage()
                        , this.requestPeopleCategoryUsage()
                        , (genres, peopleGenres) -> {
                    this.view.bindMyGenreViews(genres);
                    this.view.bindPeopleGenreViews(peopleGenres);
                    return true;
                }).toCompletable())
                .doOnError(e -> Log.e(TAG, e.toString() + "\n stacktrace=" + Arrays.asList(e.getStackTrace())))
                .subscribeOn(Schedulers.io());
    }

    @Override
    public List<Pair<CategoryUsage, Integer>> getPercentage(List<CategoryUsage> categoryUsages, int start, int end) {
        float total = 0;
        for (CategoryUsage categoryUsage : categoryUsages) {
            total += categoryUsage.getTotalUsedTime();
        }

        List<Pair<CategoryUsage, Integer>> result = new ArrayList<>();
        for (CategoryUsage categoryUsage : categoryUsages.subList(start, categoryUsages.size() > end ? end : categoryUsages.size())) {
            result.add(new Pair<>(categoryUsage, Math.round(categoryUsage.getTotalUsedTime() / total * 100)));
        }

        return result;
    }
}
