package com.appbee.appbeemobile.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.appbee.appbeemobile.AppBeeApplication;
import com.appbee.appbeemobile.R;
import com.appbee.appbeemobile.helper.AppBeeAndroidNativeHelper;
import com.appbee.appbeemobile.helper.AppUsageDataHelper;
import com.appbee.appbeemobile.helper.LocalStorageHelper;
import com.appbee.appbeemobile.model.AppInfo;
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
    AppBeeAndroidNativeHelper appBeeAndroidNativeHelper;

    @Inject
    LocalStorageHelper localStorageHelper;

    @Inject
    UserService userService;

    private List<String> usedPackageNameList;

    private boolean isServiceAPIFailAlready = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);

        //Glide.with(this).asGif().load(R.drawable.automated_loading_bowl).into((ImageView) findViewById(R.id.loadingImage));

        ((AppBeeApplication) getApplication()).getComponent().inject(this);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        userService.sendUser(new User(localStorageHelper.getUserId(), localStorageHelper.getRegistrationToken()));

        appStatService.getLastUpdateStatTimestamp()
                .observeOn(Schedulers.io())
                .subscribe(lastUpdateStatTimestamp -> appStatService.sendShortTermStats(lastUpdateStatTimestamp)
                        .observeOn(Schedulers.io())
                        .subscribe(result -> callAppServiceGetInfoAPI(), appStatService::logError), appStatService::logError);
    }

    private void callAppServiceGetInfoAPI() {
        usedPackageNameList = appStatService.getUsedPackageNameList();
        appService.getInfos(usedPackageNameList, appInfosServiceCallback);
    }

    private void moveToAnalysisResultActivity() {
        startActivity(new Intent(LoadingActivity.this, MainActivity.class));
        finish();
    }

    AppService.AppInfosServiceCallback appInfosServiceCallback = new AppService.AppInfosServiceCallback() {
        @Override
        public void onSuccess(List<AppInfo> appInfos) {
            callAppServicePostUncrawledApps(appInfos);

            LoadingActivity.this.runOnUiThread(() -> {
                appRepositoryHelper.insertUsedApps(appInfos);
                appRepositoryHelper.updateTotalUsedTime(appUsageDataHelper.getShortTermStatsTimeSummary());
                moveToAnalysisResultActivity();
            });
        }

        @Override
        public void onFail(String resultCode) {
            if (!isServiceAPIFailAlready) {
                isServiceAPIFailAlready = true;
                callAppServiceGetInfoAPI();
            } else {
                //LoadingActivity.this.runOnUiThread(() -> Toast.makeText(LoadingActivity.this, R.string.app_service_get_info_api_fail, Toast.LENGTH_SHORT).show());
            }
        }
    };

    private void callAppServicePostUncrawledApps(List<AppInfo> appInfos) {
        List<String> uncrawledAppList = Observable.from(usedPackageNameList).filter(packageName -> {
            for (AppInfo app : appInfos) {
                if (app.getPackageName().equals(packageName)) {
                    return false;
                }
            }
            return true;
        }).toList().toBlocking().single();

        if (uncrawledAppList != null && !uncrawledAppList.isEmpty()) {
            appService.postUncrawledApps(uncrawledAppList);
        }
    }
}
