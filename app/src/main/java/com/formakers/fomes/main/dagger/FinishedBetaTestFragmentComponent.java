package com.formakers.fomes.main.dagger;

import com.formakers.fomes.common.dagger.ApplicationComponent;
import com.formakers.fomes.main.dagger.scope.FinishedBetaTestFragmentScope;
import com.formakers.fomes.main.view.FinishedBetaTestFragment;

import dagger.Component;

@FinishedBetaTestFragmentScope
@Component(modules = FinishedBetaTestFragmentModule.class, dependencies = ApplicationComponent.class)
public interface FinishedBetaTestFragmentComponent {
    void inject(FinishedBetaTestFragment fragment);
}
