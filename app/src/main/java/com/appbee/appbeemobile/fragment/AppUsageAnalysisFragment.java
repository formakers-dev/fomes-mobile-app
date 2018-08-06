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
import com.appbee.appbeemobile.helper.AppUsageDataHelper;
import com.appbee.appbeemobile.helper.NativeAppInfoHelper;
import com.appbee.appbeemobile.helper.TimeHelper;
import com.appbee.appbeemobile.model.ShortTermStat;
import com.appbee.appbeemobile.network.ConfigService;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class AppUsageAnalysisFragment extends BaseFragment {
    public static final String TAG = "AnalysisFragment";
    public static final String EXTRA_DESCRIPTION_RES_ID = "DESCRIPTION_RES_ID";

    @Inject
    ConfigService configService;

    @Inject
    AppUsageDataHelper appUsageDataHelper;

    @Inject
    NativeAppInfoHelper nativeAppInfoHelper;

    @Inject
    TimeHelper timeHelper;


    @BindView(R.id.most_personality_app_layout)
    ViewGroup mostPersonalityAppViewGroup;

    @BindView(R.id.most_used_app_layout)
    ViewGroup mostUsedAppViewGroup;

    @BindView(R.id.analysis_description)
    TextView analysisDescription;

    public AppUsageAnalysisFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ((AppBeeApplication) getActivity().getApplication()).getComponent().inject(this);
        return inflater.inflate(R.layout.fragment_analysis, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle arguments = getArguments();
        if (arguments != null) {
            int descriptionStringResId = arguments.getInt(EXTRA_DESCRIPTION_RES_ID, R.string.empty);
            analysisDescription.setText(descriptionStringResId);
        }

        //TODO : 로딩이미지.... 추가.... 하자... --;;;;
        Observable<ShortTermStat> shortTermStatObs = Observable.from(appUsageDataHelper.getWeeklyStatSummaryList());
        Observable<List<String>> blackListObs = configService.getExcludePackageNames().toObservable();

        Observable<ShortTermStat> filteredShortTermStatObs = blackListObs
                .concatMap(blackList -> shortTermStatObs.filter(shortTermStat -> !blackList.contains(shortTermStat.getPackageName())));

        extractAnalysisDataAndBindTo(mostUsedAppViewGroup, shortTermStatObs);
        extractAnalysisDataAndBindTo(mostPersonalityAppViewGroup, filteredShortTermStatObs);
    }

    private void extractAnalysisDataAndBindTo(ViewGroup viewGroup, Observable<ShortTermStat> shortTermStats) {
        addCompositeSubscription(
                shortTermStats.take(3)
                        .map(shortTermStat -> nativeAppInfoHelper.getNativeAppInfo(shortTermStat.getPackageName()))
                        .subscribeOn(Schedulers.io()) // upStream
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
                            viewGroup.addView(itemView);
                        }, Throwable::printStackTrace));
    }
}
