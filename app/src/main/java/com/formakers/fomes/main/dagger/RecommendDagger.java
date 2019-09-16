package com.formakers.fomes.main.dagger;

import com.formakers.fomes.common.dagger.ApplicationComponent;
import com.formakers.fomes.main.contract.RecommendContract;
import com.formakers.fomes.main.view.RecommendFragment;
import com.formakers.fomes.main.presenter.RecommendPresenter;

public class RecommendDagger {

    @javax.inject.Scope public @interface Scope { }

    @dagger.Module
    public static class Module {
        private RecommendContract.View view;

        public Module(RecommendContract.View view) {
            this.view = view;
        }

        @Scope
        @dagger.Provides
        RecommendContract.Presenter presenter(RecommendPresenter presenter) {
            return presenter;
        }

        @Scope
        @dagger.Provides
        RecommendContract.View view() {
            return this.view;
        }
    }

    @Scope
    @dagger.Component(modules = Module.class, dependencies = ApplicationComponent.class)
    public interface Component {
        void inject(RecommendFragment fragment);
    }
}
