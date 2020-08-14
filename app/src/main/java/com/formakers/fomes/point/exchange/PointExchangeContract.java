package com.formakers.fomes.point.exchange;

import com.formakers.fomes.common.mvp.BaseView;

public interface PointExchangeContract {
    interface Presenter {
        void bindAvailablePoint();
        void exchange(int exchangeCount, String phoneNumber);
        boolean isAvailableToExchange(int currentExchangeCount, String phoneNumber);
    }

    interface View extends BaseView<Presenter> {
        void setAvailablePoint(long point);
        void setMaxExchangeCount(int maxExchangeCount);
        void setInputComponentsEnabled(boolean enabled);

        void showToast(String message);
        void successfullyFinish();
    }
}
