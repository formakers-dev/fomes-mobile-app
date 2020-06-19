package com.formakers.fomes.main;

import android.os.Bundle;

import com.formakers.fomes.BuildConfig;
import com.formakers.fomes.common.constant.FomesConstants;
import com.formakers.fomes.common.dagger.AnalyticsModule;
import com.formakers.fomes.common.helper.FomesUrlHelper;
import com.formakers.fomes.common.helper.ImageLoader;
import com.formakers.fomes.common.helper.SharedPreferencesHelper;
import com.formakers.fomes.common.job.JobManager;
import com.formakers.fomes.common.network.EventLogService;
import com.formakers.fomes.common.network.PostService;
import com.formakers.fomes.common.network.UserService;
import com.formakers.fomes.common.network.vo.EventLog;
import com.formakers.fomes.common.network.vo.RemoteConfigVO;
import com.formakers.fomes.common.util.Log;
import com.formakers.fomes.common.view.FomesNoticeDialog;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.gson.Gson;

import javax.inject.Inject;
import javax.inject.Named;

import rx.Completable;
import rx.Single;
import rx.android.schedulers.AndroidSchedulers;
import rx.subscriptions.CompositeSubscription;

@MainDagger.Scope
public class MainPresenter implements MainContract.Presenter {

    private static final String TAG = "MainPresenter";

    private Single<String> userEmail;
    private Single<String> userNickName;
    private UserService userService;
    private PostService postService;
    private EventLogService eventLogService;
    private AnalyticsModule.Analytics analytics;
    private ImageLoader imageLoader;
    private FirebaseRemoteConfig remoteConfig;
    private SharedPreferencesHelper sharedPreferencesHelper;

    private JobManager jobManager;
    private FomesUrlHelper fomesUrlHelper;

    private MainContract.View view;
    private EventPagerAdapterContract.Model eventPagerAdapterModel;

    private CompositeSubscription compositeSubscription = new CompositeSubscription();

    @Inject
    MainPresenter(MainContract.View view,
                  @Named("userEmail") Single<String> userEmail,
                  @Named("userNickName") Single<String> userNickName,
                  UserService userService,
                  PostService postService,
                  EventLogService eventLogService,
                  JobManager jobManager,
                  FomesUrlHelper fomesUrlHelper,
                  AnalyticsModule.Analytics analytics,
                  ImageLoader imageLoader,
                  FirebaseRemoteConfig remoteConfig,
                  SharedPreferencesHelper sharedPreferencesHelper) {
        this.view = view;
        this.userEmail = userEmail;
        this.userNickName = userNickName;
        this.userService = userService;
        this.postService = postService;
        this.jobManager = jobManager;
        this.eventLogService = eventLogService;
        this.fomesUrlHelper = fomesUrlHelper;
        this.analytics = analytics;
        this.imageLoader = imageLoader;
        this.remoteConfig = remoteConfig;
        this.sharedPreferencesHelper = sharedPreferencesHelper;
    }

    @Override
    public AnalyticsModule.Analytics getAnalytics() {
        return this.analytics;
    }

    @Override
    public ImageLoader getImageLoader() {
        return this.imageLoader = imageLoader;
    }

    @Override
    public void setEventPagerAdapterModel(EventPagerAdapterContract.Model eventPagerAdapterModel) {
        this.eventPagerAdapterModel = eventPagerAdapterModel;
    }

    @Override
    public Completable requestVerifyAccessToken() {
        return userService.verifyToken();
    }

    @Override
    public int registerSendDataJob() {
        return this.jobManager.registerSendDataJob(JobManager.JOB_ID_SEND_DATA);
    }

    @Override
    public boolean checkRegisteredSendDataJob() {
        return jobManager.isRegisteredJob(JobManager.JOB_ID_SEND_DATA);
    }

    @Override
    public void checkNeedToShowMigrationDialog() {
        String migrationNoticeString = this.remoteConfig.getString(FomesConstants.RemoteConfig.MIGRATION_NOTICE);

        Log.v(TAG, "[RemoteConfig] MIGRATION_NOTICE=" + this.remoteConfig.getString(FomesConstants.RemoteConfig.MIGRATION_NOTICE));

        RemoteConfigVO.MigrationNotice migrationNotice = new Gson().fromJson(migrationNoticeString, RemoteConfigVO.MigrationNotice.class);

        if (migrationNotice == null) {
            return;
        }

        if (migrationNotice.getNoticeVersion() > sharedPreferencesHelper.getMigrationNoticeVersion()) {
            boolean isNeedToUpdate = migrationNotice.getVersionCode() > BuildConfig.VERSION_CODE;

            Bundle bundle = new Bundle();

            bundle.putString(FomesNoticeDialog.EXTRA_TITLE, migrationNotice.getTitle());
            bundle.putString(FomesNoticeDialog.EXTRA_SUBTITLE, migrationNotice.getDescription());
            bundle.putString(FomesNoticeDialog.EXTRA_DESCRIPTION, migrationNotice.getGuide());
            bundle.putStringArrayList(FomesNoticeDialog.EXTRA_IMAGE_URL_LIST, migrationNotice.getDescriptionImages());
            bundle.putBoolean("IS_NEED_TO_UPDATE", isNeedToUpdate);
            bundle.putString("POSITIVE_BUTTON_TEXT", isNeedToUpdate ? "업데이트 하러가기" : "확인");

            this.view.showMigrationNoticeDialog(bundle, v -> {
                if (isNeedToUpdate) {
                    this.view.moveToPlayStore();
                }
                this.sharedPreferencesHelper.setMigrationNoticeVersion(migrationNotice.getNoticeVersion());
            });
        }
    }

    @Override
    public void sendEventLog(String code) {
        compositeSubscription.add(
            eventLogService.sendEventLog(new EventLog().setCode(code))
                    .subscribe(() -> Log.d(TAG, "Event log is sent successfully!!"),
                            (e) -> Log.e(TAG, String.valueOf(e)))
        );
    }

    @Override
    public void requestPromotions() {
        compositeSubscription.add(
            postService.getPromotions()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(promotions -> {
                        this.eventPagerAdapterModel.addAll(promotions);
                        this.view.refreshEventPager();
                    },
                    e -> Log.e(TAG, String.valueOf(e))
                )
        );
    }

    @Override
    public int getPromotionCount() {
        return eventPagerAdapterModel.getCount();
    }

    @Override
    public String getInterpretedUrl(String originalUrl) {
        return this.fomesUrlHelper.interpretUrlParams(originalUrl);
    }

    @Override
    public void unsubscribe() {
        if (compositeSubscription != null) {
            compositeSubscription.clear();
        }
    }
}
