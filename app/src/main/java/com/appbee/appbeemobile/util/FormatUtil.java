package com.appbee.appbeemobile.util;

import java.util.List;

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

    public static String formatAppsString(List<String> apps) {
        String result = "";
        for(String app : apps) {
            result += String.format("[%s]", app);
        }
        return result;
    }
}
