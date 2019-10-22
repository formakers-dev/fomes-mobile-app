package com.formakers.fomes.settings;

import com.formakers.fomes.common.repository.dao.UserDAO;

import javax.inject.Inject;

import rx.android.schedulers.AndroidSchedulers;

@MyInfoDagger.Scope
public class MyInfoPresenter implements MyInfoContract.Presenter {

    private MyInfoContract.View view;
    private UserDAO userDAO;

    @Inject
    public MyInfoPresenter(MyInfoContract.View view, UserDAO userDAO) {
        this.view = view;
        this.userDAO = userDAO;
    }

    @Override
    public void loadUserInfo() {
        userDAO.getUserInfo()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(userInfo -> this.view.bind(userInfo));
    }
}
