package com.formakers.fomes.main.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.formakers.fomes.FomesApplication;
import com.formakers.fomes.R;
import com.formakers.fomes.common.FomesConstants;
import com.formakers.fomes.common.network.vo.BetaTest;
import com.formakers.fomes.common.util.Log;
import com.formakers.fomes.common.view.BaseFragment;
import com.formakers.fomes.common.view.decorator.ContentDividerItemDecoration;
import com.formakers.fomes.main.adapter.BetaTestListAdapter;
import com.formakers.fomes.main.contract.BetaTestContract;
import com.formakers.fomes.main.contract.BetaTestListAdapterContract;
import com.formakers.fomes.main.dagger.BetaTestFragmentModule;
import com.formakers.fomes.main.dagger.DaggerBetaTestFragmentComponent;

import java.util.Date;

import javax.inject.Inject;

import butterknife.BindView;

public class BetaTestFragment extends BaseFragment implements BetaTestContract.View {

    public static final String TAG = "BetaTestFragment";

    @BindView(R.id.feedback_recyclerview) RecyclerView recyclerView;
    @BindView(R.id.loading) ProgressBar loadingBar;
    @BindView(R.id.betatest_swipe_refresh_layout) SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.betatest_empty_view) View emptyView;
    @BindView(R.id.betatest_empty_textview) TextView emptyTextView;

    @Inject BetaTestContract.Presenter presenter;
    BetaTestListAdapterContract.View betaTestListAdapterView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");

        DaggerBetaTestFragmentComponent.builder()
                .applicationComponent(FomesApplication.get(this.getActivity()).getComponent())
                .betaTestFragmentModule(new BetaTestFragmentModule(this))
                .build()
                .inject(this);

        return inflater.inflate(R.layout.fragment_betatest, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);

        ContentDividerItemDecoration dividerItemDecoration = new ContentDividerItemDecoration(getContext(), ContentDividerItemDecoration.VERTICAL);
        dividerItemDecoration.setDrawable(getResources().getDrawable(R.drawable.divider, new ContextThemeWrapper(getContext(), R.style.FomesMainTabTheme_BetaTestDivider).getTheme()));
        recyclerView.addItemDecoration(dividerItemDecoration);

        BetaTestListAdapter betaTestListAdapter = new BetaTestListAdapter();
        betaTestListAdapter.setPresenter(presenter);
        recyclerView.setAdapter(betaTestListAdapter);
        presenter.setAdapterModel(betaTestListAdapter);
        this.setAdapterView(betaTestListAdapter);

        betaTestListAdapterView.setOnItemClickListener(position -> {
            Bundle bundle = new Bundle();
            BetaTest betaTestItem = this.presenter.getBetaTestItem(position);

            Log.v(TAG, "Clicked BetaTest=" + betaTestItem);

            bundle.putParcelable(FomesConstants.BetaTest.EXTRA_BETA_TEST, betaTestItem);
            bundle.putString(FomesConstants.BetaTest.EXTRA_USER_EMAIL, this.presenter.getUserEmail());

            BetaTestDetailAlertDialog betaTestDetailAlertDialog = new BetaTestDetailAlertDialog();
            betaTestDetailAlertDialog.setArguments(bundle);
            betaTestDetailAlertDialog.setPresenter(this.presenter);
            betaTestDetailAlertDialog.show(getFragmentManager(), BetaTestDetailAlertDialog.TAG);

            this.presenter.sendEventLog(FomesConstants.EventLog.Code.BETA_TEST_FRAGMENT_TAP_ITEM, String.valueOf(betaTestItem.getId()));
        });

        swipeRefreshLayout.setOnRefreshListener(() -> {
            presenter.loadToBetaTestList(new Date())
                    .toCompletable()
                    .doOnSubscribe(x -> swipeRefreshLayout.setRefreshing(true))
                    .doAfterTerminate(() -> swipeRefreshLayout.setRefreshing(false))
                    .subscribe(() -> {
                    }, e -> Log.e(TAG, String.valueOf(e)));
        });

        presenter.initialize();
    }

    @Override
    public void setPresenter(BetaTestContract.Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void setAdapterView(BetaTestListAdapterContract.View adapterView) {
        this.betaTestListAdapterView = adapterView;
    }

    @Override
    public void setUserNickName(String nickName) {
        emptyTextView.setText(String.format(getString(R.string.beta_test_empty_text_format), nickName));
    }

    @Override
    public void showLoading() {
        loadingBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideLoading() {
        loadingBar.setVisibility(View.GONE);
    }

    @Override
    public void showEmptyView() {
        emptyView.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
    }

    @Override
    public void showBetaTestListView() {
        emptyView.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
    }

    @Override
    public void refreshBetaTestList() {
        betaTestListAdapterView.notifyDataSetChanged();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        if (this.presenter != null) {
            this.presenter.unsubscribe();
        }
    }
}
