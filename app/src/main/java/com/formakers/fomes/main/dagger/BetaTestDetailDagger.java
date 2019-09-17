package com.formakers.fomes.main.dagger;

import com.formakers.fomes.common.dagger.ApplicationComponent;
import com.formakers.fomes.main.contract.BetaTestDetailContract;
import com.formakers.fomes.main.presenter.BetaTestDetailPresenter;
import com.formakers.fomes.main.view.BetaTestDetailActivity;

public class BetaTestDetailDagger {

    @javax.inject.Scope public @interface Scope { }

    @dagger.Module
    public static class Module {
        private BetaTestDetailContract.View view;

        public Module(BetaTestDetailContract.View view) {
            this.view = view;
        }

        @Scope
        @dagger.Provides
        BetaTestDetailContract.Presenter presenter(BetaTestDetailPresenter presenter) {
            return presenter;
        }

        @Scope
        @dagger.Provides
        BetaTestDetailContract.View view() {
            return this.view;
        }
    }

    @Scope
    @dagger.Component(modules = Module.class, dependencies = ApplicationComponent.class)
    public interface Component {
        void inject(BetaTestDetailActivity activity);
    }
}
