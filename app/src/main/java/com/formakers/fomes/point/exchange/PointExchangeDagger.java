package com.formakers.fomes.point.exchange;

import com.formakers.fomes.common.dagger.ApplicationComponent;

public class PointExchangeDagger {

    @javax.inject.Scope public @interface Scope { }

    @dagger.Module
    public static class Module {
        private PointExchangeContract.View view;

        public Module(PointExchangeContract.View view) {
            this.view = view;
        }

        @Scope
        @dagger.Provides
        PointExchangeContract.Presenter presenter(PointExchangePresenter presenter) {
            return presenter;
        }

        @Scope
        @dagger.Provides
        PointExchangeContract.View view() {
            return this.view;
        }
    }

    @Scope
    @dagger.Component(modules = Module.class, dependencies = ApplicationComponent.class)
    public interface Component {
        void inject(PointExchangeActivity activity);
    }
}
