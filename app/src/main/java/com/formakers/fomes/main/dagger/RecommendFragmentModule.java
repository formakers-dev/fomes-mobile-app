package com.formakers.fomes.main.dagger;

import com.formakers.fomes.main.contract.RecommendContract;
import com.formakers.fomes.main.dagger.scope.RecommendFragmentScope;
import com.formakers.fomes.main.presenter.RecommendPresenter;

import dagger.Module;
import dagger.Provides;

@Module
public class RecommendFragmentModule {
    private RecommendContract.View view;

    public RecommendFragmentModule(RecommendContract.View view) {
        this.view = view;
    }

    @RecommendFragmentScope
    @Provides
    RecommendContract.Presenter presenter(RecommendPresenter presenter) {
        return presenter;
    }

    @RecommendFragmentScope
    @Provides
    RecommendContract.View view() {
        return this.view;
    }
}
