package com.formakers.fomes.main.view;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.formakers.fomes.FomesApplication;
import com.formakers.fomes.R;
import com.formakers.fomes.common.FomesConstants;
import com.formakers.fomes.common.constant.Feature;
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

public class BetaTestFragment extends BaseFragment implements BetaTestContract.View, MainActivity.FragmentCommunicator {

    public static final String TAG = "BetaTestFragment";

    public static final int REQUEST_CODE_DETAIL_DIALOG = 1001;
    public static final int REQUEST_CODE_DETAIL = 1002;

    @BindView(R.id.feedback_recyclerview) RecyclerView recyclerView;
    @BindView(R.id.loading) ProgressBar loadingBar;
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

        DaggerBetaTestFragmentComponent.builder()
                .applicationComponent(FomesApplication.get(this.getActivity()).getComponent())
                .betaTestFragmentModule(new BetaTestFragmentModule(this))
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
        recyclerView.setLayoutManager(linearLayoutManager);

        ContentDividerItemDecoration dividerItemDecoration = new ContentDividerItemDecoration(getContext(), ContentDividerItemDecoration.VERTICAL);
        dividerItemDecoration.setDrawable(getResources().getDrawable(R.drawable.divider,
                new ContextThemeWrapper(getContext(), R.style.FomesMainTabTheme_BetaTestDivider).getTheme()));
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

            if (Feature.FOMES_V_2_5_TEMPORARY_DESIGN) {
                bundle.putParcelable(FomesConstants.BetaTest.EXTRA_BETA_TEST, betaTestItem);
                bundle.putString(FomesConstants.BetaTest.EXTRA_USER_EMAIL, this.presenter.getUserEmail());

                BetaTestDetailAlertDialog betaTestDetailAlertDialog = new BetaTestDetailAlertDialog();
                betaTestDetailAlertDialog.setArguments(bundle);
                betaTestDetailAlertDialog.setPresenter(this.presenter);
                betaTestDetailAlertDialog.show(getFragmentManager(), BetaTestDetailAlertDialog.TAG);
            } else {
                // 테스트 디테일 화면
                bundle.putString(FomesConstants.BetaTest.EXTRA_ID, betaTestItem.getId());
                bundle.putLong(FomesConstants.BetaTest.EXTRA_REMAIN_DAYS, betaTestItem.getRemainDays());
                bundle.putString(FomesConstants.BetaTest.EXTRA_USER_EMAIL, this.presenter.getUserEmail());
                Intent intent = new Intent(getContext(), BetaTestDetailActivity.class);
                intent.putExtras(bundle);
                this.startActivityForResult(intent, REQUEST_CODE_DETAIL);
            }

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

        if (Feature.FOMES_V_2_5_TEMPORARY_DESIGN) {
            attendFilterSwitch.setVisibility(View.GONE);
            attendFilterTextView.setVisibility(View.GONE);
        }

        presenter.initialize();
    }

    @Override
    public void onResume() {
        super.onResume();

        Bundle bundle = getArguments();
        if (bundle != null) {
            String betaTestId = bundle.getString("EXTRA_SELECTED_ITEM_ID");

            if (!TextUtils.isEmpty(betaTestId)) {
                int position = presenter.getBetaTestPostitionById(betaTestId);
                if (position >= 0) {
                    recyclerView.findViewHolderForAdapterPosition(position).itemView.performClick();
                } else {
                    Toast.makeText(getContext(), "없어용", Toast.LENGTH_SHORT).show();
                }
                bundle.remove("EXTRA_SELECTED_ITEM_ID");
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult(" + requestCode + ", " + resultCode + ", " + data + ")");

        if (requestCode == REQUEST_CODE_DETAIL_DIALOG) {
            this.presenter.getAnalytics().setCurrentScreen(this);
        } else if (requestCode == REQUEST_CODE_DETAIL) {
            this.presenter.requestBetaTestProgress(data.getStringExtra(FomesConstants.BetaTest.EXTRA_ID));
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
        presenter.getAnalytics().setCurrentScreen(this);
    }
}
