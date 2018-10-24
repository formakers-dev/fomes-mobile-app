package com.formakers.fomes.main.contract;

import com.formakers.fomes.common.mvp.BaseView;
import com.formakers.fomes.model.AppInfo;

import java.util.List;

import rx.Observable;

public interface RecommendContract {
    interface Presenter {
        void setAdapterModel(RecommendListAdapterContract.Model adapterModel);
        void emitShowDetailEvent(AppInfo appInfo);
        Observable<List<AppInfo>> loadSimilarAppsByDemographic();
    }

    interface View extends BaseView<Presenter> {
        void onShowDetailEvent(AppInfo appInfo);
    }
}
