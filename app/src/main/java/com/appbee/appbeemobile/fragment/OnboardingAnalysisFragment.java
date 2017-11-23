package com.appbee.appbeemobile.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.appbee.appbeemobile.AppBeeApplication;
import com.appbee.appbeemobile.R;
import com.appbee.appbeemobile.activity.IFragmentManager;
import com.appbee.appbeemobile.helper.AppUsageDataHelper;
import com.appbee.appbeemobile.model.AppUsage;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;

public class OnboardingAnalysisFragment extends BaseFragment {
    public static final String TAG = OnboardingAnalysisFragment.class.getSimpleName();

    public IFragmentManager fragmentManager;

    @Inject
    AppUsageDataHelper appUsageDataHelper;

    @BindView(R.id.most_used_app_layout)
    ViewGroup mostUsedAppViewGroup;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ((AppBeeApplication) getActivity().getApplication()).getComponent().inject(this);
        return inflater.inflate(R.layout.fragment_analysis, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        List<AppUsage> appUsageList = appUsageDataHelper.getSortedUsedApp();
        appUsageList = appUsageList.subList(0, appUsageList.size() < 3 ? appUsageList.size() : 3);

        for (AppUsage item : appUsageList) {
            View itemView = LayoutInflater.from(getActivity()).inflate(R.layout.item_app, null);
            ((ImageView) itemView.findViewById(R.id.app_imageview)).setImageResource(R.drawable.appbee_logo);
            ((TextView) itemView.findViewById(R.id.app_name_textview)).setText(item.getPackageName());
            mostUsedAppViewGroup.addView(itemView);
        }
    }

    public OnboardingAnalysisFragment setFragmentManager(IFragmentManager fragmentManager) {
        this.fragmentManager = fragmentManager;
        return this;
    }

    @OnClick(R.id.next_button)
    public void onNextButtonClick() {
        fragmentManager.replaceFragment(OnboardingRewardsFragment.TAG);
    }
}
