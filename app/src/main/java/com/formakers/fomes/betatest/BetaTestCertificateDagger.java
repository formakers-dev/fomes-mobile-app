package com.formakers.fomes.betatest;

import com.formakers.fomes.common.dagger.ApplicationComponent;

public class BetaTestCertificateDagger {

    @javax.inject.Scope public @interface Scope { }

    @dagger.Module
    public static class Module {
        private BetaTestCertificateContract.View view;

        public Module(BetaTestCertificateContract.View view) {
            this.view = view;
        }

        @Scope
        @dagger.Provides
        BetaTestCertificateContract.Presenter presenter(BetaTestCertificatePresenter presenter) {
            return presenter;
        }

        @Scope
        @dagger.Provides
        BetaTestCertificateContract.View view() {
            return this.view;
        }
    }

    @Scope
    @dagger.Component(modules = Module.class, dependencies = ApplicationComponent.class)
    public interface Component {
        void inject(BetaTestCertificateActivity activity);
    }
}
