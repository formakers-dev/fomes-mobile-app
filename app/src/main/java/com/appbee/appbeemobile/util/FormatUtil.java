package com.appbee.appbeemobile.util;

public class FormatUtil {
    public static String formatLongCategoryName(String categoryName) {
        if (categoryName.length() > 4) {
            int midIndex = (categoryName.length() + 1) / 2;
            return categoryName.substring(0, midIndex) + "\n" + categoryName.substring(midIndex);
        } else {
            return categoryName;
        }
    }

    public static String parseEmailName(String email) {
        return email.split("@")[0];
    }
}
