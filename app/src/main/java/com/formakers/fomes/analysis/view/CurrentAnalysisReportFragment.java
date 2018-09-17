package com.formakers.fomes.analysis.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.formakers.fomes.AppBeeApplication;
import com.formakers.fomes.R;
import com.formakers.fomes.analysis.contract.CurrentAnalysisReportContract;
import com.formakers.fomes.analysis.presenter.CurrentAnalysisReportPresenter;
import com.formakers.fomes.dagger.ApplicationComponent;
import com.formakers.fomes.fragment.BaseFragment;

import butterknife.BindView;
import rx.android.schedulers.AndroidSchedulers;

public class CurrentAnalysisReportFragment extends BaseFragment implements CurrentAnalysisReportContract.View {
    @BindView(R.id.current_analysis_loading_layout) ViewGroup loadingLayout;
    @BindView(R.id.current_analysis_layout) ViewGroup contentLayout;
    @BindView(R.id.current_analysis_error_layout) ViewGroup errorLayout;

    CurrentAnalysisReportContract.Presenter presenter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setPresenter(new CurrentAnalysisReportPresenter(this));

        return inflater.inflate(R.layout.fragment_current_analysis_report, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // TODO : 아래 뷰들 Fragment 관리로 변경 필요
        presenter.requestPostUsages()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> {
                    loadingLayout.setVisibility(View.GONE);
                    contentLayout.setVisibility(View.VISIBLE);
                }, e -> {
                    loadingLayout.setVisibility(View.GONE);
                    contentLayout.setVisibility(View.GONE);
                    errorLayout.setVisibility(View.VISIBLE);
                });
    }

    @Override
    public void setPresenter(CurrentAnalysisReportContract.Presenter presenter) {
        this.presenter = presenter;
    }


    @Override
    public ApplicationComponent getApplicationComponent() {
        return ((AppBeeApplication) this.getActivity().getApplication()).getComponent();
    }
}
