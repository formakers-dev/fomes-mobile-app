package com.formakers.fomes.main.presenter;

import com.formakers.fomes.common.network.RecommendService;
import com.formakers.fomes.main.contract.RecommendContract;
import com.formakers.fomes.main.contract.RecommendListAdapterContract;
import com.formakers.fomes.model.AppInfo;

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
    public void emitShowDetailEvent(AppInfo appInfo) {
        this.view.onShowDetailEvent(appInfo);
    }

    @Override
    public Observable<List<AppInfo>> loadSimilarAppsByDemographic() {
        return recommendService.requestSimilarAppsByDemographic(1, 10);
    }
}
