package com.appbee.appbeemobile.activity;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.appbee.appbeemobile.AppBeeApplication;
import com.appbee.appbeemobile.R;
import com.appbee.appbeemobile.fragment.BrainFragment;
import com.appbee.appbeemobile.fragment.OverviewFragment;
import com.appbee.appbeemobile.helper.AppBeeAndroidNativeHelper;
import com.appbee.appbeemobile.helper.AppUsageDataHelper;
import com.appbee.appbeemobile.model.LongTermStat;
import com.appbee.appbeemobile.repository.helper.AppRepositoryHelper;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class AnalysisResultActivity extends Activity {
    public static final String BRAIN_FRAGMENT_TAG = "BRAIN_FRAGMENT_TAG";
    public static final String OVERVIEW_FRAGMENT_TAG = "OVERVIEW_FRAGMENT_TAG";

    public static final int NUMBER_OF_MOST_INSTALLED_CATEGORY = 3;
    public static final int NUMBER_OF_LEAST_INSTALLED_CATEGORY = 1;

    @Inject
    AppUsageDataHelper appUsageDataHelper;

    @Inject
    AppBeeAndroidNativeHelper appBeeAndroidNativeHelper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_analysis_result);

        ((AppBeeApplication)getApplication()).getComponent().inject(this);

        getFragmentManager().beginTransaction()
                .add(R.id.overview_fragment, getOverviewFragment(), OVERVIEW_FRAGMENT_TAG)
                .add(R.id.brain_fragment, getBrainFragment(), BRAIN_FRAGMENT_TAG)
                .commit();
    }

    private Fragment getOverviewFragment() {
        Fragment overviewFragment = new OverviewFragment();

        int appCount = appUsageDataHelper.getTotalUsedApps();

        Bundle bundle = new Bundle();
        bundle.putInt(OverviewFragment.EXTRA_APP_LIST_COUNT, appCount);
        bundle.putString(OverviewFragment.EXTRA_APP_LIST_COUNT_MSG, getString(appUsageDataHelper.getAppCountMessage(appCount)));

        List<LongTermStat> longTermStatList = appUsageDataHelper.getLongTermStats();
        int appUsageAverageHourPerDay = appUsageDataHelper.getAppUsageAverageHourPerDay(longTermStatList);
        bundle.putInt(OverviewFragment.EXTRA_APP_AVG_TIME, appUsageAverageHourPerDay);
        bundle.putString(OverviewFragment.EXTRA_APP_USAGE_AVG_TIME_MSG, getString(appUsageDataHelper.getAppUsageAverageMessage(appUsageAverageHourPerDay)));

        List<String> top3UsedAppList = appUsageDataHelper.getTop3UsedAppList();
        ArrayList<String> top3UsedAppNameList = new ArrayList<>();
        for(String packageName : top3UsedAppList) {
            top3UsedAppNameList.add(appBeeAndroidNativeHelper.getAppName(packageName));
        }
        bundle.putStringArrayList(OverviewFragment.EXTRA_LONGEST_USED_APP_NAME_LIST, top3UsedAppNameList);
        String mostUsedSocialAppName = appRepositoryHelper.getMostUsedSocialApp().getAppName();
        String mostUsedSocialAppMessage = appUsageDataHelper.getMostUsedSocialAppMessage(mostUsedSocialAppName);

        if(TextUtils.isEmpty(mostUsedSocialAppName)) {
            mostUsedSocialAppName = "없음";
            mostUsedSocialAppMessage = "SNS 안하시네요";
        }

        bundle.putString(OverviewFragment.EXTRA_MOST_USED_SOCIAL_APP, mostUsedSocialAppName);
        bundle.putString(OverviewFragment.EXTRA_MOST_USED_SOCIAL_APP_MSG, mostUsedSocialAppMessage);

        overviewFragment.setArguments(bundle);

        return overviewFragment;
    }

    private Fragment getBrainFragment() {
        Fragment brainFragment = new BrainFragment();

        Bundle bundle = new Bundle();
        bundle.putStringArrayList(BrainFragment.EXTRA_MOST_INSTALLED_CATEGORIES, appUsageDataHelper.getMostInstalledCategories(NUMBER_OF_MOST_INSTALLED_CATEGORY));
        bundle.putStringArrayList(BrainFragment.EXTRA_LEAST_INSTALLED_CATEGORIES, appUsageDataHelper.getLeastInstalledCategories(NUMBER_OF_LEAST_INSTALLED_CATEGORY));
        bundle.putInt(BrainFragment.EXTRA_INSTALLED_APP_COUNT, appUsageDataHelper.getTotalUsedApps());

        brainFragment.setArguments(bundle);

        return brainFragment;
    }
}
