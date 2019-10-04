package com.formakers.fomes.common.helper;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import androidx.annotation.DrawableRes;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.transition.DrawableCrossFadeTransition;
import com.bumptech.glide.request.transition.Transition;
import com.bumptech.glide.request.transition.TransitionFactory;
import com.formakers.fomes.R;

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
        loadImage(imageView, imageUrl, null);
    }

    public void loadImage(ImageView imageView, String imageUrl, @Nullable RequestOptions requestOptions) {
        loadImage(imageView, imageUrl, requestOptions, true);
    }

    public void loadImage(ImageView imageView, String imageUrl, @Nullable RequestOptions requestOptions, boolean isUsePlaceHolder) {
        RequestBuilder<Drawable> requestBuilder = Glide.with(context).load(imageUrl);

        // requestOption
        if (requestOptions == null) {
            requestOptions = new RequestOptions();
        }

        if (isUsePlaceHolder) {
            requestOptions = requestOptions.placeholder(new ColorDrawable(context.getResources().getColor(R.color.fomes_deep_gray)));
        }

        requestBuilder = requestBuilder.apply(requestOptions);

        // transition
        requestBuilder = requestBuilder.transition(DrawableTransitionOptions.with(new DrawableAlwaysCrossFadeFactory()));

        // inject
        requestBuilder.into(imageView);
    }

    public void loadGifImage(ImageView imageView, @DrawableRes int resId) {
        Glide.with(context)
                .asGif()
                .load(resId)
                .into(imageView);
    }

    public static class DrawableAlwaysCrossFadeFactory implements TransitionFactory<Drawable> {

        @Override
        public Transition<Drawable> build(DataSource dataSource, boolean isFirstResource) {
            return new DrawableCrossFadeTransition(300, true);
        }
    }
}
