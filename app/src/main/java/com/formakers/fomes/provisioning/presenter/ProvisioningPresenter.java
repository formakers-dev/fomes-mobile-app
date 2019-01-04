package com.formakers.fomes.provisioning.presenter;

import com.formakers.fomes.R;
import com.formakers.fomes.common.FomesConstants;
import com.formakers.fomes.common.job.JobManager;
import com.formakers.fomes.common.network.UserService;
import com.formakers.fomes.common.noti.ChannelManager;
import com.formakers.fomes.common.repository.dao.UserDAO;
import com.formakers.fomes.common.util.Log;
import com.formakers.fomes.common.view.BaseFragment;
import com.formakers.fomes.helper.AndroidNativeHelper;
import com.formakers.fomes.helper.SharedPreferencesHelper;
import com.formakers.fomes.model.User;
import com.formakers.fomes.provisioning.contract.ProvisioningContract;
import com.google.common.collect.Lists;
import com.google.firebase.messaging.FirebaseMessaging;

import javax.inject.Inject;

import rx.Completable;

import static com.formakers.fomes.common.FomesConstants.Notification.TOPIC_NOTICE_ALL;

public class ProvisioningPresenter implements ProvisioningContract.Presenter {
    public static final String TAG = ProvisioningPresenter.class.getSimpleName();

    @Inject UserService userService;
    @Inject AndroidNativeHelper androidNativeHelper;
    @Inject SharedPreferencesHelper sharedPreferencesHelper;
    @Inject UserDAO userDAO;
    @Inject JobManager jobManager;
    @Inject ChannelManager channelManager;

    private ProvisioningContract.View view;
    private User user = new User();

    public ProvisioningPresenter(ProvisioningContract.View view) {
        this.view = view;
        this.view.getApplicationComponent().inject(this);
    }

    // temporary code for test
    ProvisioningPresenter(ProvisioningContract.View view, User user, UserService userService, AndroidNativeHelper androidNativeHelper, SharedPreferencesHelper sharedPreferencesHelper, UserDAO userDAO) {
        this.view = view;
        this.user = user;
        this.userService = userService;
        this.androidNativeHelper = androidNativeHelper;
        this.sharedPreferencesHelper = sharedPreferencesHelper;
        this.userDAO = userDAO;
    }

    @Override
    public void updateUserInfo(String game, Integer birth, Integer job, String gender) {
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
    public Completable requestUpdateUser() {
        userDAO.updateUserInfo(this.user);
        return this.userService.updateUser(this.user);
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
    public int registerSendDataJob() {
        return this.jobManager.registerSendDataJob(JobManager.JOB_ID_SEND_DATA);
    }

    @Override
    public void registerPublicNotificationTopic() {
        channelManager.subscribePublicTopic();
    }

}
