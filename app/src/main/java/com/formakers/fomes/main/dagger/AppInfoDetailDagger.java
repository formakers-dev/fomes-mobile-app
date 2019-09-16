package com.formakers.fomes.main.dagger;

import com.formakers.fomes.common.dagger.ApplicationComponent;
import com.formakers.fomes.main.contract.AppInfoDetailContract;
import com.formakers.fomes.main.view.AppInfoDetailDialogFragment;
import com.formakers.fomes.main.presenter.AppInfoDetailPresenter;

public class AppInfoDetailDagger {

    @javax.inject.Scope public @interface Scope { }

    @dagger.Module
    public static class Module {
        private AppInfoDetailContract.View view;

        public Module(AppInfoDetailContract.View view) {
            this.view = view;
        }

        @Scope
        @dagger.Provides
        AppInfoDetailContract.Presenter presenter(AppInfoDetailPresenter presenter) {
            return presenter;
        }

        @Scope
        @dagger.Provides
        AppInfoDetailContract.View view() {
            return this.view;
        }
    }

    @Scope
    @dagger.Component(modules = Module.class, dependencies = ApplicationComponent.class)
    public interface Component {
        void inject(AppInfoDetailDialogFragment fragment);
    }
}
