package com.appbee.appbeemobile.activity;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.appbee.appbeemobile.AppBeeApplication;
import com.appbee.appbeemobile.R;
import com.appbee.appbeemobile.fragment.BrainFragment;
import com.appbee.appbeemobile.fragment.OverviewFragment;
import com.appbee.appbeemobile.fragment.ShareFragment;
import com.appbee.appbeemobile.helper.AppUsageDataHelper;
import com.appbee.appbeemobile.helper.ShareSnsHelper;
import com.appbee.appbeemobile.model.AppInfo;
import com.appbee.appbeemobile.model.LongTermStat;
import com.appbee.appbeemobile.repository.helper.AppRepositoryHelper;
import com.appbee.appbeemobile.util.AppBeeConstants.APP_LIST_COUNT_TYPE;
import com.appbee.appbeemobile.util.AppBeeConstants.APP_USAGE_TIME_TYPE;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class AnalysisResultActivity extends Activity {
    private static final int NUMBER_OF_TOP_USED_APP_COUNT = 3;

    public static final String BRAIN_FRAGMENT_TAG = "BRAIN_FRAGMENT_TAG";
    public static final String OVERVIEW_FRAGMENT_TAG = "OVERVIEW_FRAGMENT_TAG";
    public static final String SHARE_FRAGMENT_TAG = "SHARE_FRAGMENT_TAG";

    public static final int NUMBER_OF_MOST_INSTALLED_CATEGORY = 3;
    public static final int NUMBER_OF_LEAST_INSTALLED_CATEGORY = 1;

    @Inject
    AppUsageDataHelper appUsageDataHelper;

    @Inject
    AppRepositoryHelper appRepositoryHelper;

    @Inject
    ShareSnsHelper shareSnsHelper;

    private List<AppInfo> appInfoList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_analysis_result);

        ((AppBeeApplication) getApplication()).getComponent().inject(this);

        appInfoList = appUsageDataHelper.getSortedUsedAppsByTotalUsedTime();

        getFragmentManager().beginTransaction()
                .add(R.id.overview_fragment, getOverviewFragment(), OVERVIEW_FRAGMENT_TAG)
                .add(R.id.brain_fragment, getBrainFragment(), BRAIN_FRAGMENT_TAG)
                .add(R.id.share_fragment, new ShareFragment(), SHARE_FRAGMENT_TAG)
                .commit();
    }

    private Fragment getOverviewFragment() {
        Fragment overviewFragment = new OverviewFragment();

        int appCount = appInfoList.size();

        Bundle bundle = new Bundle();
        bundle.putInt(OverviewFragment.EXTRA_APP_LIST_COUNT, appCount);
        bundle.putInt(OverviewFragment.EXTRA_APP_LIST_COUNT_TYPE, getAppCountType(appCount));

        List<LongTermStat> longTermStatList = appUsageDataHelper.getLongTermStats();
        double appUsageAverageHourPerDay = appUsageDataHelper.getAppUsageAverageHourPerDay(longTermStatList);
        bundle.putInt(OverviewFragment.EXTRA_APP_AVG_TIME, (int) Math.round(appUsageAverageHourPerDay));
        bundle.putInt(OverviewFragment.EXTRA_APP_USAGE_TIME_TYPE, getAppUsageTimeType(appUsageAverageHourPerDay));
        bundle.putStringArrayList(OverviewFragment.EXTRA_LONGEST_USED_APP_NAME_LIST, getTopUsedAppNameList(NUMBER_OF_TOP_USED_APP_COUNT));
        bundle.putInt(OverviewFragment.EXTRA_CHARACTER_TYPE, appUsageDataHelper.getCharacterType());
        overviewFragment.setArguments(bundle);

        return overviewFragment;
    }

    private Fragment getBrainFragment() {
        Fragment brainFragment = new BrainFragment();

        ArrayList<String> mostCategoryList = appUsageDataHelper.getMostInstalledCategories(NUMBER_OF_MOST_INSTALLED_CATEGORY);
        int appCount = appInfoList.size();

        Bundle bundle = new Bundle();
        bundle.putStringArrayList(BrainFragment.EXTRA_MOST_INSTALLED_CATEGORIES, mostCategoryList);
        bundle.putStringArrayList(BrainFragment.EXTRA_LEAST_INSTALLED_CATEGORIES, appUsageDataHelper.getLeastInstalledCategories(NUMBER_OF_LEAST_INSTALLED_CATEGORY));
        bundle.putInt(BrainFragment.EXTRA_INSTALLED_APP_COUNT, appCount);
        bundle.putLong(BrainFragment.EXTRA_MOST_INSTALLED_CATEGORY_RATE,
                Math.round((double) appUsageDataHelper.getAppCountByCategoryId(mostCategoryList.get(0)) / appCount * 100));

        brainFragment.setArguments(bundle);

        return brainFragment;
    }

    ArrayList<String> getTopUsedAppNameList(int count) {
        ArrayList<String> top3UsedAppNameList = new ArrayList<>();
        List<AppInfo> topUsedAppList = appInfoList.subList(0, Math.min(count, appInfoList.size()));
        for (AppInfo appInfo : topUsedAppList) {
            top3UsedAppNameList.add(appInfo.getAppName());
        }
        return top3UsedAppNameList;
    }

    int getAppCountType(int appCount) {
        if (appCount < 25) {
            return APP_LIST_COUNT_TYPE.LEAST;
        } else if (appCount < 50) {
            return APP_LIST_COUNT_TYPE.LESS;
        } else if (appCount < 100) {
            return APP_LIST_COUNT_TYPE.NORMAL;
        } else if (appCount < 150) {
            return APP_LIST_COUNT_TYPE.MORE;
        } else {
            return APP_LIST_COUNT_TYPE.MOST;
        }
    }

    int getAppUsageTimeType(double hour) {
        if (hour < 1) {
            return APP_USAGE_TIME_TYPE.LEAST;
        } else if (hour < 2) {
            return APP_USAGE_TIME_TYPE.LESS;
        } else if (hour < 4) {
            return APP_USAGE_TIME_TYPE.NORMAL;
        } else if (hour < 8) {
            return APP_USAGE_TIME_TYPE.MORE;
        } else {
            return APP_USAGE_TIME_TYPE.MOST;
        }
    }
}
