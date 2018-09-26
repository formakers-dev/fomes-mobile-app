package com.formakers.fomes.main.presenter;

import com.formakers.fomes.main.contract.MainContract;
import com.formakers.fomes.model.User;
import com.formakers.fomes.repository.dao.UserDAO;

import javax.inject.Inject;

import rx.Single;

public class MainPresenter implements MainContract.Presenter {

    @Inject UserDAO userDAO;

    MainContract.View view;

    public MainPresenter(MainContract.View view) {
        this.view = view;
        this.view.getApplicationComponent().inject(this);
    }

    // temporary code for test
    MainPresenter(MainContract.View view, UserDAO userDAO) {
        this.view = view;
        this.userDAO = userDAO;
    }

    @Override
    public Single<User> requestUserInfo() {
        return userDAO.getUserInfo();
    }
}
