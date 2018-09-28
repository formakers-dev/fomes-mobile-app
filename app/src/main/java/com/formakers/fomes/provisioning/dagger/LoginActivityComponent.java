package com.formakers.fomes.provisioning.dagger;

import com.formakers.fomes.dagger.ApplicationComponent;
import com.formakers.fomes.provisioning.dagger.scope.LoginActivityScope;
import com.formakers.fomes.provisioning.view.LoginActivity;

import dagger.Component;

@LoginActivityScope
@Component(modules = LoginActivityModule.class, dependencies = ApplicationComponent.class)
public interface LoginActivityComponent {
    void inject(LoginActivity activity);
}
