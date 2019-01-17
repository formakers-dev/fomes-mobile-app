package com.formakers.fomes.main.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

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
import com.google.common.collect.Lists;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.BindView;

public class RecommendFragment extends BaseFragment implements RecommendContract.View, AppInfoDetailDialogFragment.Communicator {

    public static final String TAG = "RecommendFragment";

    @BindView(R.id.recommend_recyclerview) RecyclerView recommendRecyclerView;
    @BindView(R.id.recommend_contents_layout) ViewGroup recommendContentsLayout;
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

        return inflater.inflate(R.layout.fragment_recommend, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recommendRecyclerView.setLayoutManager(linearLayoutManager);

        ContentDividerItemDecoration dividerItemDecoration = new ContentDividerItemDecoration(getContext(), ContentDividerItemDecoration.VERTICAL);
        dividerItemDecoration.setDrawable(getResources().getDrawable(R.drawable.divider, new ContextThemeWrapper(getContext(), R.style.FomesMainTabTheme_DarkDivider).getTheme()));
        recommendRecyclerView.addItemDecoration(dividerItemDecoration);

        RecommendListAdapter recommendListAdapter = new RecommendListAdapter();
        recommendListAdapter.setPresenter(presenter);
        recommendRecyclerView.setAdapter(recommendListAdapter);
        presenter.setAdapterModel(recommendListAdapter);
        recommendListAdapterView = recommendListAdapter;

        setNestedScrollViewOnScrollChangeListener((NestedScrollView) recommendContentsLayout);

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

    private void setNestedScrollViewOnScrollChangeListener(NestedScrollView view) {
        view.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener) (nestedScrollView, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            View lastChildView = nestedScrollView.getChildAt(nestedScrollView.getChildCount() - 1);

            boolean isBottom = (lastChildView.getBottom() - (nestedScrollView.getHeight() + scrollY)) == 0;

            if (isBottom) {
                presenter.loadRecommendApps("GAME");
            }
        });
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
        recommendContentsLayout.setVisibility(View.VISIBLE);
        recommendErrorLayout.setVisibility(View.GONE);
    }

    @Override
    public void showErrorPage() {
        recommendContentsLayout.setVisibility(View.GONE);
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
}
