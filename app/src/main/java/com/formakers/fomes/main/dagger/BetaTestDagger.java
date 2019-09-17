package com.formakers.fomes.main.dagger;

import com.formakers.fomes.common.dagger.ApplicationComponent;
import com.formakers.fomes.main.contract.BetaTestContract;
import com.formakers.fomes.main.presenter.BetaTestPresenter;
import com.formakers.fomes.main.view.BetaTestFragment;

public class BetaTestDagger {

    @javax.inject.Scope public @interface Scope { }

    @dagger.Module
    public static class Module {
        private BetaTestContract.View view;

        public Module(BetaTestContract.View view) {
            this.view = view;
        }

        @Scope
        @dagger.Provides
        BetaTestContract.Presenter presenter(BetaTestPresenter presenter) {
            return presenter;
        }

        @Scope
        @dagger.Provides
        BetaTestContract.View view() {
            return this.view;
        }
    }

    @Scope
    @dagger.Component(modules = Module.class, dependencies = ApplicationComponent.class)
    public interface Component {
        void inject(BetaTestFragment fragment);
    }
}
