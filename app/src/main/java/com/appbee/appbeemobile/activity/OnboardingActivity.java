package com.appbee.appbeemobile.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.appbee.appbeemobile.R;

import butterknife.BindView;
import butterknife.OnClick;

public class OnboardingActivity extends BaseActivity {

    @DrawableRes static final int[] ONBOARDING_IMAGES = {
            R.drawable.onboarding_background_1,
            R.drawable.onboarding_background_2,
            R.drawable.onboarding_background_3
    };

    PagerAdapter onboardingPagerAdapter;

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

        onboardingPagerAdapter = new OnboardingPagerAdapter(this);
        onboardingViewPager.setAdapter(onboardingPagerAdapter);
    }

    private class OnboardingPagerAdapter extends PagerAdapter {

        Context context;

        OnboardingPagerAdapter(Context context) {
            this.context = context;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            ImageView imageView = new ImageView(context);
            imageView.setImageDrawable(getDrawable(ONBOARDING_IMAGES[position]));

            container.addView(imageView);

            return imageView;
        }

        @Override
        public int getCount() {
            return ONBOARDING_IMAGES.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            if (object instanceof View) {
                container.removeView((View) object);
            }
        }
    }
}
