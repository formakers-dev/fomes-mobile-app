package com.appbee.appbeemobile.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.appbee.appbeemobile.R;
import com.appbee.appbeemobile.activity.IFragmentManager;

import butterknife.OnClick;

public class OnboardingRewardsFragment extends BaseFragment {
    public static final String TAG = OnboardingRewardsFragment.class.getSimpleName();

    public IFragmentManager fragmentManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_rewards, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // View 작업은 여기서 부터 시작
    }

    public OnboardingRewardsFragment setFragmentManager(IFragmentManager fragmentManager) {
        this.fragmentManager = fragmentManager;
        return this;
    }

    @OnClick(R.id.next_button)
    void onNextButtonClick() {
        fragmentManager.startMainActivityAndFinish();
    }
}
