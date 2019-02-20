package com.formakers.fomes.main.presenter;

import com.formakers.fomes.common.job.JobManager;
import com.formakers.fomes.common.network.EventLogService;
import com.formakers.fomes.common.network.PostService;
import com.formakers.fomes.common.network.UserService;
import com.formakers.fomes.common.network.vo.EventLog;
import com.formakers.fomes.common.repository.dao.UserDAO;
import com.formakers.fomes.common.util.Log;
import com.formakers.fomes.main.contract.MainContract;
import com.formakers.fomes.model.User;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import rx.Completable;
import rx.Observable;
import rx.Single;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.subscriptions.CompositeSubscription;

public class MainPresenter implements MainContract.Presenter {

    private static final String TAG = "MainPresenter";

    @Inject UserDAO userDAO;
    @Inject UserService userService;
    @Inject PostService postService;
    @Inject EventLogService eventLogService;

    @Inject JobManager jobManager;

    private MainContract.View view;

    private CompositeSubscription compositeSubscription = new CompositeSubscription();

    public MainPresenter(MainContract.View view) {
        this.view = view;
        this.view.getApplicationComponent().inject(this);
    }

    // temporary code for test
    MainPresenter(MainContract.View view, UserDAO userDAO, UserService userService, EventLogService eventLogService, JobManager jobManager) {
        this.view = view;
        this.userDAO = userDAO;
        this.userService = userService;
        this.jobManager = jobManager;
        this.eventLogService = eventLogService;
    }

    @Override
    public Single<User> requestUserInfo() {
        return userDAO.getUserInfo();
    }

    @Override
    public Completable requestVerifyAccessToken() {
        return userService.verifyToken();
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
                .subscribe(promotions -> view.setPromotionViews(promotions),
                        e -> Log.e(TAG, String.valueOf(e)))
        );
    }

    @Override
    public void unsubscribe() {
        if (compositeSubscription != null) {
            compositeSubscription.clear();
        }
    }
}
