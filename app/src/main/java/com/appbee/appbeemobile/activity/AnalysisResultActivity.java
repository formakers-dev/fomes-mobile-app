package com.appbee.appbeemobile.activity;

import android.app.Activity;
import android.app.Fragment;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.appbee.appbeemobile.AppBeeApplication;
import com.appbee.appbeemobile.R;
import com.appbee.appbeemobile.fragment.BrainFragment;
import com.appbee.appbeemobile.fragment.FlowerFragment;
import com.appbee.appbeemobile.fragment.OverviewFragment;
import com.appbee.appbeemobile.fragment.ShareFragment;
import com.appbee.appbeemobile.helper.AppUsageDataHelper;
import com.appbee.appbeemobile.helper.NativeAppInfoHelper;
import com.appbee.appbeemobile.helper.ShareSnsHelper;
import com.appbee.appbeemobile.model.AppInfo;
import com.appbee.appbeemobile.model.LongTermStat;
import com.appbee.appbeemobile.model.NativeAppInfo;
import com.appbee.appbeemobile.repository.helper.AppRepositoryHelper;
import com.appbee.appbeemobile.util.AppBeeConstants.*;
import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

public class AnalysisResultActivity extends Activity {
    public static final String BRAIN_FRAGMENT_TAG = "BRAIN_FRAGMENT_TAG";
    public static final String OVERVIEW_FRAGMENT_TAG = "OVERVIEW_FRAGMENT_TAG";
    public static final String SHARE_FRAGMENT_TAG = "SHARE_FRAGMENT_TAG";
    public static final String FLOWER_FRAGMENT_TAG = "FLOWER_FRAGMENT_TAG";

    private static final int NUMBER_OF_MOST_INSTALLED_CATEGORY = 3;
    private static final int NUMBER_OF_LEAST_INSTALLED_CATEGORY = 1;

    private static final int NUMBER_OF_MOST_USED_TIME_CATEGORY = 3;
    private static final int NUMBER_OF_LEAST_USED_TIME_CATEGORY = 1;
    @Inject
    AppUsageDataHelper appUsageDataHelper;

    @Inject
    AppRepositoryHelper appRepositoryHelper;

    @Inject
    ShareSnsHelper shareSnsHelper;

    @Inject
    NativeAppInfoHelper nativeAppInfoHelper;

    private List<AppInfo> appInfoList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_analysis_result);

        ((AppBeeApplication) getApplication()).getComponent().inject(this);

        appInfoList = appUsageDataHelper.getSortedUsedAppsByTotalUsedTime();

        // TODO : BrainFragment의 가장많이설치된 카테고리와 FlowerFragment의 가장 많이 사용된 카테고리가 동일한 경우 예외 처리
        getFragmentManager().beginTransaction()
                .add(R.id.overview_fragment, getOverviewFragment(), OVERVIEW_FRAGMENT_TAG)
                .add(R.id.brain_fragment, getBrainFragment(), BRAIN_FRAGMENT_TAG)
                .add(R.id.flower_fragment, getFlowerFragment(), FLOWER_FRAGMENT_TAG)
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
        int appUsageAverageMinutesPerDay = appUsageDataHelper.getAppUsageAverageMinutesPerDay(longTermStatList);
        bundle.putInt(OverviewFragment.EXTRA_APP_AVG_TIME, appUsageAverageMinutesPerDay);
        bundle.putInt(OverviewFragment.EXTRA_APP_USAGE_TIME_TYPE, getAppUsageTimeType(appUsageAverageMinutesPerDay));

        AppInfo longestUsedAppInfo = appInfoList.get(0);
        bundle.putString(OverviewFragment.EXTRA_LONGEST_USED_APP_NAME, longestUsedAppInfo.getAppName());
        bundle.putString(OverviewFragment.EXTRA_LONGEST_USED_APP_DESCRIPTION, getLongestUsedAppDescription(longestUsedAppInfo.getCategoryId1()));
        NativeAppInfo nativeAppInfo = nativeAppInfoHelper.getNativeAppInfo(longestUsedAppInfo.getPackageName());
        if (nativeAppInfo.getIcon() != null) {
            bundle.putParcelable(OverviewFragment.EXTRA_LONGEST_USED_APP_ICON_BITMAP, ((BitmapDrawable) nativeAppInfo.getIcon()).getBitmap());
        }

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
        bundle.putString(BrainFragment.EXTRA_MOST_INSTALLED_CATEGORY_DESCRIPTION, getString(Category.fromId(mostCategoryList.get(0)).getMostUsedCategoryDescription()));
        brainFragment.setArguments(bundle);

        return brainFragment;
    }

    private Fragment getFlowerFragment() {
        Fragment flowerFragment = new FlowerFragment();

        Bundle bundle = new Bundle();
        Map<String, Long> usedTimeCategoryMap = appUsageDataHelper.getSortedCategoriesByUsedTime();
        ArrayList<String> usedTimeCategoryKeyList = Lists.newArrayList(usedTimeCategoryMap.keySet());

        ArrayList<String> mostUsedTimeCategoryList = getKeySubListByCount(usedTimeCategoryKeyList, NUMBER_OF_MOST_USED_TIME_CATEGORY);
        ArrayList<String> leastUsedTimeCategoryList = getKeySubListByCount(Lists.reverse(usedTimeCategoryKeyList), NUMBER_OF_LEAST_USED_TIME_CATEGORY);

        bundle.putStringArrayList(FlowerFragment.EXTRA_MOST_USED_TIME_CATEGORIES, mostUsedTimeCategoryList);
        bundle.putStringArrayList(FlowerFragment.EXTRA_LEAST_USED_TIME_CATEGORIES, leastUsedTimeCategoryList);
        bundle.putLong(FlowerFragment.EXTRA_MOST_USED_TIME_CATEGORY_RATE, getCategoryRate(usedTimeCategoryMap, mostUsedTimeCategoryList.get(0)));

        bundle.putString(FlowerFragment.EXTRA_MOST_USED_TIME_CATEGORY_DESC, getMostUsedCategoryDesc(mostUsedTimeCategoryList.get(0)));
        flowerFragment.setArguments(bundle);

        return flowerFragment;
    }

    String getMostUsedCategoryDesc(String categoryId) {
        return getString(Category.fromId(categoryId).getMostUsedCategoryDescription());
    }

    private long getCategoryRate(Map<String, Long> usedTimeCategoryMap, String mostUsedTimeCategoryKey) {
        long totalUsedTime = 0L;
        Set<String> keySet = usedTimeCategoryMap.keySet();
        for (String key : keySet) {
            totalUsedTime += usedTimeCategoryMap.get(key);
        }
        return Math.round((double) usedTimeCategoryMap.get(mostUsedTimeCategoryKey) / totalUsedTime * 100);
    }

    @NonNull
    private ArrayList<String> getKeySubListByCount(List<String> usedTimeCategoryList, int count) {
        return Lists.newArrayList(Lists.newArrayList(usedTimeCategoryList).subList(0, Math.min(count, usedTimeCategoryList.size())));
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

    int getAppUsageTimeType(int minutes) {
        if (minutes < 60) {
            return APP_USAGE_TIME_TYPE.LEAST;
        } else if (minutes < 120) {
            return APP_USAGE_TIME_TYPE.LESS;
        } else if (minutes < 240) {
            return APP_USAGE_TIME_TYPE.NORMAL;
        } else if (minutes < 480) {
            return APP_USAGE_TIME_TYPE.MORE;
        } else {
            return APP_USAGE_TIME_TYPE.MOST;
        }
    }

    String getLongestUsedAppDescription(String categoryId) {
        return getString(Category.fromId(categoryId).getLongestUsedAppDescription());
    }
}
