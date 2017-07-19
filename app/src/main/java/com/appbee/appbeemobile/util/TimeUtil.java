package com.appbee.appbeemobile.util;

public class TimeUtil {

    public static long getCurrentTime() {
        return (System.currentTimeMillis() / 1000) * 1000;
    }
}
