package com.formakers.fomes.shadow;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.formakers.fomes.analysis.view.RecentAnalysisReportFragment;
import com.formakers.fomes.fragment.BaseFragment;

import org.robolectric.annotation.Implements;

@Implements(value = RecentAnalysisReportFragment.class)
public class ShadowCurrentAnalysisReportFragment extends BaseFragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
}
