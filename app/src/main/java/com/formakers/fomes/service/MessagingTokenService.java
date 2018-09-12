package com.formakers.fomes.service;

import android.util.Log;

import com.formakers.fomes.AppBeeApplication;
import com.formakers.fomes.helper.SharedPreferencesHelper;
import com.formakers.fomes.helper.MessagingHelper;
import com.formakers.fomes.network.UserService;
import com.google.firebase.iid.FirebaseInstanceIdService;

import javax.inject.Inject;

import rx.android.schedulers.AndroidSchedulers;

public class MessagingTokenService extends FirebaseInstanceIdService {
    private static final String TAG = "MessagingTokenService";

    @Inject
    SharedPreferencesHelper SharedPreferencesHelper;

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
        final String oldToken = SharedPreferencesHelper.getRegistrationToken();

        if (!SharedPreferencesHelper.isLoggedIn()) {
            Log.e(TAG, "Not Signed User");
            SharedPreferencesHelper.setRegistrationToken(refreshedToken);

        } else if (!oldToken.equals(refreshedToken)) {
            userService.updateRegistrationToken(refreshedToken)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(() -> {
                        Log.d(TAG, "Token Refresh is Completed!");
                        SharedPreferencesHelper.setRegistrationToken(refreshedToken);
                    }, (throwable) -> Log.e(TAG, throwable.toString()));
        }
    }
}
