package com.formakers.fomes.main.contract;


import com.formakers.fomes.common.mvp.BaseView;
import com.formakers.fomes.common.network.vo.BetaTest;

import java.util.List;

import rx.Single;

public interface BetaTestContract {
    interface Presenter {
        void setAdapterModel(BetaTestListAdapterContract.Model adapterModel);

        void initialize();
        Single<List<BetaTest>> loadToBetaTestList();
        BetaTest getBetaTestItem(int position);
        int getBetaTestPostitionById(int id);
        String getUserEmail();
        void sendEventLog(String code, String ref);
        void unsubscribe();
    }

    interface View extends BaseView<Presenter> {
        void setAdapterView(BetaTestListAdapterContract.View adapterView);
        void setUserNickName(String nickName);

        void showLoading();
        void hideLoading();
        void showEmptyView();

        void showBetaTestListView();

        void refreshBetaTestList();
    }
}
