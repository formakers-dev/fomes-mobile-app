package com.formakers.fomes.shadow;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.formakers.fomes.appbee.fragment.AppUsageAnalysisFragment;
import com.formakers.fomes.common.view.BaseFragment;

import org.robolectric.annotation.Implements;

@Implements(value = AppUsageAnalysisFragment.class)
public class ShadowAppUsageAnalysisFragment extends BaseFragment {

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
