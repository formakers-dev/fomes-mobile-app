package com.formakers.fomes.provisioning.presenter;

import android.util.Log;

import com.formakers.fomes.R;
import com.formakers.fomes.helper.AppBeeAndroidNativeHelper;
import com.formakers.fomes.helper.SharedPreferencesHelper;
import com.formakers.fomes.model.User;
import com.formakers.fomes.common.network.UserService;
import com.formakers.fomes.provisioning.contract.ProvisioningContract;
import com.formakers.fomes.analysis.view.RecentAnalysisReportActivity;
import com.formakers.fomes.provisioning.view.LoginActivity;
import com.formakers.fomes.util.FomesConstants;

import java.util.ArrayList;

import javax.inject.Inject;

import retrofit2.adapter.rxjava.HttpException;
import rx.Completable;
import rx.android.schedulers.AndroidSchedulers;

public class ProvisioningPresenter implements ProvisioningContract.Presenter {
    public static final String TAG = ProvisioningPresenter.class.getSimpleName();

    private ProvisioningContract.View view;
    private User user = new User();
    @Inject UserService userService;
    @Inject AppBeeAndroidNativeHelper appBeeAndroidNativeHelper;
    @Inject SharedPreferencesHelper sharedPreferencesHelper;

    public ProvisioningPresenter(ProvisioningContract.View view) {
        this.view = view;
        this.view.getApplicationComponent().inject(this);
    }

    // temporary code for test
    ProvisioningPresenter(ProvisioningContract.View view, User user, UserService userService, AppBeeAndroidNativeHelper appBeeAndroidNativeHelper, SharedPreferencesHelper sharedPreferencesHelper) {
        this.view = view;
        this.user = user;
        this.userService = userService;
        this.appBeeAndroidNativeHelper = appBeeAndroidNativeHelper;
        this.sharedPreferencesHelper = sharedPreferencesHelper;
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
    public void emitFilledUpEvent(boolean isFilledUp) {
        this.view.setNextButtonVisibility(isFilledUp);
    }

    @Override
    public void emitGrantedEvent(boolean isGranted) {
        if (isGranted) {
            this.userService.verifyToken()
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(() -> {
                        this.setProvisioningProgressStatus(FomesConstants.PROVISIONING.PROGRESS_STATUS.COMPLETED);
                        this.view.startActivityAndFinish(RecentAnalysisReportActivity.class);
                    }, e -> {
                        if (e instanceof HttpException) {
                            int code = ((HttpException) e).code();
                            if (code == 401 || code == 403) {
                                this.view.showToast("인증 오류가 발생하였습니다. 재로그인이 필요합니다.");
                                this.view.startActivityAndFinish(LoginActivity.class);
                                return;
                            }
                        }

                        this.view.showToast("예상치 못한 에러가 발생하였습니다.");
                    });
        } else {
            this.view.setNextButtonVisibility(true);
        }
    }

    @Override
    public void emitAlmostCompletedEvent(boolean isAlmostCompleted) {
        if (isAlmostCompleted) {
            this.view.setIconImage(R.drawable.fomes_face_smile);
        } else {
            this.view.setIconImage(R.drawable.fomes_face);
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
