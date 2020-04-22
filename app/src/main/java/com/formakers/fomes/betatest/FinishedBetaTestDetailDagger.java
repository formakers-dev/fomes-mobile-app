package com.formakers.fomes.betatest;

import com.formakers.fomes.common.dagger.ApplicationComponent;

public class FinishedBetaTestDetailDagger {

    @javax.inject.Scope public @interface Scope { }

    @dagger.Module
    public static class Module {
        private FinishedBetaTestDetailContract.View view;

        public Module(FinishedBetaTestDetailContract.View view) {
            this.view = view;
        }

        @Scope
        @dagger.Provides
        FinishedBetaTestDetailContract.Presenter presenter(FinishedBetaTestDetailPresenter presenter) {
            return presenter;
        }

        @Scope
        @dagger.Provides
        FinishedBetaTestDetailContract.View view() {
            return this.view;
        }
    }

    @Scope
    @dagger.Component(modules = Module.class, dependencies = ApplicationComponent.class)
    public interface Component {
        void inject(FinishedBetaTestDetailActivity activity);
    }
}
