package com.formakers.fomes.point;

import com.formakers.fomes.common.dagger.ApplicationComponent;

public class PointHistoryDagger {

    @javax.inject.Scope public @interface Scope { }

    @dagger.Module
    public static class Module {
        private PointHistoryContract.View view;

        public Module(PointHistoryContract.View view) {
            this.view = view;
        }

        @Scope
        @dagger.Provides
        PointHistoryContract.Presenter presenter(PointHistoryPresenter presenter) {
            return presenter;
        }

        @Scope
        @dagger.Provides
        PointHistoryContract.View view() {
            return this.view;
        }
    }

    @Scope
    @dagger.Component(modules = Module.class, dependencies = ApplicationComponent.class)
    public interface Component {
        void inject(PointHistoryActivity activity);
    }
}
