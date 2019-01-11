package com.formakers.fomes.main.dagger;

import com.formakers.fomes.main.contract.BetaTestContract;
import com.formakers.fomes.main.dagger.scope.BetaTestFragmentScope;
import com.formakers.fomes.main.presenter.BetaTestPresenter;

import dagger.Module;
import dagger.Provides;

@Module
public class BetaTestFragmentModule {
    private BetaTestContract.View view;

    public BetaTestFragmentModule(BetaTestContract.View view) {
        this.view = view;
    }

    @BetaTestFragmentScope
    @Provides
    BetaTestContract.Presenter presenter(BetaTestPresenter presenter) {
        return presenter;
    }

    @BetaTestFragmentScope
    @Provides
    BetaTestContract.View view() {
        return this.view;
    }
}
