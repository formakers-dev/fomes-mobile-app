package com.appbee.appbeemobile.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.appbee.appbeemobile.AppBeeApplication;
import com.appbee.appbeemobile.R;
import com.appbee.appbeemobile.helper.AppUsageDataHelper;
import com.appbee.appbeemobile.helper.LocalStorageHelper;
import com.appbee.appbeemobile.helper.TimeHelper;
import com.appbee.appbeemobile.model.ShortTermStat;
import com.appbee.appbeemobile.model.User;
import com.appbee.appbeemobile.network.AppService;
import com.appbee.appbeemobile.network.AppStatService;
import com.appbee.appbeemobile.network.UserService;
import com.appbee.appbeemobile.repository.helper.AppRepositoryHelper;

import java.util.List;

import javax.inject.Inject;

import rx.Observable;
import rx.schedulers.Schedulers;

public class LoadingActivity extends BaseActivity {

    @Inject
    AppStatService appStatService;

    @Inject
    AppService appService;

    @Inject
    AppRepositoryHelper appRepositoryHelper;

    @Inject
    AppUsageDataHelper appUsageDataHelper;

    @Inject
    LocalStorageHelper localStorageHelper;

    @Inject
    UserService userService;

    @Inject
    TimeHelper timeHelper;

    private static final String TAG = LoadingActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);

        ((AppBeeApplication) getApplication()).getComponent().inject(this);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        userService.sendUser(new User(localStorageHelper.getUserId(), localStorageHelper.getEmail(), localStorageHelper.getBirthday(), localStorageHelper.getGender(), localStorageHelper.getRegistrationToken()))
                .observeOn(Schedulers.io())
                .subscribe(result -> sendStatData(), error -> {
                    runOnUiThread(() -> {
                        Toast.makeText(LoadingActivity.this, "사용자 정보 저장에 실패하였습니다.", Toast.LENGTH_SHORT).show();
                    });
                });
    }

    private void sendStatData() {
        appStatService.getLastUpdateStatTimestamp()
                .observeOn(Schedulers.io())
                .subscribe(lastUpdateStatTimestamp -> {
                    final long statBasedEndTime = timeHelper.getStatBasedCurrentTime();
                    final List<ShortTermStat> shortTermStatList = appUsageDataHelper.getShortTermStats(lastUpdateStatTimestamp, statBasedEndTime);

                    appRepositoryHelper.updateTotalUsedTime(appUsageDataHelper.getShortTermStatsTimeSummary(shortTermStatList));

                    Observable.merge(
                            appStatService.sendShortTermStats(shortTermStatList, statBasedEndTime),
                            appService.sendAppUsages(appRepositoryHelper.getAppUsages())
                    ).observeOn(Schedulers.io())
                            .all(result -> true)
                            .subscribe(result -> moveToAnalysisResultActivity(), error -> Log.d(TAG, error.getMessage()));
                }, appStatService::logError);
    }

    private void moveToAnalysisResultActivity() {
        startActivity(new Intent(LoadingActivity.this, OnboardingAnalysisActivity.class));
        finish();
    }
}
