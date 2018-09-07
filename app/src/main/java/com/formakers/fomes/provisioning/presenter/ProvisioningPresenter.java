package com.formakers.fomes.provisioning.presenter;

import android.util.Log;

import com.formakers.fomes.model.User;
import com.formakers.fomes.provisioning.contract.ProvisioningContract;

public class ProvisioningPresenter implements ProvisioningContract.Presenter {
    public static final String TAG = ProvisioningPresenter.class.getSimpleName();

    private ProvisioningContract.View view;
    private User user = new User();

    public ProvisioningPresenter(ProvisioningContract.View view) {
        this.view = view;
    }

    // temporary code for test
    ProvisioningPresenter(ProvisioningContract.View view, User user) {
        this.view = view;
        this.user = user;
    }

    @Override
    public void onNextPageEvent() {
        this.view.nextPage();
    }

    @Override
    public void setUserInfo(Integer birth, String job, String gender) {
        this.user.setBirthday(birth);
        this.user.setJob(job);
        this.user.setGender(gender);
        Log.d(TAG, user.toString());
    }
}
