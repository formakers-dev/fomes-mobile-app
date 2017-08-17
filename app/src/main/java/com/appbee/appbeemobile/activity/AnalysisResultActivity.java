package com.appbee.appbeemobile.activity;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.appbee.appbeemobile.AppBeeApplication;
import com.appbee.appbeemobile.R;
import com.appbee.appbeemobile.fragment.OverviewFragment;
import com.appbee.appbeemobile.helper.AppUsageDataHelper;
import com.appbee.appbeemobile.model.NativeAppInfo;

import java.util.List;

import javax.inject.Inject;

public class AnalysisResultActivity extends Activity {
    final String OVERVIEW_FRAGMENT_TAG = "OVERVIEW_FRAGMENT_TAG";

    @Inject
    AppUsageDataHelper appUsageDataHelper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_analysis_result);

        ((AppBeeApplication)getApplication()).getComponent().inject(this);

        List<NativeAppInfo> appInfoList = appUsageDataHelper.getAppList();

        Fragment overviewFragment = new OverviewFragment();
        Bundle bundle = new Bundle();
        int appCount = appInfoList.size();
        bundle.putInt(OverviewFragment.EXTRA_APP_LIST_COUNT, appCount);
        bundle.putString(OverviewFragment.EXTRA_APP_LIST_COUNT_MSG, getString(appUsageDataHelper.getAppCountMessage(appCount)));
        overviewFragment.setArguments(bundle);

        getFragmentManager().beginTransaction()
                .add(R.id.overview_fragment, overviewFragment, OVERVIEW_FRAGMENT_TAG)
                .commit();
    }
}
