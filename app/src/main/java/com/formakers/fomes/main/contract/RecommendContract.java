package com.formakers.fomes.main.contract;

import com.formakers.fomes.common.mvp.BaseView;
import com.formakers.fomes.model.AppInfo;

public interface RecommendContract {
    interface Presenter {
        void setAdapterModel(RecommendListAdapterContract.Model adapterModel);
        void emitShowDetailEvent(AppInfo appInfo);
    }

    interface View extends BaseView<Presenter> {
        void onShowDetailEvent(AppInfo appInfo);
    }
}
