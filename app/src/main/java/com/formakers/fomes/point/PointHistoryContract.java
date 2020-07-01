package com.formakers.fomes.point;

import com.formakers.fomes.common.mvp.BaseView;

public interface PointHistoryContract {
    interface Presenter {
        void bindAvailablePoint();
    }

    interface View extends BaseView<Presenter> {
        void setAvailablePoint(long point);
    }
}
