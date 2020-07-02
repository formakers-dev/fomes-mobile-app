package com.formakers.fomes.point;

import com.formakers.fomes.common.mvp.BaseView;

public interface PointHistoryContract {
    interface Presenter {
        void setAdapterModel(PointHistoryListAdapterContract.Model adapterModel);

        void bindAvailablePoint();
        void bindHistory();
    }

    interface View extends BaseView<Presenter> {
        void setAvailablePoint(long point);

        void showLoading();
        void hideLoading();

        void refreshHistory();
    }
}
