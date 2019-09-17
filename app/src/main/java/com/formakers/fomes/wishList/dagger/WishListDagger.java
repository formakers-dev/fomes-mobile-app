package com.formakers.fomes.wishList.dagger;

import com.formakers.fomes.common.dagger.ApplicationComponent;
import com.formakers.fomes.wishList.contract.WishListContract;
import com.formakers.fomes.wishList.presenter.WishListPresenter;
import com.formakers.fomes.wishList.view.WishListActivity;

public class WishListDagger {

    @javax.inject.Scope public @interface Scope { }

    @dagger.Module
    public static class Module {
        private WishListContract.View view;

        public Module(WishListContract.View view) {
            this.view = view;
        }

        @Scope
        @dagger.Provides
        WishListContract.Presenter presenter(WishListPresenter presenter) {
            return presenter;
        }

        @Scope
        @dagger.Provides
        WishListContract.View view() {
            return this.view;
        }
    }

    @Scope
    @dagger.Component(modules = Module.class, dependencies = ApplicationComponent.class)
    public interface Component {
        void inject(WishListActivity activity);
    }
}
