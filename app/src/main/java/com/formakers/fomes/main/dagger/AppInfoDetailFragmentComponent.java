package com.formakers.fomes.main.dagger;

import com.formakers.fomes.common.dagger.ApplicationComponent;
import com.formakers.fomes.main.dagger.scope.AppInfoDetailFragmentScope;
import com.formakers.fomes.main.view.AppInfoDetailDialogFragment;

import dagger.Component;

@AppInfoDetailFragmentScope
@Component(modules = AppInfoDetailFragmentModule.class, dependencies = ApplicationComponent.class)
public interface AppInfoDetailFragmentComponent {
    void inject(AppInfoDetailDialogFragment fragment);
}
