package com.formakers.fomes.main.presenter;

import com.formakers.fomes.common.network.RecommendService;
import com.formakers.fomes.common.network.vo.RecommendApp;
import com.formakers.fomes.main.contract.RecommendContract;
import com.formakers.fomes.main.contract.RecommendListAdapterContract;

import java.util.List;

import javax.inject.Inject;

import rx.Observable;

public class RecommendPresenter implements RecommendContract.Presenter {

    private RecommendListAdapterContract.Model adapterModel;
    private RecommendContract.View view;
    private RecommendService recommendService;

    @Inject
    public RecommendPresenter(RecommendContract.View view, RecommendService recommendService) {
        this.view = view;
        this.recommendService = recommendService;
    }

    @Override
    public void setAdapterModel(RecommendListAdapterContract.Model adapterModel) {
        this.adapterModel = adapterModel;
    }

    @Override
    public void emitShowDetailEvent(RecommendApp recommendApp, int rank) {
        this.view.onShowDetailEvent(recommendApp, rank);
    }

    @Override
    public Observable<List<RecommendApp>> loadSimilarAppsByDemographic() {
        return recommendService.requestSimilarAppsByDemographic(1, 10)
                .concatMap(items -> Observable.from(items))
                .map(item -> item.setRecommendType(RecommendApp.RECOMMEND_TYPE_SIMILAR_DEMOGRAPHIC))
                .toList();
    }
}
