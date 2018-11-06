package com.formakers.fomes.wishList;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ContextThemeWrapper;

import com.formakers.fomes.FomesApplication;
import com.formakers.fomes.R;
import com.formakers.fomes.common.network.UserService;
import com.formakers.fomes.common.view.FomesBaseActivity;
import com.formakers.fomes.common.view.decorator.ContentDividerItemDecoration;
import com.formakers.fomes.wishList.adapter.WishListAdapter;

import javax.inject.Inject;

import butterknife.BindView;
import rx.android.schedulers.AndroidSchedulers;

public class WishListActivity extends FomesBaseActivity {

    @BindView(R.id.wish_list_recyclerview) RecyclerView wishListRecyclerView;

    @Inject UserService userService;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FomesApplication.get(this).getComponent().inject(this);

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

        addToCompositeSubscription(
                userService.requestWishList()
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(appInfoList -> {
                            WishListAdapter wishListAdapter = new WishListAdapter(appInfoList);
                            wishListRecyclerView.setAdapter(wishListAdapter);
                        })
        );
    }
}
