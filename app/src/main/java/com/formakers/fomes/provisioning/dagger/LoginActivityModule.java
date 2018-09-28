package com.formakers.fomes.provisioning.dagger;

import com.formakers.fomes.provisioning.contract.LoginContract;
import com.formakers.fomes.provisioning.dagger.scope.LoginActivityScope;
import com.formakers.fomes.provisioning.presenter.LoginPresenter;

import dagger.Module;
import dagger.Provides;

@Module
public class LoginActivityModule {

    private LoginContract.View view;

    public LoginActivityModule(LoginContract.View activity) {
        this.view = activity;
    }

    @LoginActivityScope
    @Provides
    LoginContract.Presenter presenter(LoginPresenter presenter) {
        return presenter;
    }

    @LoginActivityScope
    @Provides
    LoginContract.View view() {
        return this.view;
    }
}
