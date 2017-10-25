package com.appbee.appbeemobile.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.Button;

import com.appbee.appbeemobile.R;
import com.appbee.appbeemobile.adapter.CommonPagerAdapter;

import butterknife.BindView;
import butterknife.OnClick;

public class OnboardingActivity extends BaseActivity {

    @DrawableRes
    static final int[] ONBOARDING_IMAGES = {
            R.drawable.onboarding_background_1,
            R.drawable.onboarding_background_2,
            R.drawable.onboarding_background_3
    };

    @BindView(R.id.onboarding_view_pager)
    ViewPager onboardingViewPager;

    @BindView(R.id.login_button)
    Button loginButton;

    @OnClick(R.id.login_button)
    public void onLoginButtonClick(View view) {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        onboardingViewPager.setAdapter(new CommonPagerAdapter(this, ONBOARDING_IMAGES));
    }
}
