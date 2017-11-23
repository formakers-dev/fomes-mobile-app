package com.appbee.appbeemobile.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.appbee.appbeemobile.AppBeeApplication;
import com.appbee.appbeemobile.R;
import com.appbee.appbeemobile.activity.IFragmentManager;
import com.appbee.appbeemobile.helper.AppUsageDataHelper;
import com.appbee.appbeemobile.network.AppService;
import com.bumptech.glide.Glide;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class OnboardingAnalysisFragment extends BaseFragment {
    public static final String TAG = OnboardingAnalysisFragment.class.getSimpleName();

    public IFragmentManager fragmentManager;

    @Inject
    AppUsageDataHelper appUsageDataHelper;

    @Inject
    AppService appService;

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

        appUsageDataHelper.getSortedUsedPackageNames()
                .observeOn(Schedulers.io())
                .concatMapEager(packageNameList -> appService.getAppInfo(packageNameList))
                .concatMapEager(Observable::from)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(appInfo -> {
                    View itemView = LayoutInflater.from(getActivity()).inflate(R.layout.item_app, null);
                    ImageView iconImageView = ((ImageView) itemView.findViewById(R.id.app_imageview));

                    String url = appInfo.getIconUrl();
                    if (!TextUtils.isEmpty(url)) {
                        Glide.with(OnboardingAnalysisFragment.this).load(url).into(iconImageView);
                        iconImageView.setTag(R.string.tag_key_image_url, url);
                    } else {
                        iconImageView.setImageResource(R.mipmap.ic_launcher_app);
                    }

                    ((TextView) itemView.findViewById(R.id.app_name_textview)).setText(appInfo.getAppName());
                    mostUsedAppViewGroup.addView(itemView);
                });

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
