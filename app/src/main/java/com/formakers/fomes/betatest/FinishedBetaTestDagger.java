package com.formakers.fomes.betatest;

import com.formakers.fomes.common.dagger.ApplicationComponent;

public class FinishedBetaTestDagger {

    @javax.inject.Scope public @interface Scope { }

    @dagger.Module
    public static class Module {
        private FinishedBetaTestContract.View view;

        public Module(FinishedBetaTestContract.View view) {
            this.view = view;
        }

        @Scope
        @dagger.Provides
        FinishedBetaTestContract.Presenter presenter(FinishedBetaTestPresenter presenter) {
            return presenter;
        }

        @Scope
        @dagger.Provides
        FinishedBetaTestContract.View view() {
            return this.view;
        }
    }

    @Scope
    @dagger.Component(modules = Module.class, dependencies = ApplicationComponent.class)
    public interface Component {
        void inject(FinishedBetaTestFragment fragment);
    }
}
