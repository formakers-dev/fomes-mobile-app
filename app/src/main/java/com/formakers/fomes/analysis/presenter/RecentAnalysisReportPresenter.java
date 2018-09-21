package com.formakers.fomes.analysis.presenter;

import android.util.Log;
import android.util.Pair;

import com.formakers.fomes.analysis.contract.RecentAnalysisReportContract;
import com.formakers.fomes.common.network.vo.RecentReport;
import com.formakers.fomes.common.network.vo.Usage;
import com.formakers.fomes.common.network.vo.UsageGroup;
import com.formakers.fomes.helper.AppUsageDataHelper;
import com.formakers.fomes.helper.SharedPreferencesHelper;
import com.formakers.fomes.model.CategoryUsage;
import com.formakers.fomes.common.network.AppStatService;
import com.formakers.fomes.model.User;

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

public class RecentAnalysisReportPresenter implements RecentAnalysisReportContract.Presenter {

    public static final String TAG = RecentAnalysisReportPresenter.class.getSimpleName();

    @Inject AppUsageDataHelper appUsageDataHelper;
    @Inject AppStatService appStatService;
    @Inject SharedPreferencesHelper sharedPreferencesHelper;

    User user;

    RecentAnalysisReportContract.View view;

    public RecentAnalysisReportPresenter(RecentAnalysisReportContract.View view) {
        this.view = view;
        this.view.getApplicationComponent().inject(this);
        this.user = new User().setUserId(sharedPreferencesHelper.getUserId()).setGender("female")
                .setBirthday(1992).setJob("IT종사자");
    }

    RecentAnalysisReportPresenter(RecentAnalysisReportContract.View view, AppUsageDataHelper appUsageDataHelper, AppStatService appStatService, User user) {
        this.view = view;
        this.appUsageDataHelper = appUsageDataHelper;
        this.appStatService = appStatService;
        this.user = user;
    }

    private Completable requestPostUsages() {
        return appStatService.sendAppUsages(appUsageDataHelper.getAppUsagesFor(AppUsageDataHelper.DEFAULT_APP_USAGE_DURATION_DAYS));
    }

    private Observable<RecentReport> requestRecentReport() {
        // TODO : categoryIds Constants로 분리 필요
        return appStatService.requestRecentReport("GAME", user).observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Completable loading() {
        return Completable.concat(
                this.requestPostUsages()
                , Completable.create(emitter -> requestRecentReport().map(recentReport -> {
                    for (UsageGroup usageGroup : recentReport.getUsages()) {
                        Usage[] appUsages = new Usage[usageGroup.getAppUsages().size()];
                        usageGroup.getAppUsages().toArray(appUsages);

                        Usage[] categoryUsages = new Usage[usageGroup.getCategoryUsages().size()];
                        usageGroup.getCategoryUsages().toArray(categoryUsages);

                        Usage[] developerUsages = new Usage[usageGroup.getDeveloperUsages().size()];
                        usageGroup.getDeveloperUsages().toArray(developerUsages);

                        Arrays.sort(appUsages, (o1, o2) -> o1.getTotalUsedTime() > o2.getTotalUsedTime() ? -1 : 1);
                        Arrays.sort(categoryUsages, (o1, o2) -> o1.getTotalUsedTime() > o2.getTotalUsedTime() ? -1 : 1);
                        Arrays.sort(developerUsages, (o1, o2) -> o1.getTotalUsedTime() > o2.getTotalUsedTime() ? -1 : 1);

                        usageGroup.setAppUsages(Arrays.asList(appUsages));
                        usageGroup.setCategoryUsages(Arrays.asList(categoryUsages));
                        usageGroup.setDeveloperUsages(Arrays.asList(developerUsages));
                    }

                    return recentReport;
                }).subscribe(recentReport -> {
                    Map<Integer, UsageGroup> usageGroupMap = new HashMap<>();

                    for (UsageGroup usageGroup : recentReport.getUsages()) {
                        usageGroupMap.put(usageGroup.getGroupType(), usageGroup);
                    }

                    this.view.bindMyGenreViews(usageGroupMap.get(UsageGroup.TYPE_MINE).getCategoryUsages());
                    this.view.bindPeopleGenreViews(usageGroupMap.get(UsageGroup.TYPE_AGE | UsageGroup.TYPE_GENDER).getCategoryUsages()
                            , usageGroupMap.get(UsageGroup.TYPE_JOB).getCategoryUsages());

                    emitter.onCompleted();
                }, e -> emitter.onError(e)))
                .doOnError(e -> Log.e(TAG, e.toString() + "\n stacktrace=" + Arrays.asList(e.getStackTrace())))
                .subscribeOn(Schedulers.io()));
    }

    @Override
    public List<Pair<Usage, Integer>> getPercentage(List<Usage> categoryUsages, int start, int end) {
        float total = 0;
        for (Usage categoryUsage : categoryUsages) {
            total += categoryUsage.getTotalUsedTime();
        }

        List<Pair<Usage, Integer>> result = new ArrayList<>();
        for (Usage categoryUsage : categoryUsages.subList(start, categoryUsages.size() > end ? end : categoryUsages.size())) {
            result.add(new Pair<>(categoryUsage, Math.round(categoryUsage.getTotalUsedTime() / total * 100)));
        }

        return result;
    }
}
