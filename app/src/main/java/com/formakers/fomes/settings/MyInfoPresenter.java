package com.formakers.fomes.settings;

import com.formakers.fomes.common.model.User;
import com.formakers.fomes.common.network.UserService;
import com.formakers.fomes.common.repository.dao.UserDAO;
import com.formakers.fomes.common.util.Log;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import javax.inject.Inject;

import rx.android.schedulers.AndroidSchedulers;

@MyInfoDagger.Scope
public class MyInfoPresenter implements MyInfoContract.Presenter {

    private static final String TAG = "MyInfoPresenter";

    private MyInfoContract.View view;
    private UserDAO userDAO;
    private UserService userService;
    private User originalUserUnfo;

    @Inject
    public MyInfoPresenter(MyInfoContract.View view, UserDAO userDAO, UserService userService) {
        this.view = view;
        this.userDAO = userDAO;
        this.userService = userService;
    }

    @Override
    public void loadUserInfo() {
        userDAO.getUserInfo()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(userInfo -> {
                    this.originalUserUnfo = userInfo;
                    this.view.bind(this.originalUserUnfo);
                });
    }

    @Override
    public void updateUserInfo(Integer birthday, Integer job, String gender, String lifeApp) {
        User filledUserInfo = new User().setBirthday(birthday).setJob(job).setGender(gender)
                .setLifeApps(Collections.singletonList(lifeApp));

        User updatedUserInfo = getDiffWithUpdatableFields(filledUserInfo);

        Log.i(TAG, "user informations will be updated = " + updatedUserInfo);

        userService.updateUserInfo(updatedUserInfo)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> {
                    userDAO.updateUserInfo(updatedUserInfo);
                    this.view.showToast("유저정보가 업데이트 되었습니다!");
                    }, e -> Log.e(TAG, String.valueOf(e)));
    }

    @Override
    public boolean isUpdated(Integer birthday, Integer job, String gender, String lifeApp) {
        Log.v(TAG, "isUpdated) " + birthday + " " + job + " " + gender + " " + lifeApp);
        Log.v(TAG, "isUpdated) origianl=" + originalUserUnfo);

        boolean isUpdated =  !Objects.equals(originalUserUnfo.getBirthday(), birthday)
                || !Objects.equals(originalUserUnfo.getJob(), job)
                || !Objects.equals(originalUserUnfo.getGender(), gender)
                || !Objects.equals(originalUserUnfo.getLifeApps(), Collections.singletonList(lifeApp));

        Log.i(TAG, "isUpdated = " + isUpdated);
        return isUpdated;
    }

    private User getDiffWithUpdatableFields(User userInfo) {
        Log.v(TAG, "original = " + originalUserUnfo );
        Log.v(TAG, "update = " + userInfo );

        Integer birthday = null;
        Integer job = null ;
        String gender = null;
        List<String> lifeApps = null;

        if (!Objects.equals(originalUserUnfo.getBirthday(), userInfo.getBirthday())) {
            birthday = userInfo.getBirthday();
        }

        if (!Objects.equals(originalUserUnfo.getJob(), userInfo.getJob())) {
            job = userInfo.getJob();
        }

        if (!Objects.equals(originalUserUnfo.getGender(), userInfo.getGender())) {
            gender = userInfo.getGender();
        }

        if (!Objects.equals(originalUserUnfo.getLifeApps(), userInfo.getLifeApps())) {
            lifeApps = userInfo.getLifeApps();
        }

        if (birthday == null && job == null && gender == null && lifeApps == null) {
            return null;
        }

        return new User().setBirthday(birthday)
                .setJob(job)
                .setGender(gender)
                .setLifeApps(lifeApps)
                .setDevice(null);
    }
}
