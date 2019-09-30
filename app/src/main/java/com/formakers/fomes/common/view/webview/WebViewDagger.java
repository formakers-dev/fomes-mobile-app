package com.formakers.fomes.common.view.webview;

import com.formakers.fomes.common.dagger.ApplicationComponent;

public class WebViewDagger {

    @javax.inject.Scope @interface Scope { }

    @dagger.Module
    public static class Module {
        private WebViewConstract.View view;

        public Module(WebViewConstract.View view) {
            this.view = view;
        }

        @Scope
        @dagger.Provides
        WebViewConstract.Presenter presenter(WebViewPresenter presenter) {
            return presenter;
        }

        @Scope
        @dagger.Provides
        WebViewConstract.View view() {
            return this.view;
        }
    }

    @Scope
    @dagger.Component(modules = Module.class, dependencies = ApplicationComponent.class)
    public interface Component {
        void inject(WebViewActivity activity);
    }
}