package com.formakers.fomes.common.job;

import android.app.job.JobParameters;
import android.app.job.JobService;

import com.formakers.fomes.FomesApplication;
import com.formakers.fomes.common.network.AppStatService;
import com.formakers.fomes.common.network.UserService;
import com.formakers.fomes.common.noti.ChannelManager;
import com.formakers.fomes.common.repository.dao.UserDAO;
import com.formakers.fomes.common.util.Log;
import com.formakers.fomes.helper.AndroidNativeHelper;
import com.formakers.fomes.helper.AppUsageDataHelper;
import com.formakers.fomes.helper.SharedPreferencesHelper;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import rx.Completable;
import rx.Observable;
import rx.Subscription;
import rx.schedulers.Schedulers;

public class SendDataJobService extends JobService {

    private static String TAG = SendDataJobService.class.getSimpleName();

    @Inject SharedPreferencesHelper SharedPreferencesHelper;
    @Inject AndroidNativeHelper androidNativeHelper;
    @Inject AppUsageDataHelper appUsageDataHelper;
    @Inject UserService userService;
    @Inject AppStatService appStatService;
    @Inject ChannelManager channelManager;
    @Inject UserDAO userDAO;

    private Subscription subscription;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, " onCreate : " + this);

        ((FomesApplication) getApplication()).getComponent().inject(this);
    }

    @Override
    public boolean onStartJob(JobParameters params) {
        Log.d(TAG, "[" + params.getJobId() + "] onStartJob : " + this);

        if (params.isOverrideDeadlineExpired()) {
            // Job의 실행 조건이 만족하지 않은 상태에서 DeadLine에 걸려 실행된 경우
            Log.d(TAG, "[" + params.getJobId() + "] isOverrideDeadlineExpired!");
        }

        // 1. 공지용 전체 채널 구독시키기
        channelManager.subscribePublicTopic();

        List<Completable> completableList = new ArrayList<>();

        // 2. 백업용 : 유저정보 서버로 올리기
        completableList.add(userDAO.getUserInfo()
                .observeOn(Schedulers.io())
                .flatMapCompletable(user -> userService.updateUser(user)));

        // 3. 앱 사용 정보 접근 권한이 있을 때 : 앱 사용 데이터를 서버로 보낸다
        if (androidNativeHelper.hasUsageStatsPermission()) {
            Log.d(TAG, "Start to update data!");

            completableList.add(appUsageDataHelper.sendShortTermStats()
                    .doOnSubscribe(a -> Log.i(TAG, "sendShortTermStats) onSubscribe"))
                    .doOnCompleted(() -> Log.i(TAG, "sendShortTermStats) onCompleted"))
            );
            completableList.add(appStatService.sendAppUsages(
                    appUsageDataHelper.getAppUsagesFor(AppUsageDataHelper.DEFAULT_APP_USAGE_DURATION_DAYS)
            ));
        }

        subscription = Completable.merge(Observable.from(completableList))
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .doAfterTerminate(() -> {
                    Log.i(TAG, "doAfterTerminate");
                    this.jobFinished(params, true);
                })
                .subscribe(() -> {
                            Log.i(TAG, "Success!");
                        }, e -> Log.e(TAG, "Fail error=" + String.valueOf(e)));

        return true;
    }


    @Override
    public void onDestroy() {
        Log.i(TAG, "onDestroy");

        subscription.unsubscribe();

        super.onDestroy();
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        Log.d(TAG, "[" + params.getJobId() + "] onStopJob");

        // Job 실행 도중 실행 조건이 해제되었을 경우, onStopJob()이 호출됨.
        // 예외처리 필요

        // true를 리턴하면 재스케쥴링 한다.
        return true;
    }
}
