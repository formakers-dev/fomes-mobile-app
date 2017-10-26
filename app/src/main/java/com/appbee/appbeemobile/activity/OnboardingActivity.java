package com.appbee.appbeemobile.activity;

import android.app.Activity;
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

    private static final String TAG = OnboardingActivity.class.getSimpleName();
    private static final int RC_LOGIN = 9002;

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
        startActivityForResult(intent, RC_LOGIN);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_LOGIN) {
            if (resultCode == Activity.RESULT_OK) {
                finish();
            }
        }
    }
}
