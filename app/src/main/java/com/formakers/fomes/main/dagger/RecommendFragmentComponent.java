package com.formakers.fomes.main.dagger;

import com.formakers.fomes.common.dagger.ApplicationComponent;
import com.formakers.fomes.main.dagger.scope.RecommendFragmentScope;
import com.formakers.fomes.main.view.RecommendFragment;

import dagger.Component;

@RecommendFragmentScope
@Component(modules = RecommendFragmentModule.class, dependencies = ApplicationComponent.class)
public interface RecommendFragmentComponent {
    void inject(RecommendFragment fragment);
}
