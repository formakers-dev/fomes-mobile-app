package com.formakers.fomes.main.presenter;

import com.formakers.fomes.common.job.JobManager;
import com.formakers.fomes.common.network.UserService;
import com.formakers.fomes.main.contract.MainContract;
import com.formakers.fomes.model.User;
import com.formakers.fomes.common.repository.dao.UserDAO;

import javax.inject.Inject;

import rx.Completable;
import rx.Single;

public class MainPresenter implements MainContract.Presenter {

    @Inject UserDAO userDAO;
    @Inject UserService userService;
    @Inject JobManager jobManager;

    MainContract.View view;

    public MainPresenter(MainContract.View view) {
        this.view = view;
        this.view.getApplicationComponent().inject(this);
    }

    // temporary code for test
    MainPresenter(MainContract.View view, UserDAO userDAO, UserService userService, JobManager jobManager) {
        this.view = view;
        this.userDAO = userDAO;
        this.userService = userService;
        this.jobManager = jobManager;
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
}
