package com.appbee.appbeemobile.activity;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.appbee.appbeemobile.AppBeeApplication;
import com.appbee.appbeemobile.R;
import com.appbee.appbeemobile.fragment.BrainFragment;
import com.appbee.appbeemobile.fragment.FlowerFragment;
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

import static com.appbee.appbeemobile.util.AppBeeConstants.CATEGORY_GROUP;

public class AnalysisResultActivity extends Activity {
    public static final String BRAIN_FRAGMENT_TAG = "BRAIN_FRAGMENT_TAG";
    public static final String OVERVIEW_FRAGMENT_TAG = "OVERVIEW_FRAGMENT_TAG";
    public static final String SHARE_FRAGMENT_TAG = "SHARE_FRAGMENT_TAG";
    public static final String FLOWER_FRAGMENT_TAG = "FLOWER_FRAGMENT_TAG";

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
                .add(R.id.flower_fragment, getFlowerFragment(), FLOWER_FRAGMENT_TAG)
                .add(R.id.share_fragment, new ShareFragment(), SHARE_FRAGMENT_TAG)
                .commit();
    }

    private Fragment getFlowerFragment() {
        Fragment flowerFragment = new FlowerFragment();

        Bundle bundle = new Bundle();
        bundle.putStringArrayList(FlowerFragment.EXTRA_MOST_USED_TIME_CATEGORIES, appUsageDataHelper.getMostUsedTimeCategories(3));
        bundle.putStringArrayList(FlowerFragment.EXTRA_LEAST_USED_TIME_CATEGORIES, appUsageDataHelper.getLeastUsedTimeCategories(1));
        flowerFragment.setArguments(bundle);

        return flowerFragment;
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
        bundle.putString(OverviewFragment.EXTRA_LONGEST_USED_APP_NAME, appInfoList.get(0).getAppName());
        bundle.putString(OverviewFragment.EXTRA_LONGEST_USED_APP_DESCRIPTION, getLongestUsedAppDescription(appInfoList.get(0).getCategoryId1()));
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

    String getLongestUsedAppDescription(String categoryId) {
        switch (categoryId) {
            case "/store/apps/category/FINANCE":
                return getResources().getStringArray(R.array.longest_used_app_category_descriptions)[CATEGORY_GROUP.FINANCE];
            case "/store/apps/category/GAME":
            case "/store/apps/category/GAME_EDUCATIONAL":
            case "/store/apps/category/GAME_WORD":
            case "/store/apps/category/GAME_ROLE_PLAYING":
            case "/store/apps/category/GAME_BOARD":
            case "/store/apps/category/GAME_SPORTS":
            case "/store/apps/category/GAME_SIMULATION":
            case "/store/apps/category/GAME_ARCADE":
            case "/store/apps/category/GAME_ACTION":
            case "/store/apps/category/GAME_ADVENTURE":
            case "/store/apps/category/GAME_MUSIC":
            case "/store/apps/category/GAME_RACING":
            case "/store/apps/category/GAME_STRATEGY":
            case "/store/apps/category/GAME_CARD":
            case "/store/apps/category/GAME_CASINO":
            case "/store/apps/category/GAME_CASUAL":
            case "/store/apps/category/GAME_TRIVIA":
            case "/store/apps/category/GAME_PUZZLE":
                return getResources().getStringArray(R.array.longest_used_app_category_descriptions)[CATEGORY_GROUP.GAME];
            case "/store/apps/category/VIDEO_PLAYERS":
            case "/store/apps/category/MUSIC_AND_AUDIO":
                return getResources().getStringArray(R.array.longest_used_app_category_descriptions)[CATEGORY_GROUP.MUSIC_VIDEO];
            case "/store/apps/category/SOCIAL":
                return getResources().getStringArray(R.array.longest_used_app_category_descriptions)[CATEGORY_GROUP.SOCIAL];
            case "/store/apps/category/PHOTOGRAPHY":
                return getResources().getStringArray(R.array.longest_used_app_category_descriptions)[CATEGORY_GROUP.PHOTOGRAPHY];
            case "/store/apps/category/PERSONALIZATION":
                return getResources().getStringArray(R.array.longest_used_app_category_descriptions)[CATEGORY_GROUP.PERSONALIZATION];
            case "/store/apps/category/SHOPPING":
                return getResources().getStringArray(R.array.longest_used_app_category_descriptions)[CATEGORY_GROUP.SHOPPING];
            case "/store/apps/category/COMMUNICATION":
                return getResources().getStringArray(R.array.longest_used_app_category_descriptions)[CATEGORY_GROUP.COMMUNICATION];
            case "/store/apps/category/ENTERTAINMENT":
                return getResources().getStringArray(R.array.longest_used_app_category_descriptions)[CATEGORY_GROUP.ENTERTAINMENT];
            case "/store/apps/category/HEALTH_AND_FITNESS":
            case "/store/apps/category/SPORTS":
                return getResources().getStringArray(R.array.longest_used_app_category_descriptions)[CATEGORY_GROUP.HEALTH_SPORTS];
            case "/store/apps/category/EDUCATION":
                return getResources().getStringArray(R.array.longest_used_app_category_descriptions)[CATEGORY_GROUP.EDUCATION];
            case "/store/apps/category/WEATHER":
                return getResources().getStringArray(R.array.longest_used_app_category_descriptions)[CATEGORY_GROUP.WEATHER];
            case "/store/apps/category/TRAVEL_AND_LOCAL":
                return getResources().getStringArray(R.array.longest_used_app_category_descriptions)[CATEGORY_GROUP.TRAVEL];
            case "/store/apps/category/BUSINESS":
            case "/store/apps/category/PRODUCTIVITY":
                return getResources().getStringArray(R.array.longest_used_app_category_descriptions)[CATEGORY_GROUP.BUSINESS_PRODUCTIVITY];
            case "/store/apps/category/TOOLS":
                return getResources().getStringArray(R.array.longest_used_app_category_descriptions)[CATEGORY_GROUP.TOOLS];
            case "/store/apps/category/BOOKS_AND_REFERENCE":
            case "/store/apps/category/NEWS_AND_MAGAZINES":
                return getResources().getStringArray(R.array.longest_used_app_category_descriptions)[CATEGORY_GROUP.BOOK_NEWS];
            case "/store/apps/category/LIBRARIES_AND_DEMO":
                return getResources().getStringArray(R.array.longest_used_app_category_descriptions)[CATEGORY_GROUP.LIBRARY];
            case "/store/apps/category/LIFESTYLE":
                return getResources().getStringArray(R.array.longest_used_app_category_descriptions)[CATEGORY_GROUP.LIFESTYLE];
            case "/store/apps/category/COMICS":
                return getResources().getStringArray(R.array.longest_used_app_category_descriptions)[CATEGORY_GROUP.COMICS];
            case "/store/apps/category/HOUSE_AND_HOME":
                return getResources().getStringArray(R.array.longest_used_app_category_descriptions)[CATEGORY_GROUP.HOUSE];
            case "/store/apps/category/BEAUTY":
            case "/store/apps/category/ART_AND_DESIGN":
                return getResources().getStringArray(R.array.longest_used_app_category_descriptions)[CATEGORY_GROUP.BEAUTY_DESIGN];
            case "/store/apps/category/DATING":
                return getResources().getStringArray(R.array.longest_used_app_category_descriptions)[CATEGORY_GROUP.DATING];
            default:
                return "";
        }
    }
}
