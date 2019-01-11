package com.formakers.fomes.main.dagger;

import com.formakers.fomes.common.dagger.ApplicationComponent;
import com.formakers.fomes.main.dagger.scope.BetaTestFragmentScope;
import com.formakers.fomes.main.view.BetaTestFragment;

import dagger.Component;

@BetaTestFragmentScope
@Component(modules = BetaTestFragmentModule.class, dependencies = ApplicationComponent.class)
public interface BetaTestFragmentComponent {
    void inject(BetaTestFragment fragment);
}
