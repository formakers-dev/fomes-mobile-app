package com.formakers.fomes.main.dagger;

import com.formakers.fomes.main.contract.AppInfoDetailContract;
import com.formakers.fomes.main.dagger.scope.AppInfoDetailFragmentScope;
import com.formakers.fomes.main.presenter.AppInfoDetailPresenter;

import dagger.Module;
import dagger.Provides;

@Module
public class AppInfoDetailFragmentModule {
    private AppInfoDetailContract.View view;

    public AppInfoDetailFragmentModule(AppInfoDetailContract.View view) {
        this.view = view;
    }

    @AppInfoDetailFragmentScope
    @Provides
    AppInfoDetailContract.Presenter presenter(AppInfoDetailPresenter presenter) {
        return presenter;
    }

    @AppInfoDetailFragmentScope
    @Provides
    AppInfoDetailContract.View view() {
        return this.view;
    }
}
