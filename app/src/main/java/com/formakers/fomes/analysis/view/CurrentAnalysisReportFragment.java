package com.formakers.fomes.analysis.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.formakers.fomes.R;
import com.formakers.fomes.fragment.BaseFragment;

import butterknife.BindView;

public class CurrentAnalysisReportFragment extends BaseFragment {
    @BindView(R.id.current_analysis_loading_layout) ViewGroup loadingLayout;
    @BindView(R.id.current_analysis_layout) ViewGroup contentLayout;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_current_analysis_report, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.setOnClickListener(v -> {
            loadingLayout.setVisibility(View.GONE);
            contentLayout.setVisibility(View.VISIBLE);
        });
    }
}
