package com.appbee.appbeemobile.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.appbee.appbeemobile.R;

import butterknife.BindView;

public class OnboardingActivity extends BaseActivity {

    @BindView(R.id.onboarding_view_pager)
    ViewPager onboardingViewPager;

    PagerAdapter onboardingPagerAdapter;

    @DrawableRes int[] onboardingImages = {
            R.drawable.onboarding_background_1,
            R.drawable.onboarding_background_2,
            R.drawable.onboarding_background_3
    };

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
            imageView.setImageDrawable(getDrawable(onboardingImages[position]));

            container.addView(imageView);

            return imageView;
        }

        @Override
        public int getCount() {
            return onboardingImages.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeViewAt(position);
        }
    }
}
