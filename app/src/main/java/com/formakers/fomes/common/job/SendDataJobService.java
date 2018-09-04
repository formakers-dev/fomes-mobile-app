package com.formakers.fomes.common.job;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.util.Log;

import com.formakers.fomes.AppBeeApplication;
import com.formakers.fomes.helper.AppBeeAndroidNativeHelper;
import com.formakers.fomes.helper.AppUsageDataHelper;
import com.formakers.fomes.helper.LocalStorageHelper;
import com.formakers.fomes.helper.MessagingHelper;
import com.formakers.fomes.network.UserService;

import javax.inject.Inject;

import rx.Completable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class SendDataJobService extends JobService {

    private static String TAG = SendDataJobService.class.getSimpleName();

    @Inject LocalStorageHelper localStorageHelper;
    @Inject AppBeeAndroidNativeHelper appBeeAndroidNativeHelper;
    @Inject AppUsageDataHelper appUsageDataHelper;
    @Inject MessagingHelper messagingHelper;
    @Inject UserService userService;

    @Override
    public void onCreate() {
        super.onCreate();

        ((AppBeeApplication) getApplication()).getComponent().inject(this);
    }

    @Override
    public boolean onStartJob(JobParameters params) {
        Log.d(TAG, "[" + params.getJobId() + "] onStartJob : " + this);

        if (params.isOverrideDeadlineExpired()) {
            // Job의 실행 조건이 만족하지 않은 상태에서 DeadLine에 걸려 실행된 경우
            Log.d(TAG, "[" + params.getJobId() + "] isOverrideDeadlineExpired!");
        }

        if (appBeeAndroidNativeHelper.hasUsageStatsPermission()) {
            Log.d(TAG, "Start to update data!");

            // 노티 토큰 업데이트 로직 추가 - onRefreshToken 에서 에러난 경우에 대한 대비책
            final String refreshedToken = messagingHelper.getMessagingToken();
            if (!localStorageHelper.getRegistrationToken().equals(refreshedToken)) {
                userService.updateRegistrationToken(refreshedToken)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(() -> {
                            Log.d(TAG, "Token Refresh is Completed!");
                            localStorageHelper.setRegistrationToken(refreshedToken);
                        }, (throwable) -> Log.e(TAG, throwable.toString()));
            }

            Completable.merge(appUsageDataHelper.sendShortTermStats(), appUsageDataHelper.sendAppUsages())
                    .subscribeOn(Schedulers.io())
                    .observeOn(Schedulers.io())
                    .doAfterTerminate(() -> this.jobFinished(params, true))
                    .subscribe(() -> Log.d(TAG, "Send Data Success"), e -> Log.e(TAG, "Send Data Fail : " + e));
        }

        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        Log.d(TAG, "[" + params.getJobId() + "] onStopJob");

        // Job 실행 도중 실행 조건이 해제되었을 경우, onStopJob()이 호출됨.
        // 예외처리 필요

        return true;
    }
}
