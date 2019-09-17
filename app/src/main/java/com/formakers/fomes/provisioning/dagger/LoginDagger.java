package com.formakers.fomes.provisioning.dagger;

import com.formakers.fomes.common.dagger.ApplicationComponent;
import com.formakers.fomes.provisioning.contract.LoginContract;
import com.formakers.fomes.provisioning.presenter.LoginPresenter;
import com.formakers.fomes.provisioning.view.LoginActivity;

public class LoginDagger {

    @javax.inject.Scope public @interface Scope { }

    @dagger.Module
    public static class Module {
        private LoginContract.View view;

        public Module(LoginContract.View view) {
            this.view = view;
        }

        @Scope
        @dagger.Provides
        LoginContract.Presenter presenter(LoginPresenter presenter) {
            return presenter;
        }

        @Scope
        @dagger.Provides
        LoginContract.View view() {
            return this.view;
        }
    }

    @Scope
    @dagger.Component(modules = Module.class, dependencies = ApplicationComponent.class)
    public interface Component {
        void inject(LoginActivity activity);
    }
}
