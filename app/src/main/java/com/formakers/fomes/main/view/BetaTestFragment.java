package com.formakers.fomes.main.view;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
import com.formakers.fomes.common.constant.Feature;
import com.formakers.fomes.common.util.Log;
import com.formakers.fomes.common.view.BaseFragment;
import com.formakers.fomes.common.view.decorator.ContentDividerItemDecoration;
import com.formakers.fomes.main.adapter.BetaTestListAdapter;
import com.formakers.fomes.main.contract.BetaTestContract;
import com.formakers.fomes.main.contract.BetaTestListAdapterContract;
import com.formakers.fomes.main.dagger.BetaTestFragmentModule;
import com.formakers.fomes.main.dagger.DaggerBetaTestFragmentComponent;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;

public class BetaTestFragment extends BaseFragment implements BetaTestContract.View {

    public static final String TAG = "BetaTestFragment";

    @BindView(R.id.feedback_recyclerview) RecyclerView recyclerView;
    @BindView(R.id.loading) ProgressBar loadingBar;

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

        View view;

        if (Feature.BETATEST_ZONE) {
            view = inflater.inflate(R.layout.fragment_betatest, container, false);
        } else {
            view = inflater.inflate(R.layout.fragment_betatest_old, container, false);
        }

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (Feature.BETATEST_ZONE) {
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
                bundle.putParcelable(FomesConstants.BetaTest.EXTRA_BETA_TEST, this.presenter.getBetaTestItem(position));
                bundle.putString(FomesConstants.BetaTest.EXTRA_USER_EMAIL, this.presenter.getUserEmail());

                BetaTestDetailAlertDialog betaTestDetailAlertDialog = new BetaTestDetailAlertDialog();
                betaTestDetailAlertDialog.setArguments(bundle);
                betaTestDetailAlertDialog.show(getFragmentManager(), BetaTestDetailAlertDialog.TAG);
            });

            presenter.load();
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
    public void showLoading() {
        loadingBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideLoading() {
        loadingBar.setVisibility(View.GONE);
    }

    @Override
    public void refreshBetaTestList() {
        betaTestListAdapterView.notifyDataSetChanged();
    }

    @OnClick(R.id.betatest_notice_more_info)
    public void onInfoButtonClicked() {
        View view = getLayoutInflater().inflate(R.layout.layout_betatest_notice, null);

        view.findViewById(R.id.notice_send_mail_button).setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("message/rfc822");
            intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"contact@fomakers.net"});
            intent.putExtra(Intent.EXTRA_SUBJECT, "테스트 참여 완료 처리 문의 (By " + presenter.getUserEmail() + ")");
            intent.putExtra(Intent.EXTRA_TEXT, "포메스 팀에게 문의해주세요! :-)\n\n======신속한 문의 대응을 위한 유저 정보======\n\n포메스 가입 이메일: " + presenter.getUserEmail() + "\n\n======================================\n\n* 문의 내용 : \n");
            startActivity(intent);
        });

        new AlertDialog.Builder(this.getContext())
                .setView(view)
                .show();
    }
}
