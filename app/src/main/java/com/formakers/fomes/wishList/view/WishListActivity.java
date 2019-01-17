package com.formakers.fomes.wishList.view;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.Group;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.formakers.fomes.FomesApplication;
import com.formakers.fomes.R;
import com.formakers.fomes.common.FomesConstants;
import com.formakers.fomes.common.dagger.ApplicationComponent;
import com.formakers.fomes.common.view.FomesBaseActivity;
import com.formakers.fomes.common.view.decorator.ContentDividerItemDecoration;
import com.formakers.fomes.wishList.adapter.WishListAdapter;
import com.formakers.fomes.wishList.contract.WishListAdapterContract;
import com.formakers.fomes.wishList.contract.WishListContract;
import com.formakers.fomes.wishList.presenter.WishListPresenter;
import com.google.common.collect.Lists;

import java.util.List;

import butterknife.BindView;

public class WishListActivity extends FomesBaseActivity implements WishListContract.View {

    @BindView(R.id.wish_list_recyclerview) RecyclerView wishListRecyclerView;
    @BindView(R.id.wish_list_group) Group wishListGroup;
    @BindView(R.id.wish_list_empty_group) Group wishListEmptyGroup;
    @BindView(R.id.loading_bar) ProgressBar loadingBar;

    WishListContract.Presenter presenter;
    WishListAdapterContract.View adapterView;

    @Override
    public void setPresenter(WishListContract.Presenter presenter) {
        if (this.presenter == null) {
            this.presenter = presenter;
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
        dividerItemDecoration.setDrawable(getResources().getDrawable(R.drawable.divider, new ContextThemeWrapper(this, R.style.FomesMainTabTheme_GrayDivider).getTheme()));
        wishListRecyclerView.addItemDecoration(dividerItemDecoration);

        WishListAdapter wishListAdapter = new WishListAdapter();

        wishListAdapter.setOnWishCheckedChangeListener(position -> {
            // 이 경우 wish button이 삭제 버튼의 역할을 하기 떄문
            presenter.requestRemoveFromWishList(position);
        });

        wishListAdapter.setOnDownloadButtonClickListener(position -> {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("market://details?id=" + presenter.getItemPackageName(position)));
            startActivity(intent);
        });

        wishListRecyclerView.setAdapter(wishListAdapter);
        adapterView = wishListAdapter;

        setPresenter(new WishListPresenter(this, wishListAdapter));

        presenter.loadingWishList();
    }

    @Override
    public void finish() {
        List<String> removedPackageNames = presenter.getRemovedPackageNames();

        Bundle bundle = new Bundle();
        bundle.putStringArrayList(FomesConstants.EXTRA.UNWISHED_APPS, Lists.newArrayList(removedPackageNames));

        Intent intent = new Intent();
        intent.putExtras(bundle);

        setResult(RESULT_OK, intent);
        super.finish();
    }

    @Override
    public void showWishList(boolean hasData) {
        wishListGroup.setVisibility(hasData ? View.VISIBLE : View.GONE);
        wishListEmptyGroup.setVisibility(hasData ? View.GONE : View.VISIBLE);
    }

    @Override
    public void refresh() {
        adapterView.refresh();
    }

    @Override
    public void refresh(int position) {
        adapterView.refresh(position);
    }

    @Override
    public ApplicationComponent getApplicationComponent() {
        return FomesApplication.get(this).getComponent();
    }

    @Override
    public void showToast(String toastMessage) {
        Toast.makeText(this, toastMessage, Toast.LENGTH_LONG).show();
    }

    @Override
    public void showToast(int toastMessageResId) {
        Toast.makeText(this, toastMessageResId, Toast.LENGTH_LONG).show();
    }

    @Override
    public void showLoadingBar() {
        loadingBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideLoadingBar() {
        loadingBar.setVisibility(View.GONE);
    }
}
