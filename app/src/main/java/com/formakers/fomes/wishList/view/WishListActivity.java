package com.formakers.fomes.wishList.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.Toast;

import com.formakers.fomes.FomesApplication;
import com.formakers.fomes.R;
import com.formakers.fomes.common.FomesConstants;
import com.formakers.fomes.common.dagger.ApplicationComponent;
import com.formakers.fomes.common.view.FomesBaseActivity;
import com.formakers.fomes.common.view.decorator.ContentDividerItemDecoration;
import com.formakers.fomes.wishList.adapter.WishListAdapter;
import com.formakers.fomes.wishList.contract.WishListContract;
import com.formakers.fomes.wishList.presenter.WishListPresenter;

import java.util.ArrayList;

import butterknife.BindView;
import rx.android.schedulers.AndroidSchedulers;

public class WishListActivity extends FomesBaseActivity implements WishListContract.View {

    @BindView(R.id.wish_list_recyclerview) RecyclerView wishListRecyclerView;
    @BindView(R.id.wish_list_group) View wishListGroup;
    @BindView(R.id.wish_list_empty_group) View wishListEmptyGroup;

    WishListContract.Presenter presenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setPresenter(new WishListPresenter(this));

        setContentView(R.layout.activity_wish_list);

        getSupportActionBar().setTitle(R.string.wish_list);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        wishListRecyclerView.setLayoutManager(linearLayoutManager);

        ContentDividerItemDecoration dividerItemDecoration = new ContentDividerItemDecoration(this, ContentDividerItemDecoration.VERTICAL);
        dividerItemDecoration.setDrawable(getResources().getDrawable(R.drawable.divider, new ContextThemeWrapper(this, R.style.FomesMainTabTheme_RecommendDivider).getTheme()));
        wishListRecyclerView.addItemDecoration(dividerItemDecoration);

        loadWishList();
    }

    @Override
    public void finish() {
        ArrayList<String> removedPackageNames = presenter.getRemovedPackageNames();

        Bundle bundle = new Bundle();
        bundle.putStringArrayList(FomesConstants.EXTRA.PACKAGE_NAMES, removedPackageNames);

        Intent intent = new Intent();
        intent.putExtra(FomesConstants.EXTRA.UNWISHED_APPS, bundle);

        setResult(RESULT_OK, intent);
        super.finish();
    }

    private void loadWishList() {
        addToCompositeSubscription(
                presenter.requestWishList()
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(appInfoList -> {
                            if (appInfoList.size() == 0) {
                                showWishListUI(false);
                            } else {
                                showWishListUI(true);
                                WishListAdapter wishListAdapter = new WishListAdapter(appInfoList);
                                wishListAdapter.setPresenter(presenter);
                                wishListRecyclerView.setAdapter(wishListAdapter);
                            }
                        })
        );
    }

    private void showWishListUI(boolean visible) {
        wishListGroup.setVisibility(visible? View.VISIBLE : View.GONE);
        wishListEmptyGroup.setVisibility(visible? View.GONE : View.VISIBLE);
    }

    @Override
    public ApplicationComponent getApplicationComponent() {
        return FomesApplication.get(this).getComponent();
    }

    @Override
    public void removeApp(String packageName) {
        final WishListAdapter adapter = (WishListAdapter) wishListRecyclerView.getAdapter();
        adapter.removeApp(packageName);
    }

    @Override
    public void showToast(String toastMessage) {
        Toast.makeText(this, toastMessage, Toast.LENGTH_LONG).show();
    }

    @Override
    public void showEmptyList() {
        showWishListUI(false);
    }

    @Override
    public void setPresenter(WishListContract.Presenter presenter) {
        if (this.presenter == null) {
            this.presenter = presenter;
        }
    }
}
