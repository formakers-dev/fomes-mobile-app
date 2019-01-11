package com.formakers.fomes.main.presenter;

import com.formakers.fomes.main.contract.BetaTestContract;
import com.formakers.fomes.main.contract.BetaTestListAdapterContract;

public class BetaTestPresenter implements BetaTestContract.Presenter {

    private BetaTestListAdapterContract.Model betaTestListAdapterModel;

    @Override
    public void setAdapterModel(BetaTestListAdapterContract.Model adapterModel) {
        this.betaTestListAdapterModel = adapterModel;
    }
}
