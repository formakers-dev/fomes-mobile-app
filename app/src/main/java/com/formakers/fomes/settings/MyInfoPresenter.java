package com.formakers.fomes.settings;

import com.formakers.fomes.common.model.User;
import com.formakers.fomes.common.network.UserService;
import com.formakers.fomes.common.network.api.UserAPI;
import com.formakers.fomes.common.repository.dao.UserDAO;
import com.formakers.fomes.common.util.Log;

import java.util.List;
import java.util.Objects;

import javax.inject.Inject;

import retrofit2.adapter.rxjava.HttpException;
import rx.android.schedulers.AndroidSchedulers;

@MyInfoDagger.Scope
public class MyInfoPresenter implements MyInfoContract.Presenter {

    private static final String TAG = "MyInfoPresenter";

    private MyInfoContract.View view;
    private UserDAO userDAO;
    private UserService userService;
    private User originalUserInfo;

    @Inject
    public MyInfoPresenter(MyInfoContract.View view, UserDAO userDAO, UserService userService) {
        this.view = view;
        this.userDAO = userDAO;
        this.userService = userService;
    }

    @Override
    public void loadUserInfo() {
        userService.getUser()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(user -> {
                    this.originalUserInfo = user;
                    this.view.bind(this.originalUserInfo);
                }, e -> Log.e(TAG, String.valueOf(e)));
    }

    @Override
    public void updateUserInfo(User filledUserInfo) {
        User updatedUserInfo = getDiffWithUpdatableFields(filledUserInfo);

        Log.i(TAG, "user information will be updated = " + updatedUserInfo);

        userService.updateUserInfo(updatedUserInfo)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> {
                    userDAO.updateUserInfo(updatedUserInfo);
                    loadUserInfo();
                    this.view.showToast("프로필 수정이 완료되었습니다");
                }, e -> {
                    Log.e(TAG, String.valueOf(e));
                    if (((HttpException) e).code() == UserAPI.StatusCode.DUPLICATED_NICK_NAME) {
                        this.view.showDuplicatedNickNameWarning();
                    } else {
                        this.view.showToast("오류가 발생하였습니다. 재시도 부탁드립니다.");
                    }
                });
    }

    @Override
    public boolean isUpdated(User filledUserInfo) {
        Log.v(TAG, "isUpdated) filled=" + filledUserInfo);
        Log.v(TAG, "isUpdated) original=" + originalUserInfo);

        boolean isUpdated = getDiffWithUpdatableFields(filledUserInfo) != null;

        Log.i(TAG, "isUpdated = " + isUpdated);
        return isUpdated;
    }

    private User getDiffWithUpdatableFields(User userInfo) {
        Log.v(TAG, "original = " + originalUserInfo );
        Log.v(TAG, "update = " + userInfo );

        String nickName = null;
        Integer birthday = null;
        Integer job = null ;
        String gender = null;
        List<String> lifeApps = null;
        String monthlyPayment = null;
        List<String> favoriteGenres = null;
        List<String> leastFavoriteGenres = null;

        if (!Objects.equals(originalUserInfo.getNickName(), userInfo.getNickName())) {
            nickName = userInfo.getNickName();
        }

        if (!Objects.equals(originalUserInfo.getBirthday(), userInfo.getBirthday())) {
            birthday = userInfo.getBirthday();
        }

        if (!Objects.equals(originalUserInfo.getJob(), userInfo.getJob())) {
            job = userInfo.getJob();
        }

        if (!Objects.equals(originalUserInfo.getGender(), userInfo.getGender())) {
            gender = userInfo.getGender();
        }

        if (!Objects.equals(originalUserInfo.getLifeApps(), userInfo.getLifeApps())) {
            lifeApps = userInfo.getLifeApps();
        }

        if (!Objects.equals(originalUserInfo.getMonthlyPayment(), userInfo.getMonthlyPayment())) {
            monthlyPayment = userInfo.getMonthlyPayment();
        }

        if (!Objects.equals(originalUserInfo.getFavoriteGenres(), userInfo.getFavoriteGenres())) {
            favoriteGenres = userInfo.getFavoriteGenres();
        }

        if (!Objects.equals(originalUserInfo.getLeastFavoriteGenres(), userInfo.getLeastFavoriteGenres())) {
            leastFavoriteGenres = userInfo.getLeastFavoriteGenres();
        }

        if (nickName == null && birthday == null && job == null && gender == null && lifeApps == null && monthlyPayment == null && favoriteGenres == null && leastFavoriteGenres == null) {
            return null;
        }

        return new User()
                .setNickName(nickName)
                .setBirthday(birthday)
                .setJob(job)
                .setGender(gender)
                .setLifeApps(lifeApps)
                .setDevice(null)
                .setMonthlyPayment(monthlyPayment)
                .setFavoriteGenres(favoriteGenres)
                .setLeastFavoriteGenres(leastFavoriteGenres);
    }
}
