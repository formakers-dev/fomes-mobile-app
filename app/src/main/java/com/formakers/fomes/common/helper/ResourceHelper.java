package com.formakers.fomes.common.helper;

import android.content.Context;
import android.os.Build;
import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class ResourceHelper {

    private final Context context;

    @Inject
    public ResourceHelper(Context context) {
        this.context = context;
    }

    public
    @ColorInt
    int getColorValue(@ColorRes int colorResId) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return context.getResources().getColor(colorResId);
        } else {
            return context.getColor(colorResId);
        }
    }
}