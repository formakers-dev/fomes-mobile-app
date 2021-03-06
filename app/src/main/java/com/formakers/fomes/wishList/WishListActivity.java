package com.formakers.fomes.wishList;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.Group;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.formakers.fomes.FomesApplication;
import com.formakers.fomes.R;
import com.formakers.fomes.common.constant.FomesConstants;
import com.formakers.fomes.common.view.FomesBaseActivity;
import com.formakers.fomes.common.view.custom.decorator.ContentDividerItemDecoration;
import com.google.common.collect.Lists;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import rx.subscriptions.CompositeSubscription;

public class WishListActivity extends FomesBaseActivity implements WishListContract.View {

    @BindView(R.id.wish_list_recyclerview) RecyclerView wishListRecyclerView;
    @BindView(R.id.wish_list_group) Group wishListGroup;
    @BindView(R.id.wish_list_empty_group) Group wishListEmptyGroup;
    @BindView(R.id.loading_bar) ProgressBar loadingBar;

    @Inject WishListContract.Presenter presenter;
    WishListAdapterContract.View adapterView;

    private final CompositeSubscription compositeSubscription = new CompositeSubscription();

    @Override
    public void setPresenter(WishListContract.Presenter presenter) {
        if (this.presenter == null) {
            this.presenter = presenter;
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DaggerWishListDagger_Component.builder()
                .applicationComponent(FomesApplication.get(this).getComponent())
                .module(new WishListDagger.Module(this))
                .build()
                .inject(this);

        setContentView(R.layout.activity_wish_list);

        getSupportActionBar().setTitle(R.string.wish_list);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        wishListRecyclerView.setLayoutManager(linearLayoutManager);

        ContentDividerItemDecoration dividerItemDecoration = new ContentDividerItemDecoration(this, ContentDividerItemDecoration.VERTICAL);
        dividerItemDecoration.setDrawable(getResources().getDrawable(R.drawable.divider, new ContextThemeWrapper(this, R.style.FomesMainTabTheme_GrayDivider).getTheme()));
        wishListRecyclerView.addItemDecoration(dividerItemDecoration);

        WishListAdapter wishListAdapter = new WishListAdapter(this.presenter);

        wishListAdapter.setOnWishCheckedChangeListener(position -> {
            // ??? ?????? wish button??? ?????? ????????? ????????? ?????? ??????
            presenter.requestRemoveFromWishList(position);
        });

        wishListAdapter.setOnDownloadButtonClickListener(position -> {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("market://details?id=" + presenter.getItemPackageName(position)));
            startActivity(intent);
        });

        wishListRecyclerView.setAdapter(wishListAdapter);
        presenter.setAdapterModel(wishListAdapter);
        adapterView = wishListAdapter;

        presenter.loadingWishList();
    }

    @Override
    protected void onDestroy() {
        compositeSubscription.clear();

        super.onDestroy();
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

    @Override
    public CompositeSubscription getCompositeSubscription() {
        return compositeSubscription;
    }
}
