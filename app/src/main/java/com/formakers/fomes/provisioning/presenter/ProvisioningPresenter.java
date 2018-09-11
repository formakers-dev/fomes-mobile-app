package com.formakers.fomes.provisioning.presenter;

import android.util.Log;

import com.formakers.fomes.helper.AppBeeAndroidNativeHelper;
import com.formakers.fomes.model.User;
import com.formakers.fomes.network.UserService;
import com.formakers.fomes.provisioning.contract.ProvisioningContract;
import com.formakers.fomes.provisioning.view.CurrentAnalysisReportActivity;

import java.util.ArrayList;

import javax.inject.Inject;

import rx.Completable;

public class ProvisioningPresenter implements ProvisioningContract.Presenter {
    public static final String TAG = ProvisioningPresenter.class.getSimpleName();

    private ProvisioningContract.View view;
    private User user = new User();
    @Inject UserService userService;
    @Inject AppBeeAndroidNativeHelper appBeeAndroidNativeHelper;

    public ProvisioningPresenter(ProvisioningContract.View view) {
        this.view = view;
        this.view.getApplicationComponent().inject(this);
    }

    // temporary code for test
    ProvisioningPresenter(ProvisioningContract.View view, User user, UserService userService, AppBeeAndroidNativeHelper appBeeAndroidNativeHelper) {
        this.view = view;
        this.user = user;
        this.userService = userService;
        this.appBeeAndroidNativeHelper = appBeeAndroidNativeHelper;
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
    public void emitNextPageEvent() {
        this.view.nextPage();
    }

    @Override
    public void emitFilledUpEvent(boolean isFilledUp) {
        this.view.setNextButtonVisibility(isFilledUp);
    }

    @Override
    public void emitGrantedEvent(boolean isGranted) {
        if (isGranted) {
            this.view.startActivityAndFinish(CurrentAnalysisReportActivity.class);
        } else {
            this.view.setNextButtonVisibility(true);
        }
    }

    @Override
    public Completable requestUpdateUser() {
        return this.userService.updateUser(this.user);
    }

    @Override
    public boolean hasUsageStatsPermission() {
        return this.appBeeAndroidNativeHelper.hasUsageStatsPermission();
    }

}
