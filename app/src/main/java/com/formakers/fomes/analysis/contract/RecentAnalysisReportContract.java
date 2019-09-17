package com.formakers.fomes.analysis.contract;

import android.util.Pair;

import com.bumptech.glide.RequestManager;
import com.formakers.fomes.common.mvp.BaseView;
import com.formakers.fomes.common.network.vo.Rank;
import com.formakers.fomes.common.network.vo.Usage;
import com.formakers.fomes.model.User;

import java.util.List;

import rx.Completable;

public interface RecentAnalysisReportContract {
    interface Presenter {
        Completable loading();
        List<Pair<Usage,Integer>> getPercentage(List<Usage> categoryUsages, int start, int end);
        User getUserInfo();
        float getHour(long milliseconds);
        RequestManager getImageLoader();
    }

    interface View extends BaseView<Presenter> {
        void bindMyGenreViews(List<Usage> categoryUsages, String nickName);
        void bindPeopleGenreViews(List<Usage> genderAgeCategoryUsages, List<Usage> jobCategoryUsages);
        void bindRankingViews(List<Rank> totalUsedTimeRank, long totalUserCount);
        void bindFavoriteDeveloperViews(List<Usage> myDeveloperUsages, List<Usage> genderAgeDeveloperUsages, List<Usage> jobDeveloperUsages);
        void bindMyGames(List<Usage> myAppUsages);
        void bindPeopleGamesViews(List<Usage> genderAgeAppUsages, List<Usage> jobAppUsages);
    }
}
