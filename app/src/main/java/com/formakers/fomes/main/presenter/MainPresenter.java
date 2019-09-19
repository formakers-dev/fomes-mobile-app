package com.formakers.fomes.main.presenter;

import com.formakers.fomes.common.dagger.AnalyticsModule;
import com.formakers.fomes.common.job.JobManager;
import com.formakers.fomes.common.network.EventLogService;
import com.formakers.fomes.common.network.PostService;
import com.formakers.fomes.common.network.UserService;
import com.formakers.fomes.common.network.vo.EventLog;
import com.formakers.fomes.common.repository.dao.UserDAO;
import com.formakers.fomes.common.util.Log;
import com.formakers.fomes.helper.FomesUrlHelper;
import com.formakers.fomes.helper.SharedPreferencesHelper;
import com.formakers.fomes.main.contract.EventPagerAdapterContract;
import com.formakers.fomes.main.contract.MainContract;
import com.formakers.fomes.main.dagger.MainDagger;
import com.formakers.fomes.model.User;

import javax.inject.Inject;

import rx.Completable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

@MainDagger.Scope
public class MainPresenter implements MainContract.Presenter {

    private static final String TAG = "MainPresenter";

    private UserDAO userDAO;
    private UserService userService;
    private PostService postService;
    private EventLogService eventLogService;
    private AnalyticsModule.Analytics analytics;
    private SharedPreferencesHelper sharedPreferencesHelper;

    private JobManager jobManager;
    private FomesUrlHelper fomesUrlHelper;

    private User userInfo;
    private MainContract.View view;
    private EventPagerAdapterContract.Model eventPagerAdapterModel;

    private CompositeSubscription compositeSubscription = new CompositeSubscription();

    @Inject
    MainPresenter(MainContract.View view, UserDAO userDAO, UserService userService, PostService postService, EventLogService eventLogService, JobManager jobManager, SharedPreferencesHelper sharedPreferencesHelper, FomesUrlHelper fomesUrlHelper, AnalyticsModule.Analytics analytics) {
        this.view = view;
        this.userDAO = userDAO;
        this.userService = userService;
        this.postService = postService;
        this.jobManager = jobManager;
        this.eventLogService = eventLogService;
        this.sharedPreferencesHelper = sharedPreferencesHelper;
        this.fomesUrlHelper = fomesUrlHelper;
        this.analytics = analytics;
    }

    @Override
    public AnalyticsModule.Analytics getAnalytics() {
        return this.analytics;
    }

    @Override
    public void setEventPagerAdapterModel(EventPagerAdapterContract.Model eventPagerAdapterModel) {
        this.eventPagerAdapterModel = eventPagerAdapterModel;
    }

    @Override
    public User getUserInfo() {
        if (userInfo == null) {
            userInfo = userDAO.getUserInfo().observeOn(Schedulers.io()).toBlocking().value();

            // TODO : 임시코드 (DB에만 존재하던 유저 이메일을 sharedPreferences에도 저장시키기)
            sharedPreferencesHelper.setUserEmail(userInfo.getEmail());
        }

        return userInfo;
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
