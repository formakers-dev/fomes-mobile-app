package com.formakers.fomes.advertising;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;

import com.formakers.fomes.BuildConfig;
import com.formakers.fomes.R;
import com.formakers.fomes.common.util.Log;
import com.formakers.fomes.common.view.FomesBaseActivity;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;

import butterknife.BindView;

public class AdvertisingActivity extends FomesBaseActivity implements RewardedVideoAdListener {

    private static final String TAG = "AdvertisingActivity";

    private RewardedVideoAd mRewardedVideoAd;
    private boolean isRewardSuccess = false;

    @BindView(R.id.advertising_loading) View advertisingLoading;
    @BindView(R.id.advertising_thanks_layout) ViewGroup advertisingThanksLayout;
    @BindView(R.id.advertising_failure_layout) ViewGroup advertisingFailureLayout;
    @BindView(R.id.advertising_load_failure_layout) ViewGroup advertisingLoadFailureLayout;
    @BindView(R.id.load_new_advertising) View loadNewAdvertisingButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.setContentView(R.layout.activity_advertising);

        getSupportActionBar().setTitle(R.string.main_menu_advertising);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        MobileAds.initialize(this, BuildConfig.ADMOB_APP_ID);
        mRewardedVideoAd = MobileAds.getRewardedVideoAdInstance(this);
        mRewardedVideoAd.setRewardedVideoAdListener(this);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        loadRewardedVideoAd();

        loadNewAdvertisingButton.setOnClickListener((e) -> {
            loadRewardedVideoAd();
        });
    }

    private void showLoading() {
        advertisingLoading.setVisibility(View.VISIBLE);
        advertisingThanksLayout.setVisibility(View.GONE);
        advertisingFailureLayout.setVisibility(View.GONE);
        advertisingLoadFailureLayout.setVisibility(View.GONE);
        loadNewAdvertisingButton.setVisibility(View.GONE);
    }

    private void showThanksLayout() {
        advertisingLoading.setVisibility(View.GONE);
        advertisingThanksLayout.setVisibility(View.VISIBLE);
        advertisingFailureLayout.setVisibility(View.GONE);
        advertisingLoadFailureLayout.setVisibility(View.GONE);
        loadNewAdvertisingButton.setVisibility(View.VISIBLE);
    }

    private void showFailureLayout() {
        advertisingLoading.setVisibility(View.GONE);
        advertisingThanksLayout.setVisibility(View.GONE);
        advertisingFailureLayout.setVisibility(View.VISIBLE);
        advertisingLoadFailureLayout.setVisibility(View.GONE);
        loadNewAdvertisingButton.setVisibility(View.VISIBLE);
    }

    private void showLoadFailureLayout() {
        advertisingLoading.setVisibility(View.GONE);
        advertisingThanksLayout.setVisibility(View.GONE);
        advertisingFailureLayout.setVisibility(View.GONE);
        advertisingLoadFailureLayout.setVisibility(View.VISIBLE);
        loadNewAdvertisingButton.setVisibility(View.VISIBLE);
    }

    private void loadRewardedVideoAd() {
        showLoading();

        mRewardedVideoAd.loadAd(BuildConfig.ADMOB_UNIT_ID, new AdRequest.Builder().build());
    }

    @Override
    public void onRewarded(RewardItem rewardItem) {
        Log.d(TAG, "onRewarded");
        isRewardSuccess = true;
    }

    @Override
    public void onRewardedVideoAdLeftApplication() {
        Log.d(TAG, "onRewardedVideoAdLeftApplication");
    }

    @Override
    public void onRewardedVideoAdClosed() {
        Log.d(TAG, "onRewardedVideoAdClosed");

        if (isRewardSuccess) {
            showThanksLayout();
        } else {
            showFailureLayout();
        }
    }

    @Override
    public void onRewardedVideoAdFailedToLoad(int errorCode) {
        Log.d(TAG, "onRewardedVideoAdFailedToLoad");
        showLoadFailureLayout();
    }

    @Override
    public void onRewardedVideoAdLoaded() {
        Log.d(TAG, "onRewardedVideoAdLoaded");
        if(mRewardedVideoAd.isLoaded()) {
            isRewardSuccess = false;
            mRewardedVideoAd.show();
        }
    }

    @Override
    public void onRewardedVideoAdOpened() {
        Log.d(TAG, "onRewardedVideoAdOpened");
    }

    @Override
    public void onRewardedVideoStarted() {
        Log.d(TAG, "onRewardedVideoStarted");
    }

    @Override
    public void onRewardedVideoCompleted() {
        Log.d(TAG, "onRewardedVideoCompleted");
    }

    @Override
    public void onResume() {
        if (mRewardedVideoAd != null) {
            mRewardedVideoAd.resume(this);
        }

        super.onResume();
    }

    @Override
    public void onPause() {
        if (mRewardedVideoAd != null) {
            mRewardedVideoAd.pause(this);
        }

        super.onPause();
    }

    @Override
    public void onDestroy() {
        if (mRewardedVideoAd != null) {
            mRewardedVideoAd.destroy(this);
        }

        super.onDestroy();
    }

}
