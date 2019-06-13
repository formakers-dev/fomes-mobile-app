package com.formakers.fomes.main.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.formakers.fomes.FomesApplication;
import com.formakers.fomes.R;
import com.formakers.fomes.common.FomesConstants;
import com.formakers.fomes.common.network.vo.RecommendApp;
import com.formakers.fomes.common.util.Log;
import com.formakers.fomes.common.view.BaseFragment;
import com.formakers.fomes.common.view.decorator.ContentDividerItemDecoration;
import com.formakers.fomes.main.adapter.RecommendListAdapter;
import com.formakers.fomes.main.contract.RecommendContract;
import com.formakers.fomes.main.contract.RecommendListAdapterContract;
import com.formakers.fomes.main.dagger.DaggerRecommendFragmentComponent;
import com.formakers.fomes.main.dagger.RecommendFragmentModule;
import com.formakers.fomes.wishList.view.WishListActivity;
import com.google.common.collect.Lists;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.BindView;

public class RecommendFragment extends BaseFragment implements RecommendContract.View,
        MainActivity.FragmentCommunicator, AppInfoDetailDialogFragment.Communicator {

    public static final String TAG = "RecommendFragment";

    @BindView(R.id.title_option_menu) View optionMenuView;
    @BindView(R.id.recommend_recyclerview) RecyclerView recommendRecyclerView;
    @BindView(R.id.recommend_nested_scrollview) NestedScrollView recommendContentsLayout;
    @BindView(R.id.recommend_error_layout) ViewGroup recommendErrorLayout;
    @BindView(R.id.recommend_loading) ProgressBar recommendLoadingProgressBar;

    @Inject RecommendContract.Presenter presenter;

    RecommendListAdapterContract.View recommendListAdapterView;

    @Override
    public void setPresenter(RecommendContract.Presenter presenter) {
        if (this.presenter == null) {
            this.presenter = presenter;
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        DaggerRecommendFragmentComponent.builder()
                .applicationComponent(FomesApplication.get(this.getActivity()).getComponent())
                .recommendFragmentModule(new RecommendFragmentModule(this))
                .build()
                .inject(this);

        if (((MainActivity) getActivity()).isSelectedFragment(this)) {
            onSelectedPage();
        }

        return inflater.inflate(R.layout.fragment_recommend, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        optionMenuView.setOnClickListener(v -> {
            startActivityForResult(new Intent(getActivity(), WishListActivity.class), MainActivity.REQUEST_CODE_WISHLIST);
        });

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        recommendRecyclerView.setLayoutManager(linearLayoutManager);

        ContentDividerItemDecoration dividerItemDecoration = new ContentDividerItemDecoration(getContext(), ContentDividerItemDecoration.VERTICAL);
        dividerItemDecoration.setDrawable(getResources().getDrawable(R.drawable.divider, new ContextThemeWrapper(getContext(), R.style.FomesMainTabTheme_DarkDivider).getTheme()));
        recommendRecyclerView.addItemDecoration(dividerItemDecoration);

        RecommendListAdapter recommendListAdapter = new RecommendListAdapter();
        recommendListAdapter.setPresenter(presenter);
        recommendRecyclerView.setAdapter(recommendListAdapter);
        presenter.setAdapterModel(recommendListAdapter);
        recommendListAdapterView = recommendListAdapter;

        recommendContentsLayout.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener) (nestedScrollView, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            if (isEndOfRecommendList()) {
                presenter.loadRecommendApps("GAME");
            }
        });

        presenter.loadRecommendApps("GAME");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult requestCode=" + requestCode + " resultCode=" + resultCode + " data=" + data);

        if (resultCode == Activity.RESULT_OK) {
            ArrayList<String> unwishedPackageNames = data.getExtras().getStringArrayList(FomesConstants.EXTRA.UNWISHED_APPS);

            for (String packageName : unwishedPackageNames) {
                try {
                    this.presenter.updateWishedStatus(packageName, false);
                    this.recommendListAdapterView.notifyItemChanged(packageName);
                } catch (IllegalArgumentException e) {
                }
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        this.presenter.unsubscribe();
    }

    @Override
    public boolean isEndOfRecommendList() {
        return !this.recommendContentsLayout.canScrollVertically(1);
    }

    @Override
    public void onShowDetailEvent(RecommendApp recommendApp) {
        AppInfoDetailDialogFragment appInfoDetailDialogFragment = new AppInfoDetailDialogFragment();
        appInfoDetailDialogFragment.setCommunicator(this);

        Bundle bundle = new Bundle();
        bundle.putString(FomesConstants.EXTRA.PACKAGE_NAME, recommendApp.getAppInfo().getPackageName());
        bundle.putInt(FomesConstants.EXTRA.RECOMMEND_TYPE, recommendApp.getRecommendType());
        bundle.putStringArrayList(FomesConstants.EXTRA.RECOMMEND_CRITERIA, Lists.newArrayList(recommendApp.getCriteria()));
        appInfoDetailDialogFragment.setArguments(bundle);

        appInfoDetailDialogFragment.show(getChildFragmentManager(), AppInfoDetailDialogFragment.TAG);
    }

    @Override
    public void showRecommendList() {
        recommendRecyclerView.setVisibility(View.VISIBLE);
        recommendErrorLayout.setVisibility(View.GONE);
    }

    @Override
    public void showErrorPage() {
        recommendRecyclerView.setVisibility(View.GONE);
        recommendErrorLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void showLoading() {
        recommendLoadingProgressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideLoading() {
        recommendLoadingProgressBar.setVisibility(View.GONE);
    }

    @Override
    public void refreshRecommendList() {
        recommendListAdapterView.notifyDataSetChanged();
    }

    @Override
    public void emitUpdateWishedStatusEvent(String packageName, boolean isWished) {
        this.presenter.updateWishedStatus(packageName, isWished);
        this.recommendListAdapterView.notifyItemChanged(packageName);
    }

    @Override
    public void onSelectedPage() {
        presenter.getAnalytics().setCurrentScreen(this);
    }
}
