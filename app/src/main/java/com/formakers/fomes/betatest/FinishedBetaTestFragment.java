package com.formakers.fomes.betatest;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.formakers.fomes.FomesApplication;
import com.formakers.fomes.R;
import com.formakers.fomes.common.constant.FomesConstants;
import com.formakers.fomes.common.network.vo.BetaTest;
import com.formakers.fomes.common.util.Log;
import com.formakers.fomes.common.view.BaseFragment;
import com.formakers.fomes.common.view.FomesNoticeDialog;
import com.formakers.fomes.common.view.custom.decorator.ContentDividerItemDecoration;
import com.formakers.fomes.common.view.webview.WebViewActivity;
import com.formakers.fomes.main.MainActivity;

import javax.inject.Inject;

import butterknife.BindView;

public class FinishedBetaTestFragment extends BaseFragment implements MainActivity.FragmentCommunicator, FinishedBetaTestContract.View {
    public static final String TAG = "FinishedBetaTestFragment";

    @BindView(R.id.finished_betatest_recyclerview_shimmer) ShimmerFrameLayout finishedBetatestRecyclerViewShimmer;
    @BindView(R.id.finished_betatest_recyclerview) RecyclerView finishedBetatestRecyclerView;
    @BindView(R.id.finished_betatest_empty_view) View finishedBetatestEmptyView;
    @BindView(R.id.finished_betatest_swipe_refresh_layout) SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.title_option_menu_switch) Switch completedFilterSwitch;
    @BindView(R.id.title_option_menu) TextView completedFilterTextView;

    @Inject FinishedBetaTestContract.Presenter presenter;

    FinishedBetaTestListAdapterContract.View adapterView;

    private Context context;
    private FomesNoticeDialog noticeDialog = new FomesNoticeDialog();

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

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
        adapter.setOnItemClickListener(position -> {
            Intent intent = new Intent(context, FinishedBetaTestDetailActivity.class);
            BetaTest betaTest = presenter.getItem(position);

            intent.putExtra(FomesConstants.BetaTest.EXTRA_BETA_TEST, betaTest);

            startActivity(intent);
        });

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
        finishedBetatestRecyclerViewShimmer.startShimmer();
    }

    @Override
    public void hideLoading() {
        finishedBetatestRecyclerViewShimmer.stopShimmer();
        finishedBetatestRecyclerViewShimmer.setVisibility(View.GONE);
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

    // TODO : 네이밍 고민..
    @Override
    public void selectBetaTestIfExist() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            String betaTestId = bundle.getString("EXTRA_SELECTED_ITEM_ID");
            Log.d(TAG, "selected betaTestId=" + betaTestId);

            if (!TextUtils.isEmpty(betaTestId)) {
                int position = presenter.getPostitionById(betaTestId);
                if (position >= 0) {
                    finishedBetatestRecyclerView.findViewHolderForAdapterPosition(position).itemView.performClick();
                } else {
                    Toast.makeText(getContext(), "없어용", Toast.LENGTH_SHORT).show();
                }
                bundle.remove("EXTRA_SELECTED_ITEM_ID");
            }
        }
    }
}
