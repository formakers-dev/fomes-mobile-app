package com.formakers.fomes.service;

import android.util.Log;

import com.formakers.fomes.AppBeeApplication;
import com.formakers.fomes.helper.LocalStorageHelper;
import com.formakers.fomes.helper.MessagingHelper;
import com.formakers.fomes.network.UserService;
import com.google.firebase.iid.FirebaseInstanceIdService;

import javax.inject.Inject;

import rx.android.schedulers.AndroidSchedulers;

public class MessagingTokenService extends FirebaseInstanceIdService {
    private static final String TAG = "MessagingTokenService";

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
        final String refreshedToken = messagingHelper.getMessagingToken();
        final String oldToken = localStorageHelper.getRegistrationToken();

        if (!localStorageHelper.isLoggedIn()) {
            Log.e(TAG, "Not Signed User");
            localStorageHelper.setRegistrationToken(refreshedToken);

        } else if (!oldToken.equals(refreshedToken)) {
            userService.updateRegistrationToken(refreshedToken)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(() -> {
                        Log.d(TAG, "Token Refresh is Completed!");
                        localStorageHelper.setRegistrationToken(refreshedToken);
                    }, (throwable) -> Log.e(TAG, throwable.toString()));
        }
    }
}
