package com.formakers.fomes.betatest;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.formakers.fomes.FomesApplication;
import com.formakers.fomes.R;
import com.formakers.fomes.common.constant.FomesConstants;
import com.formakers.fomes.common.util.Log;
import com.formakers.fomes.common.view.BaseFragment;
import com.formakers.fomes.common.view.custom.decorator.ContentDividerItemDecoration;
import com.formakers.fomes.common.view.webview.WebViewActivity;
import com.formakers.fomes.main.MainActivity;

import javax.inject.Inject;

import butterknife.BindView;

public class FinishedBetaTestFragment extends BaseFragment implements MainActivity.FragmentCommunicator, FinishedBetaTestContract.View {
    public static final String TAG = "FinishedBetaTestFragment";

    @BindView(R.id.finished_betatest_recyclerview) RecyclerView finishedBetatestRecyclerView;
    @BindView(R.id.finished_betatest_empty_view) View finishedBetatestEmptyView;
    @BindView(R.id.loading) ProgressBar loadingProgressBar;
    @BindView(R.id.finished_betatest_swipe_refresh_layout) SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.title_option_menu_switch) Switch completedFilterSwitch;
    @BindView(R.id.title_option_menu) TextView completedFilterTextView;

    @Inject FinishedBetaTestContract.Presenter presenter;

    FinishedBetaTestListAdapterContract.View adapterView;

    private FomesNoticeDialog noticeDialog = new FomesNoticeDialog();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        DaggerFinishedBetaTestDagger_Component.builder()
                .applicationComponent(FomesApplication.get(this.getActivity()).getComponent())
                .module(new FinishedBetaTestDagger.Module(this))
                .build()
                .inject(this);


        if (((MainActivity) getActivity()).isSelectedFragment(this)) {
            onSelectedPage();
        }

        return inflater.inflate(R.layout.fragment_finished_betatest, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        finishedBetatestRecyclerView.setLayoutManager(linearLayoutManager);

        ContentDividerItemDecoration dividerItemDecoration = new ContentDividerItemDecoration(getContext(), ContentDividerItemDecoration.VERTICAL);
        dividerItemDecoration.setDrawable(getResources().getDrawable(R.drawable.divider, new ContextThemeWrapper(getContext(), R.style.FomesMainTabTheme_BetaTestDivider).getTheme()));
        finishedBetatestRecyclerView.addItemDecoration(dividerItemDecoration);

        FinishedBetaTestListAdapter adapter = new FinishedBetaTestListAdapter();
        adapter.setPresenter(presenter);

        finishedBetatestRecyclerView.setAdapter(adapter);
        presenter.setAdapterModel(adapter);
        this.setAdapterView(adapter);

        presenter.initialize();

        swipeRefreshLayout.setOnRefreshListener(() -> presenter.load()
                .toCompletable()
                .doOnSubscribe((x) -> swipeRefreshLayout.setRefreshing(true))
                .doAfterTerminate(() -> swipeRefreshLayout.setRefreshing(false))
                .subscribe(() -> {}, e -> Log.e(TAG, "ErrorOnLoad e=" + e)));

        completedFilterSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            presenter.applyCompletedFilter(isChecked);
            completedFilterTextView.setTextColor(isChecked ? getResources().getColor(R.color.colorPrimary) : getResources().getColor(R.color.fomes_warm_gray_2));
        });
    }

    @Override
    public void onSelectedPage() {
        if (this.presenter != null) {
            presenter.getAnalytics().setCurrentScreen(this);
        }
    }

    @Override
    public void setPresenter(FinishedBetaTestContract.Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void setAdapterView(FinishedBetaTestListAdapterContract.View adapterView) {
        this.adapterView = adapterView;
    }

    @Override
    public boolean isNeedAppliedCompletedFilter() {
        return completedFilterSwitch.isChecked();
    }

    @Override
    public void showLoading() {
        loadingProgressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideLoading() {
        loadingProgressBar.setVisibility(View.GONE);
    }

    @Override
    public void showEmptyView() {
        finishedBetatestRecyclerView.setVisibility(View.GONE);
        finishedBetatestEmptyView.setVisibility(View.VISIBLE);
    }

    @Override
    public void showListView() {
        finishedBetatestRecyclerView.setVisibility(View.VISIBLE);
        finishedBetatestEmptyView.setVisibility(View.GONE);
    }

    @Override
    public void showNoticePopup(int titleResId, int subTitleResId, int imageResId,
                                int positiveButtonTextResId, View.OnClickListener positiveButtonClickListener) {
        Bundle bundle = new Bundle();
        bundle.putString(FomesNoticeDialog.EXTRA_TITLE, getString(titleResId));
        bundle.putString(FomesNoticeDialog.EXTRA_SUBTITLE, getString(subTitleResId));
        bundle.putInt(FomesNoticeDialog.EXTRA_IMAGE_RES_ID, imageResId);

        noticeDialog.setArguments(bundle);
        noticeDialog.setPositiveButton(getString(positiveButtonTextResId), positiveButtonClickListener);
        noticeDialog.show(this.getFragmentManager(), "Test");
    }

    @Override
    public void refresh() {
        adapterView.notifyDataSetChanged();
    }

    @Override
    public void startWebViewActivity(String title, String url) {
        Intent intent = new Intent(getContext(), WebViewActivity.class);
        intent.putExtra(FomesConstants.WebView.EXTRA_TITLE, title);
        intent.putExtra(FomesConstants.WebView.EXTRA_CONTENTS, url);
        startActivity(intent);
    }

    @Override
    public void startByDeeplink(Uri deeplinkUri) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(deeplinkUri);
        startActivity(intent);
    }
}
