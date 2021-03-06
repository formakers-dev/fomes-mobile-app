package com.formakers.fomes.point.history;

import com.formakers.fomes.common.mvp.BaseView;

public interface PointHistoryContract {
    interface Presenter {
        void setAdapterModel(PointHistoryListAdapterContract.Model adapterModel);

        void bindAvailablePoint();
        void bindTotalPoint();
        void bindHistory();
    }

    interface View extends BaseView<Presenter> {
        void setAvailablePoint(long point);
        void setTotalPoint(long point);

        void showLoading();
        void hideLoading();
        void showEmpty();

        void refreshHistory();
    }
}
