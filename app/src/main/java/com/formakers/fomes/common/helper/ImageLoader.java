package com.formakers.fomes.common.helper;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import androidx.annotation.DrawableRes;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.RequestManager;
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
    private final RequestManager requestManager;

    @Inject
    public ImageLoader(Context context, RequestManager requestManager) {
        this.context = context;
        this.requestManager = requestManager;
    }

    public void loadImage(ImageView imageView, String imageUrl) {
        loadImage(imageView, imageUrl, null);
    }

    public void loadImage(ImageView imageView, String imageUrl, @Nullable RequestOptions requestOptions) {
        loadImage(imageView, imageUrl, requestOptions, true, true);
    }

    public void loadImage(ImageView imageView, String imageUrl, @Nullable RequestOptions requestOptions, boolean isUsePlaceHolder) {
        loadImage(imageView, imageUrl, requestOptions, isUsePlaceHolder, true);
    }

    // 리팩토링 필요하다... 계속 플래그와 네이밍에 의존 할 수는 없어....
    public void loadImageWithoutCrossFade(ImageView imageView, String imageUrl, @Nullable RequestOptions requestOptions, boolean isUsePlaceHolder) {
        loadImage(imageView, imageUrl, requestOptions, isUsePlaceHolder, false);
    }

    public void loadImage(ImageView imageView, String imageUrl, @Nullable RequestOptions requestOptions, boolean isUsePlaceHolder, boolean isUseCrossFade) {
        RequestBuilder<Drawable> requestBuilder = requestManager.load(imageUrl);

        // requestOption
        if (requestOptions == null) {
            requestOptions = new RequestOptions();
        }

        if (isUsePlaceHolder) {
            requestOptions = requestOptions.placeholder(new ColorDrawable(context.getResources().getColor(R.color.fomes_deep_gray)));
        }

        requestBuilder = requestBuilder.apply(requestOptions);

        // transition
        if (isUseCrossFade) {
            requestBuilder = requestBuilder.transition(DrawableTransitionOptions.with(new DrawableAlwaysCrossFadeFactory()));
        }

        // inject
        requestBuilder.into(imageView);
    }

    @Deprecated
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
