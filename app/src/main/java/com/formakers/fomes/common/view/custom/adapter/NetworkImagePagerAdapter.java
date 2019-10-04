package com.formakers.fomes.common.view.custom.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.formakers.fomes.R;
import com.formakers.fomes.common.helper.ImageLoader;

import java.util.List;

public class NetworkImagePagerAdapter extends PagerAdapter {

    List<String> imageUrlList;

    public NetworkImagePagerAdapter(List<String> imageUrlList) {
        this.imageUrlList = imageUrlList;
    }

    @Override
    public int getCount() {
        return imageUrlList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        ImageView imageView = new ImageView(container.getContext());
        Glide.with(container.getContext()).load(imageUrlList.get(position))
                .apply(new RequestOptions().override(100, 160).centerCrop()
                        .placeholder(R.drawable.screenshot_placeholder))
                .transition(DrawableTransitionOptions.with(new ImageLoader.DrawableAlwaysCrossFadeFactory()))
                .into(imageView);
        container.addView(imageView);
        return imageView;
    }

    @Override
    public float getPageWidth(int position) {
        return 0.333f;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((ImageView) object);
    }
}