package com.appbee.appbeemobile.fragment;

import android.graphics.drawable.Drawable;
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
import com.appbee.appbeemobile.helper.NativeAppInfoHelper;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

public class OnboardingAnalysisFragment extends BaseFragment {
    public static final String TAG = OnboardingAnalysisFragment.class.getSimpleName();

    public IFragmentManager fragmentManager;

    @Inject
    AppUsageDataHelper appUsageDataHelper;

    @Inject
    NativeAppInfoHelper nativeAppInfoHelper;


    @BindView(R.id.most_personality_app_layout)
    ViewGroup mostPersonalityAppViewGroup;

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
                .concatMapEager(Observable::from)
                .map(packageName -> nativeAppInfoHelper.getNativeAppInfo(packageName))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(nativeAppInfo -> {
                    View itemView = LayoutInflater.from(getActivity()).inflate(R.layout.item_app, null);
                    ImageView iconImageView = ((ImageView) itemView.findViewById(R.id.app_imageview));
                    iconImageView.setTag(R.string.tag_key_image_url, nativeAppInfo.getPackageName());

                    if (nativeAppInfo.getIcon() != null) {
                        iconImageView.setImageDrawable(nativeAppInfo.getIcon());
                    } else {
                        iconImageView.setImageResource(R.mipmap.ic_launcher_app);
                    }

                    ((TextView) itemView.findViewById(R.id.app_name_textview)).setText(nativeAppInfo.getAppName());
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
