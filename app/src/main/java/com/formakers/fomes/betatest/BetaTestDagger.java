package com.formakers.fomes.betatest;

import android.content.Context;

import com.formakers.fomes.common.dagger.ApplicationComponent;

import javax.inject.Named;

public class BetaTestDagger {

    @javax.inject.Scope public @interface Scope { }

    @dagger.Module
    public static class Module {
        private BetaTestContract.View view;
        private Context context;

        public Module(BetaTestFragment view) {
            this.view = view;
            this.context = view.getActivity();
        }

        @Scope
        @dagger.Provides
        BetaTestContract.Presenter presenter(BetaTestPresenter presenter) {
            return presenter;
        }

        @Scope
        @dagger.Provides
        BetaTestContract.View view() {
            return this.view;
        }

        @Scope
        @dagger.Provides
        @Named("activityContext")
        Context context() {
            return this.context;
        }
    }

    @Scope
    @dagger.Component(modules = Module.class, dependencies = ApplicationComponent.class)
    public interface Component {
        void inject(BetaTestFragment fragment);
    }
}
