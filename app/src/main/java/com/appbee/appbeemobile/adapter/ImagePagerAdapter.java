package com.appbee.appbeemobile.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.appbee.appbeemobile.R;
import com.appbee.appbeemobile.model.Project;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

public class ImagePagerAdapter extends PagerAdapter {

    private Context context;
    private List<Project.ImageObject> imageList;

    public ImagePagerAdapter(Context context, List<Project.ImageObject> imageList) {
        this.context = context;
        this.imageList = imageList;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        ImageView imageView = new ImageView(context);
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        Glide.with(context)
                .load(imageList.get(position).getUrl()).apply(new RequestOptions().override(1300, 1000).centerCrop())
                .into(imageView);
        imageView.setTag(R.string.tag_key_image_url, imageList.get(position).getUrl());
        container.addView(imageView);
        return imageView;
    }

    @Override
    public int getCount() {
        return imageList.size();
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
