package com.appbee.appbeemobile.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.appbee.appbeemobile.model.Project;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

public class ProjectImagePagerAdapter extends PagerAdapter {

    private Context context;
    private List<Project.ImageObject> images;

    public ProjectImagePagerAdapter(Context context, List<Project.ImageObject> images) {
        this.context = context;
        this.images = images;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        ImageView imageView = new ImageView(context);
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);

        Glide.with(context)
                .load(images.get(position).getUrl()).apply(new RequestOptions().override(1300, 1000).centerCrop())
                .into(imageView);

        container.addView(imageView);

        return imageView;
    }

    @Override
    public int getCount() {
        return images.size();
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
