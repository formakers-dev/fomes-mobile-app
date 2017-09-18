package com.appbee.appbeemobile.activity;

import android.app.Fragment;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.appbee.appbeemobile.AppBeeApplication;
import com.appbee.appbeemobile.BuildConfig;
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
import com.appbee.appbeemobile.model.AnalysisResult;
import com.appbee.appbeemobile.model.AppInfo;
import com.appbee.appbeemobile.model.LongTermStat;
import com.appbee.appbeemobile.model.NativeAppInfo;
import com.appbee.appbeemobile.model.User;
import com.appbee.appbeemobile.network.AppStatService;
import com.appbee.appbeemobile.network.UserService;
import com.appbee.appbeemobile.repository.helper.AppRepositoryHelper;
import com.appbee.appbeemobile.service.PowerConnectedService;
import com.appbee.appbeemobile.util.AppBeeConstants.APP_LIST_COUNT_TYPE;
import com.appbee.appbeemobile.util.AppBeeConstants.APP_USAGE_TIME_TYPE;
import com.appbee.appbeemobile.util.AppBeeConstants.Category;
import com.appbee.appbeemobile.util.AppBeeConstants.CharacterType;
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

    private int totalAppCount = 0;
    private AppInfo mostUsedAppInfo;
    private AnalysisResult analysisResult = new AnalysisResult();
    private CharacterType characterType;

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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_analysis_result);

        ((AppBeeApplication) getApplication()).getComponent().inject(this);

        List<AppInfo> appInfoList = appUsageDataHelper.getSortedUsedAppsByTotalUsedTime();

        if (appInfoList != null && !appInfoList.isEmpty()) {
            totalAppCount = appInfoList.size();
            mostUsedAppInfo = appInfoList.get(0);
        }

        totalAppCount = appUsageDataHelper.getSortedUsedAppsByTotalUsedTime().size();

        getFragmentManager().beginTransaction()
                .add(R.id.overview_fragment, getOverviewFragment(), OVERVIEW_FRAGMENT_TAG)
                .add(R.id.brain_fragment, getBrainFragment(), BRAIN_FRAGMENT_TAG)
                .add(R.id.flower_fragment, getFlowerFragment(), FLOWER_FRAGMENT_TAG)
                .add(R.id.share_fragment, getShareFragment(), SHARE_FRAGMENT_TAG)
                .commit();

        userService.sendAppList();
        appStatService.sendLongTermStatsFor3Months();
        appStatService.sendLongTermStatsFor2Years();
        appStatService.sendShortTermStats();
        appStatService.sendAnalysisResult(analysisResult);

        startService(new Intent(this, PowerConnectedService.class));
    }

    private Fragment getOverviewFragment() {
        Fragment overviewFragment = new OverviewFragment();

        Bundle bundle = new Bundle();
        bundle.putInt(OverviewFragment.EXTRA_APP_LIST_COUNT, totalAppCount);
        bundle.putInt(OverviewFragment.EXTRA_APP_LIST_COUNT_TYPE, getAppCountType(totalAppCount));

        List<LongTermStat> longTermStatList = appUsageDataHelper.getLongTermStats();
        int appUsageAverageMinutesPerDay = appUsageDataHelper.getAppUsageAverageMinutesPerDay(longTermStatList);
        bundle.putInt(OverviewFragment.EXTRA_APP_AVG_TIME, appUsageAverageMinutesPerDay);
        bundle.putInt(OverviewFragment.EXTRA_APP_USAGE_TIME_TYPE, getAppUsageTimeType(appUsageAverageMinutesPerDay));
        bundle.putInt(OverviewFragment.EXTRA_APP_AVG_TIME_SHORT, appUsageDataHelper.getAppUsageAverageMinutesPerDayOfShortTermStats());

        if (mostUsedAppInfo != null) {
            bundle.putString(OverviewFragment.EXTRA_LONGEST_USED_APP_NAME, mostUsedAppInfo.getAppName());
            bundle.putString(OverviewFragment.EXTRA_LONGEST_USED_APP_DESCRIPTION, getLongestUsedAppDescription(mostUsedAppInfo.getCategoryId1()));
            NativeAppInfo nativeAppInfo = nativeAppInfoHelper.getNativeAppInfo(mostUsedAppInfo.getPackageName());
            if (nativeAppInfo.getIcon() != null) {
                bundle.putParcelable(OverviewFragment.EXTRA_LONGEST_USED_APP_ICON_BITMAP, ((BitmapDrawable) nativeAppInfo.getIcon()).getBitmap());
            }

            analysisResult.setMostUsedApp(mostUsedAppInfo.getPackageName());
        }

        characterType = appUsageDataHelper.getCharacterType();
        bundle.putSerializable(OverviewFragment.EXTRA_CHARACTER_TYPE, characterType);
        overviewFragment.setArguments(bundle);

        analysisResult.setCharacterType(characterType.name());
        analysisResult.setTotalInstalledAppCount(totalAppCount);
        analysisResult.setAverageUsedMinutesPerDay(appUsageAverageMinutesPerDay);

        return overviewFragment;
    }

    private Fragment getBrainFragment() {
        Fragment brainFragment = new BrainFragment();

        List<String> mostInstalledCategoryList = appUsageDataHelper.getMostInstalledCategoryGroups(NUMBER_OF_MOST_INSTALLED_CATEGORY);

        Bundle bundle = new Bundle();
        ArrayList<String> leastInstalledCategoryList = appUsageDataHelper.getLeastInstalledCategoryGroups(NUMBER_OF_LEAST_INSTALLED_CATEGORY);
        bundle.putStringArrayList(BrainFragment.EXTRA_MOST_INSTALLED_CATEGORIES, getCategoryNameList(mostInstalledCategoryList, NUMBER_OF_MOST_INSTALLED_CATEGORY));

        if (mostInstalledCategoryList.containsAll(leastInstalledCategoryList)) {
            bundle.putStringArrayList(BrainFragment.EXTRA_LEAST_INSTALLED_CATEGORIES, getCategoryNameList(leastInstalledCategoryList, 0));
        } else {
            bundle.putStringArrayList(BrainFragment.EXTRA_LEAST_INSTALLED_CATEGORIES, getCategoryNameList(leastInstalledCategoryList, NUMBER_OF_LEAST_INSTALLED_CATEGORY));
        }

        if (mostInstalledCategoryList.size() >= 3) {
            String categoryId = mostInstalledCategoryList.get(0);
            String categoryName = Category.fromId(categoryId).categoryName;
            int rate = (int) Math.round((double) appUsageDataHelper.getAppCountByCategoryId(mostInstalledCategoryList.get(0)) / totalAppCount * 100);
            bundle.putString(BrainFragment.EXTRA_MOST_INSTALLED_CATEGORY_SUMMARY, String.format(getString(R.string.category_count_summary_format_string), categoryName, rate));
            bundle.putString(BrainFragment.EXTRA_MOST_INSTALLED_CATEGORY_DESCRIPTION, getString(Category.fromId(categoryId).description));
        } else {
            bundle.putString(BrainFragment.EXTRA_MOST_INSTALLED_CATEGORY_SUMMARY, getString(R.string.brain_summary_not_enough_data));
            bundle.putString(BrainFragment.EXTRA_MOST_INSTALLED_CATEGORY_DESCRIPTION, getString(R.string.brain_desc_not_enough_data));
        }

        brainFragment.setArguments(bundle);

        analysisResult.setMostDownloadCategories(mostInstalledCategoryList);
        if (leastInstalledCategoryList.size() > 0) {
            analysisResult.setLeastDownloadCategory(leastInstalledCategoryList.get(0));
        }

        return brainFragment;
    }

    private Fragment getFlowerFragment() {
        Fragment flowerFragment = new FlowerFragment();

        Bundle bundle = new Bundle();
        Map<String, Long> usedTimeCategoryMap = appUsageDataHelper.getSortedCategoriesByUsedTime();
        ArrayList<String> usedTimeCategoryKeyList = Lists.newArrayList(usedTimeCategoryMap.keySet());

        ArrayList<String> mostUsedTimeCategoryList = getCategoryNameList(usedTimeCategoryKeyList, NUMBER_OF_MOST_USED_TIME_CATEGORY);
        ArrayList<String> leastUsedTimeCategoryList = getCategoryNameList(Lists.reverse(usedTimeCategoryKeyList), NUMBER_OF_LEAST_USED_TIME_CATEGORY);

        if (usedTimeCategoryKeyList.size() >= 3) {
            bundle.putStringArrayList(FlowerFragment.EXTRA_MOST_USED_TIME_CATEGORIES, mostUsedTimeCategoryList);

            String mostUsedCategoryId = usedTimeCategoryKeyList.get(0);
            int rate = (int) getCategoryRate(usedTimeCategoryMap, mostUsedCategoryId);
            bundle.putString(FlowerFragment.EXTRA_MOST_USED_TIME_CATEGORY_SUMMARY, String.format(getString(R.string.category_time_summary_format_string), Category.fromId(mostUsedCategoryId).categoryName, rate));

            List<String> mostInstalledCategoryList = appUsageDataHelper.getMostInstalledCategoryGroups(NUMBER_OF_MOST_INSTALLED_CATEGORY);

            if (isMostInstalledAndMostUsedCategorySame(mostUsedCategoryId, mostInstalledCategoryList)) {
                bundle.putString(FlowerFragment.EXTRA_MOST_USED_TIME_CATEGORY_DESC, getString(R.string.flower_desc_same_category));
            } else {
                bundle.putString(FlowerFragment.EXTRA_MOST_USED_TIME_CATEGORY_DESC, getMostUsedCategoryDesc(mostUsedCategoryId));
            }

            if (usedTimeCategoryKeyList.size() == 3) {
                bundle.putStringArrayList(FlowerFragment.EXTRA_LEAST_USED_TIME_CATEGORIES, new ArrayList<>());
            } else {
                bundle.putStringArrayList(FlowerFragment.EXTRA_LEAST_USED_TIME_CATEGORIES, leastUsedTimeCategoryList);
            }
        } else {
            bundle.putStringArrayList(FlowerFragment.EXTRA_MOST_USED_TIME_CATEGORIES, new ArrayList<>());
            bundle.putStringArrayList(FlowerFragment.EXTRA_LEAST_USED_TIME_CATEGORIES, new ArrayList<>());
            bundle.putString(FlowerFragment.EXTRA_MOST_USED_TIME_CATEGORY_SUMMARY, getString(R.string.flower_summary_not_enough_data));
            bundle.putString(FlowerFragment.EXTRA_MOST_USED_TIME_CATEGORY_DESC, getString(R.string.flower_desc_not_enough_data));
        }
        flowerFragment.setArguments(bundle);

        analysisResult.setMostUsedCategories(usedTimeCategoryKeyList);
        if (usedTimeCategoryKeyList.size() > 0) {
            analysisResult.setLeastUsedCategory(Lists.reverse(usedTimeCategoryKeyList).get(0));
        }

        return flowerFragment;
    }

    private Fragment getShareFragment() {
        Fragment shareFragment = new ShareFragment();

        Bundle bundle = new Bundle();
        bundle.putSerializable(ShareFragment.EXTRA_CHARACTER_TYPE, characterType);

        shareFragment.setArguments(bundle);
        return shareFragment;
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
