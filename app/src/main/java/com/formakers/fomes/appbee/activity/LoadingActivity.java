package com.formakers.fomes.appbee.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.ImageView;
import android.widget.Toast;

import com.formakers.fomes.AppBeeApplication;
import com.formakers.fomes.R;
import com.formakers.fomes.common.view.BaseActivity;
import com.formakers.fomes.helper.AppUsageDataHelper;
import com.formakers.fomes.helper.ImageLoader;
import com.formakers.fomes.helper.SharedPreferencesHelper;
import com.formakers.fomes.common.network.UserService;

import javax.inject.Inject;

import butterknife.BindView;
import rx.android.schedulers.AndroidSchedulers;

@Deprecated
public class LoadingActivity extends BaseActivity {

    @Inject
    AppUsageDataHelper appUsageDataHelper;

    @Inject
    SharedPreferencesHelper SharedPreferencesHelper;

    @Inject
    UserService userService;

    @Inject
    ImageLoader imageLoader;

    @BindView(R.id.loading_imageview)
    ImageView loadingImageView;

    private static final String TAG = "LoadingActivity";

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

        appUsageDataHelper.sendAppUsages()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::moveToAnalysisResultActivity,
                        e -> Toast.makeText(LoadingActivity.this, R.string.app_usage_data_http_fail_message, Toast.LENGTH_SHORT).show());
    }

    private void moveToAnalysisResultActivity() {
        startActivity(new Intent(LoadingActivity.this, OnboardingAnalysisActivity.class));
        finish();
    }
}