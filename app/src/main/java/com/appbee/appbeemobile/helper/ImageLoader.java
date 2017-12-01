package com.appbee.appbeemobile.helper;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class ImageLoader {
    private final Context context;

    @Inject
    public ImageLoader(Context context) {
        this.context = context;
    }

    public void loadImage(ImageView imageView, String imageUrl) {
        Glide.with(context)
                .load(imageUrl)
                .into(imageView);
    }

    public void loadImage(ImageView imageView, String imageUrl, RequestOptions requestOptions) {
        Glide.with(context)
                .load(imageUrl)
                .apply(requestOptions)
                .into(imageView);
    }

    public void loadGifImage(ImageView imageView, String imageUrl) {
        Glide.with(context)
                .asGif()
                .load(imageUrl)
                .into(imageView);
    }
}
