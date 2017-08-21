package com.appbee.appbeemobile.activity;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.appbee.appbeemobile.AppBeeApplication;
import com.appbee.appbeemobile.R;
import com.appbee.appbeemobile.fragment.BrainFragment;
import com.appbee.appbeemobile.fragment.OverviewFragment;
import com.appbee.appbeemobile.helper.AppBeeAndroidNativeHelper;
import com.appbee.appbeemobile.helper.AppUsageDataHelper;
import com.appbee.appbeemobile.model.LongTermStat;
import com.appbee.appbeemobile.repository.helper.AppRepositoryHelper;

import java.util.List;

import javax.inject.Inject;

public class AnalysisResultActivity extends Activity {
    public static final String BRAIN_FRAGMENT_TAG = "BRAIN_FRAGMENT_TAG";
    public static final String OVERVIEW_FRAGMENT_TAG = "OVERVIEW_FRAGMENT_TAG";

    @Inject
    AppUsageDataHelper appUsageDataHelper;

    @Inject
    AppRepositoryHelper appRepositoryHelper;

    @Inject
    AppBeeAndroidNativeHelper appBeeAndroidNativeHelper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_analysis_result);

        ((AppBeeApplication)getApplication()).getComponent().inject(this);

        getFragmentManager().beginTransaction()
                .add(R.id.overview_fragment, getOverviewFragment(), OVERVIEW_FRAGMENT_TAG)
                .add(R.id.brain_fragment, new BrainFragment(), BRAIN_FRAGMENT_TAG)
                .commit();
    }

    private Fragment getOverviewFragment() {
        Fragment overviewFragment = new OverviewFragment();

        int appCount = appRepositoryHelper.getTotalUsedApps();

        Bundle bundle = new Bundle();
        bundle.putInt(OverviewFragment.EXTRA_APP_LIST_COUNT, appCount);
        bundle.putString(OverviewFragment.EXTRA_APP_LIST_COUNT_MSG, getString(appUsageDataHelper.getAppCountMessage(appCount)));

        List<LongTermStat> longTermStatList = appUsageDataHelper.getLongTermStats();
        int appUsageAverageHourPerDay = appUsageDataHelper.getAppUsageAverageHourPerDay(longTermStatList);
        bundle.putInt(OverviewFragment.EXTRA_APP_AVG_TIME, appUsageAverageHourPerDay);
        bundle.putString(OverviewFragment.EXTRA_APP_USAGE_AVG_TIME_MSG, getString(appUsageDataHelper.getAppUsageAverageMessage(appUsageAverageHourPerDay)));

        LongTermStat longTermStat = longTermStatList.get(0);
        String longestUsedPackageName = longTermStat.getPackageName();
        bundle.putString(OverviewFragment.EXTRA_LONGEST_USED_APP_NAME, appBeeAndroidNativeHelper.getAppName(longestUsedPackageName));

        overviewFragment.setArguments(bundle);

        return overviewFragment;
    }
}
