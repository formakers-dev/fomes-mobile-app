package com.formakers.fomes.main.contract;


import com.formakers.fomes.common.mvp.BaseView;

public interface BetaTestContract {
    interface Presenter {
        void setAdapterModel(BetaTestListAdapterContract.Model adapterModel);

        void load();
        String getSurveyURL(int position);
    }

    interface View extends BaseView<Presenter> {
        void setAdapterView(BetaTestListAdapterContract.View adapterView);

        void showLoading();
        void hideLoading();

        void refreshBetaTestList();
    }
}
