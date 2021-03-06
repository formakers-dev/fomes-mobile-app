package com.formakers.fomes.provisioning;

import com.formakers.fomes.BuildConfig;
import com.formakers.fomes.R;
import com.formakers.fomes.analysis.RecentAnalysisReportActivity;
import com.formakers.fomes.common.constant.FomesConstants;
import com.formakers.fomes.common.dagger.AnalyticsModule;
import com.formakers.fomes.common.helper.AndroidNativeHelper;
import com.formakers.fomes.common.helper.SharedPreferencesHelper;
import com.formakers.fomes.common.model.User;
import com.formakers.fomes.common.network.UserService;
import com.formakers.fomes.common.repository.dao.UserDAO;
import com.formakers.fomes.common.util.Log;
import com.formakers.fomes.common.view.BaseFragment;
import com.formakers.fomes.main.MainActivity;
import com.google.common.collect.Lists;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;

import javax.inject.Inject;

import rx.Completable;

@ProvisioningDagger.Scope
public class ProvisioningPresenter implements ProvisioningContract.Presenter {
    public static final String TAG = ProvisioningPresenter.class.getSimpleName();

    private UserService userService;
    private AndroidNativeHelper androidNativeHelper;
    private SharedPreferencesHelper sharedPreferencesHelper;
    private UserDAO userDAO;
    private AnalyticsModule.Analytics analytics;
    private FirebaseRemoteConfig remoteConfig;

    private ProvisioningContract.View view;
    User user = new User();

    @Inject
    ProvisioningPresenter(ProvisioningContract.View view, UserService userService, AndroidNativeHelper androidNativeHelper, SharedPreferencesHelper sharedPreferencesHelper, UserDAO userDAO, AnalyticsModule.Analytics analytics, FirebaseRemoteConfig remoteConfig) {
        this.view = view;
        this.userService = userService;
        this.androidNativeHelper = androidNativeHelper;
        this.sharedPreferencesHelper = sharedPreferencesHelper;
        this.userDAO = userDAO;
        this.analytics = analytics;
        this.remoteConfig = remoteConfig;
    }

    @Override
    public AnalyticsModule.Analytics getAnalytics() {
        return this.analytics;
    }

    @Override
    public void setUserInfo(String game, Integer birth, Integer job, String gender) {
        this.user.setLifeApps(Lists.newArrayList(game));
        this.user.setBirthday(birth);
        this.user.setJob(job);
        this.user.setGender(gender);
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
    public String getUserNickName() {
        return user.getNickName();
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
    public Completable requestVerifyUserNickName(String nickName) {
        return this.userService.verifyNickName(nickName);
    }

    @Override
    public Completable requestToUpdateUserInfo() {
        return this.userService.updateUser(this.user, BuildConfig.VERSION_NAME)
                .doOnCompleted(() -> userDAO.updateUserInfo(this.user));
    }

    @Override
    public boolean hasUsageStatsPermission() {
        return this.androidNativeHelper.hasUsageStatsPermission();
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

    @Override
    public void checkGrantedOnPermissionFragment() {
        if (this.hasUsageStatsPermission()) {
            this.finishFlow();
        } else {
            this.emitNeedToGrantEvent();
        }
    }

    private void finishFlow() {
        this.emitStartActivityAndFinishEvent(this.isMoveToAnalysisReport() ? RecentAnalysisReportActivity.class : MainActivity.class);

        if (isProvisiongProgress()) {
            this.setProvisioningProgressStatus(FomesConstants.PROVISIONING.PROGRESS_STATUS.COMPLETED);
        }
    }

    private boolean isMoveToAnalysisReport() {
        Log.d(TAG, "[RemoteConfig] SIGNUP_ALALYSIS_SCREEN_IS_VISIBLE=" + this.remoteConfig.getBoolean(FomesConstants.RemoteConfig.SIGNUP_ALALYSIS_SCREEN_IS_VISIBLE));
        return isProvisiongProgress()
                && this.remoteConfig.getBoolean(FomesConstants.RemoteConfig.SIGNUP_ALALYSIS_SCREEN_IS_VISIBLE);
    }
}
