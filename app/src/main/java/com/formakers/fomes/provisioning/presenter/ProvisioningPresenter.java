package com.formakers.fomes.provisioning.presenter;

import android.util.Log;

import com.formakers.fomes.R;
import com.formakers.fomes.common.network.UserService;
import com.formakers.fomes.common.view.BaseFragment;
import com.formakers.fomes.helper.AppBeeAndroidNativeHelper;
import com.formakers.fomes.helper.SharedPreferencesHelper;
import com.formakers.fomes.model.User;
import com.formakers.fomes.provisioning.contract.ProvisioningContract;
import com.formakers.fomes.common.repository.dao.UserDAO;
import com.formakers.fomes.util.FomesConstants;

import java.util.ArrayList;

import javax.inject.Inject;

import rx.Completable;

public class ProvisioningPresenter implements ProvisioningContract.Presenter {
    public static final String TAG = ProvisioningPresenter.class.getSimpleName();

    @Inject UserService userService;
    @Inject AppBeeAndroidNativeHelper appBeeAndroidNativeHelper;
    @Inject SharedPreferencesHelper sharedPreferencesHelper;
    @Inject UserDAO userDAO;

    private ProvisioningContract.View view;
    private User user = new User();

    public ProvisioningPresenter(ProvisioningContract.View view) {
        this.view = view;
        this.view.getApplicationComponent().inject(this);
    }

    // temporary code for test
    ProvisioningPresenter(ProvisioningContract.View view, User user, UserService userService, AppBeeAndroidNativeHelper appBeeAndroidNativeHelper, SharedPreferencesHelper sharedPreferencesHelper, UserDAO userDAO) {
        this.view = view;
        this.user = user;
        this.userService = userService;
        this.appBeeAndroidNativeHelper = appBeeAndroidNativeHelper;
        this.sharedPreferencesHelper = sharedPreferencesHelper;
        this.userDAO = userDAO;
    }

    @Override
    public void updateDemographicsToUser(Integer birth, String job, String gender) {
        this.user.setBirthday(birth);
        this.user.setJob(job);
        this.user.setGender(gender);
        Log.d(TAG, user.toString());
    }

    @Override
    public void updateLifeGameToUser(String game) {
        ArrayList<String> lifeGames = new ArrayList<>();
        lifeGames.add(game);
        
        this.user.setLifeApps(lifeGames);
        Log.d(TAG, user.toString());
    }

    @Override
    public void updateNickNameToUser(String nickName) {
        this.user.setNickName(nickName);
        Log.d(TAG, user.toString());
    }

    @Override
    public void setProvisioningProgressStatus(int status) {
        this.sharedPreferencesHelper.setProvisioningProgressStatus(status);
    }

    @Override
    public void emitNextPageEvent() {
        this.view.nextPage();
    }

    @Override
    public void emitFilledUpEvent(BaseFragment fragment, boolean isFilledUp) {
        if (isSelected(fragment)) {
            this.view.setNextButtonVisibility(isFilledUp);
        }
    }

    @Override
    public void emitNeedToGrantEvent() {
        this.view.setNextButtonText(R.string.common_go_to_grant);
        this.view.setNextButtonVisibility(true);
    }

    @Override
    public void emitStartActivityAndFinishEvent(Class<?> destActivity) {
        this.view.startActivityAndFinish(destActivity);
    }

    @Override
    public Completable requestVerifyUserToken() {
        return this.userService.verifyToken();
    }

    @Override
    public Completable requestUpdateUser() {
        userDAO.updateUserInfo(this.user);
        return this.userService.updateUser(this.user);
    }

    @Override
    public Completable requestUpdateUserWithoutRefreshToken() {
        return this.userService.updateUserWithoutRefreshToken(this.user);
    }


    @Override
    public boolean hasUsageStatsPermission() {
        return this.appBeeAndroidNativeHelper.hasUsageStatsPermission();
    }

    @Override
    public boolean isSelected(BaseFragment fragment) {
        return this.view.isSelectedFragement(fragment);
    }

    @Override
    public boolean isProvisiongProgress() {
        return this.sharedPreferencesHelper.getProvisioningProgressStatus()
                != FomesConstants.PROVISIONING.PROGRESS_STATUS.COMPLETED;
    }

}
