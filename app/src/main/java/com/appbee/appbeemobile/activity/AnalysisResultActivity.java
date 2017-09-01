package com.appbee.appbeemobile.activity;

import android.app.Fragment;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.appbee.appbeemobile.AppBeeApplication;
import com.appbee.appbeemobile.R;
import com.appbee.appbeemobile.fragment.BrainFragment;
import com.appbee.appbeemobile.fragment.FlowerFragment;
import com.appbee.appbeemobile.fragment.OverviewFragment;
import com.appbee.appbeemobile.fragment.ShareFragment;
import com.appbee.appbeemobile.helper.AppUsageDataHelper;
import com.appbee.appbeemobile.helper.LocalStorageHelper;
import com.appbee.appbeemobile.helper.NativeAppInfoHelper;
import com.appbee.appbeemobile.helper.ShareSnsHelper;
import com.appbee.appbeemobile.helper.TimeHelper;
import com.appbee.appbeemobile.model.AppInfo;
import com.appbee.appbeemobile.model.LongTermStat;
import com.appbee.appbeemobile.model.NativeAppInfo;
import com.appbee.appbeemobile.model.User;
import com.appbee.appbeemobile.network.AppStatService;
import com.appbee.appbeemobile.network.UserService;
import com.appbee.appbeemobile.repository.helper.AppRepositoryHelper;
import com.appbee.appbeemobile.util.AppBeeConstants.*;
import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.inject.Inject;

public class AnalysisResultActivity extends BaseActivity {
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

    @Inject
    LocalStorageHelper localStorageHelper;

    @Inject
    TimeHelper timeHelper;

    @Inject
    UserService userService;

    @Inject
    AppStatService appStatService;

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

        if (TextUtils.isEmpty(localStorageHelper.getUUID())) {
            String uuid = UUID.randomUUID().toString();
            localStorageHelper.setUUID(uuid);

            String currentDate = timeHelper.getFormattedCurrentTime(TimeHelper.DATE_FORMAT);
            User user = new User(localStorageHelper.getUUID(), currentDate, currentDate);
            userService.sendUser(user);
        }

        appStatService.sendLongTermStatsFor2Years();
        appStatService.sendShortTermStats();
        appStatService.sendEventStats();
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

        if (appInfoList != null && !appInfoList.isEmpty()) {
            AppInfo longestUsedAppInfo = appInfoList.get(0);
            bundle.putString(OverviewFragment.EXTRA_LONGEST_USED_APP_NAME, longestUsedAppInfo.getAppName());
            bundle.putString(OverviewFragment.EXTRA_LONGEST_USED_APP_DESCRIPTION, getLongestUsedAppDescription(longestUsedAppInfo.getCategoryId1()));
            NativeAppInfo nativeAppInfo = nativeAppInfoHelper.getNativeAppInfo(longestUsedAppInfo.getPackageName());
            if (nativeAppInfo.getIcon() != null) {
                bundle.putParcelable(OverviewFragment.EXTRA_LONGEST_USED_APP_ICON_BITMAP, ((BitmapDrawable) nativeAppInfo.getIcon()).getBitmap());
            }
        }

        bundle.putInt(OverviewFragment.EXTRA_CHARACTER_TYPE, appUsageDataHelper.getCharacterType());
        overviewFragment.setArguments(bundle);

        return overviewFragment;
    }

    private Fragment getBrainFragment() {
        Fragment brainFragment = new BrainFragment();

        List<String> mostInstalledCategoryList = appUsageDataHelper.getMostInstalledCategoryGroups(NUMBER_OF_MOST_INSTALLED_CATEGORY);
        int appCount = appInfoList.size();

        Bundle bundle = new Bundle();
        ArrayList<String> leastInstalledCategory = appUsageDataHelper.getLeastInstalledCategoryGroups(NUMBER_OF_LEAST_INSTALLED_CATEGORY);
        bundle.putStringArrayList(BrainFragment.EXTRA_MOST_INSTALLED_CATEGORIES, getCategoryNameList(mostInstalledCategoryList, NUMBER_OF_MOST_INSTALLED_CATEGORY));
        bundle.putStringArrayList(BrainFragment.EXTRA_LEAST_INSTALLED_CATEGORIES, getCategoryNameList(leastInstalledCategory, NUMBER_OF_LEAST_INSTALLED_CATEGORY));
        if (mostInstalledCategoryList != null && mostInstalledCategoryList.size() >= 3) {
            String categoryId = mostInstalledCategoryList.get(0);
            String categoryName = Category.fromId(categoryId).categoryName;
            int rate = (int) Math.round((double) appUsageDataHelper.getAppCountByCategoryId(mostInstalledCategoryList.get(0)) / appCount * 100);
            bundle.putString(BrainFragment.EXTRA_MOST_INSTALLED_CATEGORY_SUMMARY, String.format(getString(R.string.category_count_summary_format_string), categoryName, rate));
            bundle.putString(BrainFragment.EXTRA_MOST_INSTALLED_CATEGORY_DESCRIPTION, getString(Category.fromId(categoryId).description));
        } else {
            bundle.putString(BrainFragment.EXTRA_MOST_INSTALLED_CATEGORY_SUMMARY, getString(R.string.brain_summary_not_enough_data));
            bundle.putString(BrainFragment.EXTRA_MOST_INSTALLED_CATEGORY_DESCRIPTION, getString(R.string.brain_desc_not_enough_data));
        }

        brainFragment.setArguments(bundle);

        return brainFragment;
    }

    private Fragment getFlowerFragment() {
        Fragment flowerFragment = new FlowerFragment();

        Bundle bundle = new Bundle();
        Map<String, Long> usedTimeCategoryMap = appUsageDataHelper.getSortedCategoriesByUsedTime();
        ArrayList<String> usedTimeCategoryKeyList = Lists.newArrayList(usedTimeCategoryMap.keySet());

        ArrayList<String> mostUsedTimeCategoryList = getCategoryNameList(usedTimeCategoryKeyList, NUMBER_OF_MOST_USED_TIME_CATEGORY);
        ArrayList<String> leastUsedTimeCategoryList = getCategoryNameList(Lists.reverse(usedTimeCategoryKeyList), NUMBER_OF_LEAST_USED_TIME_CATEGORY);

        bundle.putStringArrayList(FlowerFragment.EXTRA_MOST_USED_TIME_CATEGORIES, mostUsedTimeCategoryList);
        bundle.putStringArrayList(FlowerFragment.EXTRA_LEAST_USED_TIME_CATEGORIES, leastUsedTimeCategoryList);
        if (usedTimeCategoryKeyList.size() >= 3) {
            String mostUsedCategoryId = usedTimeCategoryKeyList.get(0);
            int rate = (int) getCategoryRate(usedTimeCategoryMap, mostUsedCategoryId);
            bundle.putString(FlowerFragment.EXTRA_MOST_USED_TIME_CATEGORY_SUMMARY, String.format(getString(R.string.category_time_summary_format_string), Category.fromId(mostUsedCategoryId).categoryName, rate));

            List<String> mostInstalledCategoryList = appUsageDataHelper.getMostInstalledCategoryGroups(NUMBER_OF_MOST_INSTALLED_CATEGORY);

            if (isMostInstalledAndMostUsedCategorySame(mostUsedCategoryId, mostInstalledCategoryList)) {
                bundle.putString(FlowerFragment.EXTRA_MOST_USED_TIME_CATEGORY_DESC, getString(R.string.flower_desc_same_category));
            } else {
                bundle.putString(FlowerFragment.EXTRA_MOST_USED_TIME_CATEGORY_DESC, getMostUsedCategoryDesc(mostUsedCategoryId));
            }
        } else {
            bundle.putString(FlowerFragment.EXTRA_MOST_USED_TIME_CATEGORY_SUMMARY, getString(R.string.flower_summary_not_enough_data));
            bundle.putString(FlowerFragment.EXTRA_MOST_USED_TIME_CATEGORY_DESC, getString(R.string.flower_desc_not_enough_data));
        }
        flowerFragment.setArguments(bundle);

        return flowerFragment;
    }

    private boolean isMostInstalledAndMostUsedCategorySame(String mostUsedCategoryId, List<String> mostInstalledCategoryList) {
        return mostInstalledCategoryList != null && !mostInstalledCategoryList.isEmpty() && mostUsedCategoryId.equals(mostInstalledCategoryList.get(0));
    }

    String getMostUsedCategoryDesc(String categoryId) {
        return getString(Category.fromId(categoryId).description);
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
    private ArrayList<String> getCategoryNameList(List<String> usedTimeCategoryList, int count) {
        ArrayList<String> categoryIdList = Lists.newArrayList(Lists.newArrayList(usedTimeCategoryList).subList(0, Math.min(count, usedTimeCategoryList.size())));
        ArrayList<String> categoryNameList = new ArrayList<>();
        for (String categoryId : categoryIdList) {
            categoryNameList.add(Category.fromId(categoryId).categoryName);
        }
        return categoryNameList;
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
        return getString(Category.fromId(categoryId).appDescription);
    }
}
