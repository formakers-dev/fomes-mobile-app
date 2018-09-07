package com.formakers.fomes.provisioning.presenter;

import android.util.Log;

import com.formakers.fomes.model.User;
import com.formakers.fomes.provisioning.contract.ProvisioningContract;

import java.util.ArrayList;

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
}
