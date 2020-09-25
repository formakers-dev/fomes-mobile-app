package com.formakers.fomes.advertising;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

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
    @BindView(R.id.load_new_advertising) View loadNewAdvertisingButton;
    @BindView(R.id.advertising_content_imageview) ImageView contentImageView;
    @BindView(R.id.advertising_content_textview)
    TextView contentTextView;

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
        contentImageView.setImageResource(R.drawable.fomes_description_1);
        contentTextView.setText("클릭해줘서 고맙다멍❤️\n광고 로딩까지 조금만 기다려주라멍!");
        loadNewAdvertisingButton.setVisibility(View.GONE);
    }

    private void showThanksLayout() {
        advertisingLoading.setVisibility(View.GONE);
        contentImageView.setImageResource(R.drawable.fomes_coffee);
        contentTextView.setText("여러분의 후원에 감사하다멍!\n더 멋진 포메스로 보답하겠다멍!");
        loadNewAdvertisingButton.setVisibility(View.VISIBLE);
    }

    private void showFailureLayout() {
        advertisingLoading.setVisibility(View.GONE);
        contentImageView.setImageResource(R.drawable.fomes_face_cry);
        contentTextView.setText("후원을 위해서는\n광고를 끝까지 봐야한다멍ㅠㅠ");
        loadNewAdvertisingButton.setVisibility(View.VISIBLE);
    }

    private void showLoadFailureLayout() {
        advertisingLoading.setVisibility(View.GONE);
        contentImageView.setImageResource(R.drawable.fomes_face_cry);
        contentTextView.setText("광고 로딩에 실패했다멍ㅠㅜ\n아래 버튼을 눌러 다시 시도해 달라멍🙏");
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
