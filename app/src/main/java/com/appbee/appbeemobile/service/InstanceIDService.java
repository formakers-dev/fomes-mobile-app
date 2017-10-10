package com.appbee.appbeemobile.service;

import android.util.Log;

import com.appbee.appbeemobile.AppBeeApplication;
import com.appbee.appbeemobile.helper.LocalStorageHelper;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import javax.inject.Inject;

public class InstanceIDService extends FirebaseInstanceIdService {
    private static final String TAG = InstanceIDService.class.getSimpleName();

    @Inject
    LocalStorageHelper localStorageHelper;

    @Override
    public void onCreate() {
        super.onCreate();
        ((AppBeeApplication) getApplication()).getComponent().inject(this);
    }

    @Override
    public void onTokenRefresh() {
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + refreshedToken);
        localStorageHelper.setRegistrationToken(refreshedToken);
    }
}
