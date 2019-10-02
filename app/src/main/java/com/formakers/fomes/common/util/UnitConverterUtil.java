package com.formakers.fomes.common.util;

import android.util.DisplayMetrics;
import android.util.TypedValue;

public class UnitConverterUtil {

    // TODO : 흠... 뭔가 찜찜한디............ 사용할 때 다시 체크해보기

    /**
     * Convert pixel to dp
     *
     * @param displayMetrics Use context.getResources().getDisplayMetrics()
     * @param pixel pixel
     *
     * @return dp
     */
    public static float pixelToDp(DisplayMetrics displayMetrics, int pixel) {
        return pixel / (displayMetrics.densityDpi / 160f);
    }

    /**
     * Convert dp to pixel
     *
     * @param displayMetrics Use context.getResources().getDisplayMetrics()
     * @param dp dp
     *
     * @return pixel
     */
    public static float dpToPixel(DisplayMetrics displayMetrics, int dp) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, displayMetrics);
    }
}
