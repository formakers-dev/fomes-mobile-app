package com.formakers.fomes.advertising;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;

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

    @BindView(R.id.advertising_loading) View advertisingLoading;
    @BindView(R.id.advertising_thanks_layout) ViewGroup advertisingThanksLayout;
    @BindView(R.id.advertising_failure_layout) ViewGroup advertisingFailureLayout;
    @BindView(R.id.load_new_advertising) View loadNewAdvertisingButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.setContentView(R.layout.activity_advertising);

        getSupportActionBar().setTitle(R.string.main_menu_advertising);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        MobileAds.initialize(this, "ca-app-pub-3940256099942544~3347511713");
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
        loadNewAdvertisingButton.setVisibility(View.GONE);
    }

    private void showThanksLayout() {
        advertisingLoading.setVisibility(View.GONE);
        advertisingThanksLayout.setVisibility(View.VISIBLE);
        advertisingFailureLayout.setVisibility(View.GONE);
        loadNewAdvertisingButton.setVisibility(View.VISIBLE);
    }

    private void showFailureLayout() {
        advertisingLoading.setVisibility(View.GONE);
        advertisingThanksLayout.setVisibility(View.GONE);
        advertisingFailureLayout.setVisibility(View.VISIBLE);
        loadNewAdvertisingButton.setVisibility(View.VISIBLE);
    }

    private void loadRewardedVideoAd() {
        showLoading();

        mRewardedVideoAd.loadAd("ca-app-pub-3940256099942544/5224354917",
                new AdRequest.Builder().build());
    }

    @Override
    public void onRewarded(RewardItem rewardItem) {
        Log.d(TAG, "onRewarded");
        showThanksLayout();
    }

    @Override
    public void onRewardedVideoAdLeftApplication() {
        Log.d(TAG, "onRewardedVideoAdLeftApplication");
        showFailureLayout();
    }

    @Override
    public void onRewardedVideoAdClosed() {
        Log.d(TAG, "onRewardedVideoAdClosed");
    }

    @Override
    public void onRewardedVideoAdFailedToLoad(int errorCode) {
        Log.d(TAG, "onRewardedVideoAdFailedToLoad");
        showFailureLayout();
    }

    @Override
    public void onRewardedVideoAdLoaded() {
        Log.d(TAG, "onRewardedVideoAdLoaded");
        if(mRewardedVideoAd.isLoaded()) {
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
        showFailureLayout();
    }

    @Override
    public void onRewardedVideoCompleted() {
        Log.d(TAG, "onRewardedVideoCompleted");
        showThanksLayout();
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
