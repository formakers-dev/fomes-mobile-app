package com.formakers.fomes.main.presenter;

import com.formakers.fomes.main.contract.BetaTestContract;
import com.formakers.fomes.main.contract.BetaTestListAdapterContract;
import com.formakers.fomes.main.dagger.scope.BetaTestFragmentScope;

import javax.inject.Inject;

@BetaTestFragmentScope
public class BetaTestPresenter implements BetaTestContract.Presenter {

    private BetaTestListAdapterContract.Model betaTestListAdapterModel;

    @Inject
    public BetaTestPresenter() {
    }

    @Override
    public void setAdapterModel(BetaTestListAdapterContract.Model adapterModel) {
        this.betaTestListAdapterModel = adapterModel;
    }
}
