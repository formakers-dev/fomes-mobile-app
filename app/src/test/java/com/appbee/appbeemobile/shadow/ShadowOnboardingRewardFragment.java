package com.appbee.appbeemobile.shadow;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.appbee.appbeemobile.fragment.BaseFragment;
import com.appbee.appbeemobile.fragment.OnboardingRewardsFragment;

import org.robolectric.annotation.Implements;

@Implements(value = OnboardingRewardsFragment.class)
public class ShadowOnboardingRewardFragment extends BaseFragment{
    public ShadowOnboardingRewardFragment() {
    }

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
