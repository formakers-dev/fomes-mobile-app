package com.formakers.fomes.point.withdraw;

import com.formakers.fomes.common.dagger.ApplicationComponent;

public class PointWithdrawDagger {

    @javax.inject.Scope public @interface Scope { }

    @dagger.Module
    public static class Module {
        private PointWithdrawContract.View view;

        public Module(PointWithdrawContract.View view) {
            this.view = view;
        }

        @Scope
        @dagger.Provides
        PointWithdrawContract.Presenter presenter(PointWithdrawPresenter presenter) {
            return presenter;
        }

        @Scope
        @dagger.Provides
        PointWithdrawContract.View view() {
            return this.view;
        }
    }

    @Scope
    @dagger.Component(modules = Module.class, dependencies = ApplicationComponent.class)
    public interface Component {
        void inject(PointWithdrawActivity activity);
    }
}
