package com.appbee.appbeemobile.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.appbee.appbeemobile.helper.TimeHelper;
import com.appbee.appbeemobile.model.User;
import com.appbee.appbeemobile.network.ServiceCallback;
import com.appbee.appbeemobile.network.UserService;

import java.util.UUID;

import javax.inject.Inject;

public class PowerConnectedReceiver extends BroadcastReceiver {

    private UserService userService;
    private TimeHelper timeHelper;

    @Inject
    public PowerConnectedReceiver(UserService userService, TimeHelper timeHelper) {
        this.userService = userService;
        this.timeHelper = timeHelper;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if(Intent.ACTION_POWER_CONNECTED.equals(intent.getAction())) {
            String uniqueID = UUID.randomUUID().toString();
            String date = timeHelper.getFormattedCurrentTime(TimeHelper.DATE_FORMAT);
            User user = new User(uniqueID, date);
            userService.sendUser(user, new ServiceCallback() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onFail(String resultCode) {

                }
            });
        }
    }
}