package com.formakers.fomes.main.dagger;

import com.formakers.fomes.main.contract.BetaTestDetailContract;
import com.formakers.fomes.main.dagger.scope.BetaTestDetailActivityScope;
import com.formakers.fomes.main.presenter.BetaTestDetailPresenter;

import dagger.Module;
import dagger.Provides;

@Module
public class BetaTestDetailActivityModule {
    private BetaTestDetailContract.View view;

    public BetaTestDetailActivityModule(BetaTestDetailContract.View view) {
        this.view = view;
    }

    @BetaTestDetailActivityScope
    @Provides
    BetaTestDetailContract.Presenter presenter(BetaTestDetailPresenter presenter) {
        return presenter;
    }

    @BetaTestDetailActivityScope
    @Provides
    BetaTestDetailContract.View view() {
        return this.view;
    }
}
