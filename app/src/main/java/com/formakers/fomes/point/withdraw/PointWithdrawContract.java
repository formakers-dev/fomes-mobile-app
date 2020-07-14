package com.formakers.fomes.point.withdraw;

import com.formakers.fomes.common.mvp.BaseView;

public interface PointWithdrawContract {
    interface Presenter {
        void bindAvailablePoint();
    }

    interface View extends BaseView<Presenter> {
        void setAvailablePoint(long point);
        void setMaxWithdrawCount(int maxWithdrawCount);
        void setInputComponentsEnabled(boolean enabled);
    }
}
