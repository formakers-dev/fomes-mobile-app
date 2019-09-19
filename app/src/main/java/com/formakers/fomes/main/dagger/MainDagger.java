package com.formakers.fomes.main.dagger;

import com.formakers.fomes.common.dagger.ApplicationComponent;
import com.formakers.fomes.main.contract.MainContract;
import com.formakers.fomes.main.presenter.MainPresenter;
import com.formakers.fomes.main.view.MainActivity;

public class MainDagger {

    @javax.inject.Scope public @interface Scope { }

    @dagger.Module
    public static class Module {
        private MainContract.View view;

        public Module(MainContract.View view) {
            this.view = view;
        }

        @Scope
        @dagger.Provides
        MainContract.Presenter presenter(MainPresenter presenter) {
            return presenter;
        }

        @Scope
        @dagger.Provides
        MainContract.View view() {
            return this.view;
        }
    }

    @Scope
    @dagger.Component(modules = Module.class, dependencies = ApplicationComponent.class)
    public interface Component {
        void inject(MainActivity activity);
    }
}
