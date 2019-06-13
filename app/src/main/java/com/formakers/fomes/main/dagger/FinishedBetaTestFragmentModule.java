package com.formakers.fomes.main.dagger;

import com.formakers.fomes.main.contract.FinishedBetaTestContract;
import com.formakers.fomes.main.dagger.scope.FinishedBetaTestFragmentScope;
import com.formakers.fomes.main.presenter.FinishedBetaTestPresenter;

import dagger.Module;
import dagger.Provides;

@Module
public class FinishedBetaTestFragmentModule {
    private FinishedBetaTestContract.View view;

    public FinishedBetaTestFragmentModule(FinishedBetaTestContract.View view) {
        this.view = view;
    }

    @FinishedBetaTestFragmentScope
    @Provides
    FinishedBetaTestContract.Presenter presenter(FinishedBetaTestPresenter presenter) {
        return presenter;
    }

    @FinishedBetaTestFragmentScope
    @Provides
    FinishedBetaTestContract.View view() {
        return this.view;
    }
}
