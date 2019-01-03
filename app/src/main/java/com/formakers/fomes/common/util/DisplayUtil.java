package com.formakers.fomes.common.util;

import android.content.Context;

public class DisplayUtil {
    private static final float DEFAULT_HDIP_DENSITY_SCALE = 1.5f;

    public static int pixelToDp(Context context, int pixel) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int)(pixel / DEFAULT_HDIP_DENSITY_SCALE * scale);
    }

    public static int dpToPixel(Context context, int dp) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int)(dp / scale * DEFAULT_HDIP_DENSITY_SCALE);
    }
}
