package com.formakers.fomes.common.network;

import com.formakers.fomes.common.network.api.RecommendAPI;
import com.formakers.fomes.common.network.vo.RecommendApp;
import com.formakers.fomes.helper.APIHelper;
import com.formakers.fomes.helper.SharedPreferencesHelper;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Observable;
import rx.schedulers.Schedulers;

@Singleton
public class RecommendService extends AbstractService {
    private static final String TAG = "RecommendService";

    private final RecommendAPI recommendAPI;
    private final SharedPreferencesHelper SharedPreferencesHelper;
    private final APIHelper APIHelper;

    @Inject
    public RecommendService(RecommendAPI recommendAPI, SharedPreferencesHelper SharedPreferencesHelper, APIHelper APIHelper) {
        this.recommendAPI = recommendAPI;
        this.SharedPreferencesHelper = SharedPreferencesHelper;
        this.APIHelper = APIHelper;
    }

    public Observable<List<RecommendApp>> requestRecommendApps(String categoryId, int page) {
        return  Observable.defer(() -> recommendAPI.getRecommendApps(SharedPreferencesHelper.getAccessToken(), categoryId, page))
                .doOnError(this::logError)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .compose(APIHelper.refreshExpiredToken());
    }

    @Override
    protected String getTag() {
        return TAG;
    }
}
