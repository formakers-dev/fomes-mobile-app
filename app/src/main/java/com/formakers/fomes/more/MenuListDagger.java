package com.formakers.fomes.more;

import com.formakers.fomes.common.dagger.ApplicationComponent;

public class MenuListDagger {

    @javax.inject.Scope public @interface Scope { }

    @dagger.Module
    public static class Module {
        private MenuListContract.View view;

        public Module(MenuListContract.View view) {
            this.view = view;
        }

        @Scope
        @dagger.Provides
        MenuListContract.Presenter presenter(MenuListPresenter presenter) {
            return presenter;
        }

        @Scope
        @dagger.Provides
        MenuListContract.View view() {
            return this.view;
        }
    }

    @Scope
    @dagger.Component(modules = Module.class, dependencies = ApplicationComponent.class)
    public interface Component {
        void inject(MenuListFragment fragment);
    }
}
