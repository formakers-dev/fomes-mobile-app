package com.appbee.appbeemobile.util;

public class TimeUtil {
    public static long getCurrentTime() {
        return (System.currentTimeMillis() / 1000) * 1000;
    }

    public static long getMobileTotalUsedDay(long minStatedTime) {
        long mobileTotalUsedDay = (System.currentTimeMillis() - minStatedTime) / (1000 * 60 * 60 * 24);
        if (minStatedTime == 0L) {
            mobileTotalUsedDay = 365 * 2; // 2년치 기간(일)
        }
        return mobileTotalUsedDay;
    }
}
