package com.formakers.fomes.main;

import android.util.Pair;

import com.formakers.fomes.common.dagger.AnalyticsModule;
import com.formakers.fomes.common.helper.FomesUrlHelper;
import com.formakers.fomes.common.helper.ImageLoader;
import com.formakers.fomes.common.job.JobManager;
import com.formakers.fomes.common.network.EventLogService;
import com.formakers.fomes.common.network.PostService;
import com.formakers.fomes.common.network.UserService;
import com.formakers.fomes.common.network.vo.EventLog;
import com.formakers.fomes.common.util.Log;

import javax.inject.Inject;
import javax.inject.Named;

import rx.Completable;
import rx.Single;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
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
                  ImageLoader imageLoader) {
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
    public void bindUserInfo() {
        userEmail.zipWith(userNickName, Pair::new)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(emailNickNamePair ->
                        this.view.setUserInfoToNavigationView(emailNickNamePair.first, emailNickNamePair.second),
                        e -> Log.e(TAG, String.valueOf(e)));
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
