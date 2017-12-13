package com.appbee.appbeemobile.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.ImageView;
import android.widget.Toast;

import com.appbee.appbeemobile.AppBeeApplication;
import com.appbee.appbeemobile.R;
import com.appbee.appbeemobile.helper.AppUsageDataHelper;
import com.appbee.appbeemobile.helper.ImageLoader;
import com.appbee.appbeemobile.helper.LocalStorageHelper;
import com.appbee.appbeemobile.model.User;
import com.appbee.appbeemobile.network.UserService;

import javax.inject.Inject;

import butterknife.BindView;
import rx.schedulers.Schedulers;

public class LoadingActivity extends BaseActivity {

    @Inject
    AppUsageDataHelper appUsageDataHelper;

    @Inject
    LocalStorageHelper localStorageHelper;

    @Inject
    UserService userService;

    @Inject
    ImageLoader imageLoader;

    @BindView(R.id.loading_imageview)
    ImageView loadingImageView;

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

        imageLoader.loadGifImage(loadingImageView, R.drawable.loading_bowl);

        addToCompositeSubscription(
                userService.sendUser(new User(localStorageHelper.getUserId(), localStorageHelper.getEmail(), localStorageHelper.getBirthday(), localStorageHelper.getGender(), localStorageHelper.getRegistrationToken()))
                        .observeOn(Schedulers.io())
                        .subscribe(this::sendStatData, error -> toastSendUserErrorMessage())
        );
    }

    private void toastSendUserErrorMessage() {
        runOnUiThread(() -> Toast.makeText(LoadingActivity.this, R.string.user_http_fail_message, Toast.LENGTH_SHORT).show());
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
            Toast.makeText(LoadingActivity.this, R.string.app_usage_data_http_fail_message, Toast.LENGTH_SHORT).show();
        }
    };

    private void moveToAnalysisResultActivity() {
        startActivity(new Intent(LoadingActivity.this, OnboardingAnalysisActivity.class));
        finish();
    }
}
