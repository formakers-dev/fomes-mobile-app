package com.appbee.appbeemobile.service;

import android.util.Log;

import com.appbee.appbeemobile.AppBeeApplication;
import com.appbee.appbeemobile.helper.LocalStorageHelper;
import com.appbee.appbeemobile.helper.MessagingHelper;
import com.appbee.appbeemobile.model.User;
import com.appbee.appbeemobile.network.UserService;
import com.google.firebase.iid.FirebaseInstanceIdService;

import javax.inject.Inject;

import rx.android.schedulers.AndroidSchedulers;

public class MessagingTokenService extends FirebaseInstanceIdService {
    private static final String TAG = MessagingTokenService.class.getSimpleName();

    @Inject
    LocalStorageHelper localStorageHelper;

    @Inject
    MessagingHelper messagingHelper;

    @Inject
    UserService userService;

    @Override
    public void onCreate() {
        super.onCreate();
        ((AppBeeApplication) getApplication()).getComponent().inject(this);
    }

    @Override
    public void onTokenRefresh() {
        Log.d(TAG, "onTokenRefresh");
        String refreshedToken = messagingHelper.getMessagingToken();

        if (!localStorageHelper.getRegistrationToken().equals(refreshedToken)) {
            User user = new User(refreshedToken);
            userService.sendUser(user)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(() -> {
                        Log.d(TAG, "Token Refresh is Completed!");
                        localStorageHelper.setRegistrationToken(refreshedToken);
                    }, (throwable) -> Log.e(TAG, throwable.toString()));
        }
    }
}
