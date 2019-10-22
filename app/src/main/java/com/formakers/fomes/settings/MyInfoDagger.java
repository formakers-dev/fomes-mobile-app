package com.formakers.fomes.settings;

import com.formakers.fomes.common.dagger.ApplicationComponent;

public class MyInfoDagger {

    @javax.inject.Scope public @interface Scope { }

    @dagger.Module
    public static class Module {
        private MyInfoContract.View view;

        public Module(MyInfoContract.View view) {
            this.view = view;
        }

        @Scope
        @dagger.Provides
        MyInfoContract.Presenter presenter(MyInfoPresenter presenter) {
            return presenter;
        }

        @Scope
        @dagger.Provides
        MyInfoContract.View view() {
            return this.view;
        }
    }

    @Scope
    @dagger.Component(modules = Module.class, dependencies = ApplicationComponent.class)
    public interface Component {
        void inject(MyInfoActivity activity);
    }
}
