package com.formakers.fomes.recommend;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
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
import com.formakers.fomes.analysis.RecentAnalysisReportActivity;
import com.formakers.fomes.common.constant.FomesConstants;
import com.formakers.fomes.common.network.vo.RecommendApp;
import com.formakers.fomes.common.util.Log;
import com.formakers.fomes.common.view.BaseFragment;
import com.formakers.fomes.common.view.custom.decorator.ContentDividerItemDecoration;
import com.formakers.fomes.main.MainActivity;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.BindView;

public class RecommendFragment extends BaseFragment implements RecommendContract.View,
        MainActivity.FragmentCommunicator {

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
        DaggerRecommendDagger_Component.builder()
                .applicationComponent(FomesApplication.get(this.getActivity()).getComponent())
                .module(new RecommendDagger.Module(this))
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
            startActivityForResult(new Intent(getActivity(), RecentAnalysisReportActivity.class), MainActivity.REQUEST_CODE_ANALYSIS);
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
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("market://details?id=" + recommendApp.getAppInfo().getPackageName()));
        startActivity(intent);
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
    public void onSelectedPage() {
        if (this.presenter != null) {
            presenter.getAnalytics().setCurrentScreen(this);
        }
    }
}
