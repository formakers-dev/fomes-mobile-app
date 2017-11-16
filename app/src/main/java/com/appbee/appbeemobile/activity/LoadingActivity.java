package com.appbee.appbeemobile.activity;

import android.content.Intent;
import android.hardware.ConsumerIrManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.appbee.appbeemobile.AppBeeApplication;
import com.appbee.appbeemobile.R;
import com.appbee.appbeemobile.helper.AppUsageDataHelper;
import com.appbee.appbeemobile.helper.LocalStorageHelper;
import com.appbee.appbeemobile.model.User;
import com.appbee.appbeemobile.network.UserService;

import java.util.function.Consumer;

import javax.inject.Inject;

import rx.schedulers.Schedulers;

public class LoadingActivity extends BaseActivity {

    @Inject
    AppUsageDataHelper appUsageDataHelper;

    @Inject
    LocalStorageHelper localStorageHelper;

    @Inject
    UserService userService;

    private static final String TAG = LoadingActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);

        ((AppBeeApplication) getApplication()).getComponent().inject(this);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        userService.sendUser(new User(localStorageHelper.getUserId(), localStorageHelper.getEmail(), localStorageHelper.getBirthday(), localStorageHelper.getGender(), localStorageHelper.getRegistrationToken()))
                .observeOn(Schedulers.io())
                .subscribe(result -> {
                            if (result) {
                                sendStatData();
                            } else {
                                toastSendUserErrorMessage();
                            }
                        },
                        error -> toastSendUserErrorMessage());
    }

    private void toastSendUserErrorMessage() {
        runOnUiThread(() -> Toast.makeText(LoadingActivity.this, "사용자 정보 저장에 실패하였습니다.", Toast.LENGTH_SHORT).show());
    }

    private void sendStatData() {
        appUsageDataHelper.sendShortTermStatAndAppUsages(appUsageDataHelperSendDataCallback);
    }

    AppUsageDataHelper.SendDataCallback appUsageDataHelperSendDataCallback = new AppUsageDataHelper.SendDataCallback() {
        @Override
        public void onSuccess() {
            moveToAnalysisResultActivity();
        }

        @Override
        public void onFail() {
            Toast.makeText(LoadingActivity.this, "데이터 전송에 실패하였습니다.", Toast.LENGTH_SHORT).show();
        }
    };

    private void moveToAnalysisResultActivity() {
        startActivity(new Intent(LoadingActivity.this, OnboardingAnalysisActivity.class));
        finish();
    }
}
