package com.formakers.fomes.main.contract;


import com.formakers.fomes.common.mvp.BaseView;

public interface BetaTestContract {
    interface Presenter {
        void setAdapterModel(BetaTestListAdapterContract.Model adapterModel);
    }

    interface View extends BaseView<Presenter> {
    }
}
