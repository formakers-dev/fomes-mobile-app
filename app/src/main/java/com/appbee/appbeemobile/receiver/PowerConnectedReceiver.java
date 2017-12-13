package com.appbee.appbeemobile.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.appbee.appbeemobile.AppBeeApplication;
import com.appbee.appbeemobile.helper.AppBeeAndroidNativeHelper;
import com.appbee.appbeemobile.helper.AppUsageDataHelper;
import com.appbee.appbeemobile.helper.LocalStorageHelper;
import com.appbee.appbeemobile.helper.MessagingHelper;
import com.appbee.appbeemobile.model.User;
import com.appbee.appbeemobile.network.UserService;

import javax.inject.Inject;

import rx.android.schedulers.AndroidSchedulers;

public class PowerConnectedReceiver extends BroadcastReceiver {
    private static final String TAG = PowerConnectedReceiver.class.getSimpleName();

    @Inject
    AppUsageDataHelper appUsageDataHelper;

    @Inject
    AppBeeAndroidNativeHelper appBeeAndroidNativeHelper;

    @Inject
    LocalStorageHelper localStorageHelper;

    @Inject
    MessagingHelper messagingHelper;

    @Inject
    UserService userService;

    public PowerConnectedReceiver() {}

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "PowerConnectedReceiver OnReceive");
        ((AppBeeApplication) context.getApplicationContext()).getComponent().inject(this);

        if (Intent.ACTION_POWER_CONNECTED.equals(intent.getAction())) {
            Log.d(TAG, "PowerConnectedReceiver Action equals");

            if (!TextUtils.isEmpty(localStorageHelper.getAccessToken())
                    && appBeeAndroidNativeHelper.hasUsageStatsPermission()) {
                Log.d(TAG, "PowerConnectedReceiver has permission");

                // 노티 토큰 업데이트 로직 추가 - onRefreshToken 에서 에러난 경우에 대한 대비책
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

                appUsageDataHelper.sendShortTermStatAndAppUsages(new AppUsageDataHelper.SendDataCallback() {
                    @Override
                    public void onSuccess() {
                        Log.d(TAG, "PowerConnectedReceiver send ShortTermStats successfully");
                    }

                    @Override
                    public void onFail() {
                        Log.e(TAG, "PowerConnectedReceiver fail to send ShortTermStats");
                    }
                });
            }
        }
    }
}
