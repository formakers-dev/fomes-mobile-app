package com.formakers.fomes.main.contract;


import com.formakers.fomes.common.mvp.BaseView;
import com.formakers.fomes.common.network.vo.BetaTest;

public interface BetaTestContract {
    interface Presenter {
        void setAdapterModel(BetaTestListAdapterContract.Model adapterModel);

        void load();
        BetaTest getBetaTestItem(int position);
        String getUserEmail();
        void unsubscribe();
    }

    interface View extends BaseView<Presenter> {
        void setAdapterView(BetaTestListAdapterContract.View adapterView);

        void showLoading();
        void hideLoading();

        void refreshBetaTestList();
    }
}
