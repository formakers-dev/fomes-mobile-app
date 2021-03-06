package com.formakers.fomes.betatest;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

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
import com.formakers.fomes.common.view.custom.decorator.ContentDividerItemDecoration;
import com.formakers.fomes.main.MainActivity;

import java.util.Date;

import javax.inject.Inject;

import butterknife.BindView;

public class BetaTestFragment extends BaseFragment implements BetaTestContract.View, MainActivity.FragmentCommunicator {

    public static final String TAG = "BetaTestFragment";

    public static final int REQUEST_CODE_DETAIL = 1002;

    @BindView(R.id.feedback_recyclerview_shimmer) ShimmerFrameLayout betaTestRecyclerViewShimmer;
    @BindView(R.id.feedback_recyclerview) RecyclerView betaTestRecyclerView;
    @BindView(R.id.betatest_swipe_refresh_layout) SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.betatest_empty_view) View emptyView;
    @BindView(R.id.betatest_empty_textview) TextView emptyTextView;
    @BindView(R.id.title_option_menu_switch) Switch attendFilterSwitch;
    @BindView(R.id.title_option_menu) TextView attendFilterTextView;

    @Inject BetaTestContract.Presenter presenter;
    BetaTestListAdapterContract.View betaTestListAdapterView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        com.formakers.fomes.common.util.Log.d(TAG, "onCreateView");

        DaggerBetaTestDagger_Component.builder()
                .applicationComponent(FomesApplication.get(this.getActivity()).getComponent())
                .module(new BetaTestDagger.Module(this))
                .build()
                .inject(this);

        if (((MainActivity) getActivity()).isSelectedFragment(this)) {
            onSelectedPage();
        }

        return inflater.inflate(R.layout.fragment_betatest, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        betaTestRecyclerView.setLayoutManager(linearLayoutManager);

        ContentDividerItemDecoration dividerItemDecoration = new ContentDividerItemDecoration(getContext(), ContentDividerItemDecoration.VERTICAL);
        dividerItemDecoration.setDrawable(getResources().getDrawable(R.drawable.divider,
                new ContextThemeWrapper(getContext(), R.style.FomesMainTabTheme_BetaTestDivider).getTheme()));
        betaTestRecyclerView.addItemDecoration(dividerItemDecoration);

        BetaTestListAdapter betaTestListAdapter = new BetaTestListAdapter();
        betaTestListAdapter.setPresenter(presenter);
        betaTestRecyclerView.setAdapter(betaTestListAdapter);
        presenter.setAdapterModel(betaTestListAdapter);
        this.setAdapterView(betaTestListAdapter);

        betaTestListAdapterView.setOnItemClickListener(position -> {
            Bundle bundle = new Bundle();
            BetaTest betaTestItem = this.presenter.getBetaTestItem(position);

            Log.v(TAG, "Clicked BetaTest=" + betaTestItem);

            // ????????? ????????? ??????
            bundle.putString(FomesConstants.BetaTest.EXTRA_ID, betaTestItem.getId());
            bundle.putLong(FomesConstants.BetaTest.EXTRA_REMAIN_DAYS, betaTestItem.getRemainDays());
            Intent intent = new Intent(getContext(), BetaTestDetailActivity.class);
            intent.putExtras(bundle);
            this.startActivityForResult(intent, REQUEST_CODE_DETAIL);

            this.presenter.sendEventLog(FomesConstants.FomesEventLog.Code.BETA_TEST_FRAGMENT_TAP_ITEM, String.valueOf(betaTestItem.getId()));
            this.presenter.getAnalytics().sendClickEventLog(FomesConstants.BetaTest.Log.TARGET_ITEM, String.valueOf(betaTestItem.getId()));
        });

        swipeRefreshLayout.setOnRefreshListener(() ->
            presenter.loadToBetaTestList(new Date())
                    .toCompletable()
                    .doOnSubscribe(x -> swipeRefreshLayout.setRefreshing(true))
                    .doAfterTerminate(() -> swipeRefreshLayout.setRefreshing(false))
                    .subscribe(() -> {
                    }, e -> com.formakers.fomes.common.util.Log.e(TAG, String.valueOf(e))));

        attendFilterSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
//            presenter.applyAttendFilter(isChecked);
            attendFilterTextView.setTextColor(isChecked ? getResources().getColor(R.color.colorPrimary) : getResources().getColor(R.color.fomes_warm_gray_2));
        });

        presenter.initialize();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult(" + requestCode + ", " + resultCode + ", " + data + ")");

        if (requestCode == REQUEST_CODE_DETAIL) {
            this.presenter.getAnalytics().setCurrentScreen(this);

            if (data != null) {
                this.presenter.requestBetaTestProgress(data.getStringExtra(FomesConstants.BetaTest.EXTRA_ID));
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
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
        betaTestRecyclerViewShimmer.startShimmer();
    }

    @Override
    public void hideLoading() {
        betaTestRecyclerViewShimmer.stopShimmer();
        betaTestRecyclerViewShimmer.setVisibility(View.GONE);
        betaTestRecyclerView.setVisibility(View.VISIBLE);
    }

    @Override
    public void showEmptyView() {
        emptyView.setVisibility(View.VISIBLE);
        betaTestRecyclerView.setVisibility(View.GONE);
    }

    @Override
    public void showBetaTestListView() {
        emptyView.setVisibility(View.GONE);
        betaTestRecyclerView.setVisibility(View.VISIBLE);
    }

    @Override
    public void refreshBetaTestList() {
        betaTestListAdapterView.notifyDataSetChanged();
    }

    @Override
    public void refreshBetaTestProgress(int position) {
        betaTestListAdapterView.notifyItemChanged(position);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        if (this.presenter != null) {
            this.presenter.unsubscribe();
        }
    }

    @Override
    public void onSelectedPage() {
        if (this.presenter != null) {
            presenter.getAnalytics().setCurrentScreen(this);
        }
    }

    // TODO : ????????? ??????..
    @Override
    public void selectBetaTestIfExist() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            String betaTestId = bundle.getString("EXTRA_SELECTED_ITEM_ID");
            Log.d(TAG, "selected betaTestId=" + betaTestId);

            if (!TextUtils.isEmpty(betaTestId)) {
                int position = presenter.getBetaTestPostitionById(betaTestId);
                if (position >= 0) {
                    betaTestRecyclerView.findViewHolderForAdapterPosition(position).itemView.performClick();
                } else {
                    Toast.makeText(getContext(), "?????????", Toast.LENGTH_SHORT).show();
                }
                bundle.remove("EXTRA_SELECTED_ITEM_ID");
            }
        }
    }
}
