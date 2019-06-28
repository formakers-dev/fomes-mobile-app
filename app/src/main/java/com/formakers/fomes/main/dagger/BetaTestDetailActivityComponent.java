package com.formakers.fomes.main.dagger;

import com.formakers.fomes.common.dagger.ApplicationComponent;
import com.formakers.fomes.main.dagger.scope.BetaTestDetailActivityScope;
import com.formakers.fomes.main.view.BetaTestDetailActivity;

import dagger.Component;

@BetaTestDetailActivityScope
@Component(modules = BetaTestDetailActivityModule.class, dependencies = ApplicationComponent.class)
public interface BetaTestDetailActivityComponent {
    void inject(BetaTestDetailActivity activity);
}
