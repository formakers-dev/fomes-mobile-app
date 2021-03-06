package com.formakers.fomes.settings;

import com.formakers.fomes.common.constant.FomesConstants;
import com.formakers.fomes.common.model.User;
import com.formakers.fomes.common.network.UserService;
import com.formakers.fomes.common.network.api.UserAPI;
import com.formakers.fomes.common.repository.dao.UserDAO;
import com.formakers.fomes.common.util.Log;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;

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
    private FirebaseRemoteConfig remoteConfig;

    private Long remoteConfigUserInfoUpdateVersion;

    @Inject
    public MyInfoPresenter(MyInfoContract.View view, UserDAO userDAO, UserService userService, FirebaseRemoteConfig remoteConfig) {
        this.view = view;
        this.userDAO = userDAO;
        this.userService = userService;
        this.remoteConfig = remoteConfig;
    }

    @Override
    public void loadUserInfo() {
        this.view.showLoading();

        if (remoteConfigUserInfoUpdateVersion == null) {
            remoteConfigUserInfoUpdateVersion = this.remoteConfig.getLong(FomesConstants.RemoteConfig.USER_INFO_UPDATE_VERSION);
        }

        userService.getUser()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(user -> {
                    this.originalUserInfo = user;
                    this.view.hideLoading();
                    this.view.bind(this.originalUserInfo);

                    long remoteConfigVersion = (remoteConfigUserInfoUpdateVersion != null)? remoteConfigUserInfoUpdateVersion : 0L;
                    long originalUserInfoUpdateVersion = (originalUserInfo.getUserInfoUpdateVersion() != null)? originalUserInfo.getUserInfoUpdateVersion() : 0L;

                    if (remoteConfigVersion > originalUserInfoUpdateVersion) {
                        this.view.showPointRewardEventDialog();
                    }
                }, e -> Log.e(TAG, String.valueOf(e)));
    }

    @Override
    public void updateUserInfo(User filledUserInfo) {
        User updatedUserInfo = getDiffWithUpdatableFields(filledUserInfo);

        Log.i(TAG, "user information will be updated = " + updatedUserInfo);

        updatedUserInfo.setUserInfoUpdateVersion(remoteConfigUserInfoUpdateVersion);

        userService.updateUserInfo(updatedUserInfo)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> {
                    userDAO.updateUserInfo(updatedUserInfo);
                    loadUserInfo();
                    this.view.showToast("????????? ????????? ?????????????????????");
                }, e -> {
                    Log.e(TAG, String.valueOf(e));
                    if (((HttpException) e).code() == UserAPI.StatusCode.DUPLICATED_NICK_NAME) {
                        this.view.showDuplicatedNickNameWarning();
                    } else {
                        this.view.showToast("????????? ?????????????????????. ????????? ??????????????????.");
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
        List<String> favoritePlatforms = null;
        List<String> favoriteGenres = null;
        List<String> leastFavoriteGenres = null;
        List<String> feedbackStyles = null;

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

        if (!Objects.equals(originalUserInfo.getFavoritePlatforms(), userInfo.getFavoritePlatforms())) {
            favoritePlatforms = userInfo.getFavoritePlatforms();
        }

        if (!Objects.equals(originalUserInfo.getFavoriteGenres(), userInfo.getFavoriteGenres())) {
            favoriteGenres = userInfo.getFavoriteGenres();
        }

        if (!Objects.equals(originalUserInfo.getLeastFavoriteGenres(), userInfo.getLeastFavoriteGenres())) {
            leastFavoriteGenres = userInfo.getLeastFavoriteGenres();
        }

        if (!Objects.equals(originalUserInfo.getFeedbackStyles(), userInfo.getFeedbackStyles())) {
            feedbackStyles = userInfo.getFeedbackStyles();
        }

        if (nickName == null && birthday == null && job == null && gender == null && lifeApps == null && monthlyPayment == null && favoritePlatforms == null && favoriteGenres == null && leastFavoriteGenres == null && feedbackStyles == null) {
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
                .setFavoritePlatforms(favoritePlatforms)
                .setFavoriteGenres(favoriteGenres)
                .setLeastFavoriteGenres(leastFavoriteGenres)
                .setFeedbackStyles(feedbackStyles);
    }
}
