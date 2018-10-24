package com.formakers.fomes.main.contract;

import com.formakers.fomes.common.mvp.BaseView;
import com.formakers.fomes.common.network.vo.RecommendApp;
import com.formakers.fomes.model.AppInfo;

import java.util.List;

import rx.Observable;

public interface RecommendContract {
    interface Presenter {
        void setAdapterModel(RecommendListAdapterContract.Model adapterModel);
        void emitShowDetailEvent(AppInfo appInfo);
        Observable<List<RecommendApp>> loadSimilarAppsByDemographic();
    }

    interface View extends BaseView<Presenter> {
        void onShowDetailEvent(AppInfo appInfo);
    }
}
