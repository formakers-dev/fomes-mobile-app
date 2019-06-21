package com.formakers.fomes.main.presenter;

import com.formakers.fomes.common.dagger.AnalyticsModule;
import com.formakers.fomes.common.job.JobManager;
import com.formakers.fomes.common.network.EventLogService;
import com.formakers.fomes.common.network.PostService;
import com.formakers.fomes.common.network.UserService;
import com.formakers.fomes.common.network.vo.EventLog;
import com.formakers.fomes.common.repository.dao.UserDAO;
import com.formakers.fomes.common.util.Log;
import com.formakers.fomes.helper.SharedPreferencesHelper;
import com.formakers.fomes.main.contract.EventPagerAdapterContract;
import com.formakers.fomes.main.contract.MainContract;
import com.formakers.fomes.model.User;

import javax.inject.Inject;

import rx.Completable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class MainPresenter implements MainContract.Presenter {

    private static final String TAG = "MainPresenter";

    @Inject UserDAO userDAO;
    @Inject UserService userService;
    @Inject PostService postService;
    @Inject EventLogService eventLogService;
    @Inject AnalyticsModule.Analytics analytics;
    @Inject SharedPreferencesHelper sharedPreferencesHelper;

    @Inject JobManager jobManager;

    private User userInfo;
    private MainContract.View view;
    private EventPagerAdapterContract.Model adapterModel;

    private CompositeSubscription compositeSubscription = new CompositeSubscription();

    public MainPresenter(MainContract.View view) {
        this.view = view;
        this.view.getApplicationComponent().inject(this);
    }

    // temporary code for test
    MainPresenter(MainContract.View view, UserDAO userDAO, UserService userService, EventLogService eventLogService, JobManager jobManager, SharedPreferencesHelper sharedPreferencesHelper) {
        this.view = view;
        this.userDAO = userDAO;
        this.userService = userService;
        this.jobManager = jobManager;
        this.eventLogService = eventLogService;
        this.sharedPreferencesHelper = sharedPreferencesHelper;
    }

    @Override
    public AnalyticsModule.Analytics getAnalytics() {
        return this.analytics;
    }

    @Override
    public void setAdapterModel(EventPagerAdapterContract.Model adapterModel) { this.adapterModel = adapterModel; }

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
                        this.adapterModel.addAll(promotions);
                        this.view.refreshEventPager();
                    },
                    e -> Log.e(TAG, String.valueOf(e))
                )
        );
    }

    @Override
    public int getPromotionCount() {
        return adapterModel.getCount();
    }

    @Override
    public void unsubscribe() {
        if (compositeSubscription != null) {
            compositeSubscription.clear();
        }
    }
}
