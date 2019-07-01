package com.formakers.fomes.main.presenter;

import com.formakers.fomes.common.dagger.AnalyticsModule;
import com.formakers.fomes.main.contract.BetaTestDetailContract;
import com.formakers.fomes.main.dagger.scope.BetaTestDetailActivityScope;

@BetaTestDetailActivityScope
public class BetaTestDetailPresenter implements BetaTestDetailContract.Presenter {
    @Override
    public AnalyticsModule.Analytics getAnalytics() {
        return null;
    }

    @Override
    public void sendEventLog(String code, String ref) {

    }

    @Override
    public void unsubscribe() {

    }
}
