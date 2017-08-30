package com.appbee.appbeemobile.helper;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class TimeHelper {

    public static String DATE_FORMAT = "yyyy-MM-dd";

    @Inject
    public TimeHelper() {

    }

    public long getCurrentTime() {
        return System.currentTimeMillis();
    }

    public long getMobileTotalUsedDay(long minStatedTime) {
        long mobileTotalUsedDay = (System.currentTimeMillis() - minStatedTime) / 86400000L; // 86400000L = 1000 * 60 * 60 * 24 (milliseconds / day);
        if (minStatedTime == 0L) {
            mobileTotalUsedDay = 365 * 2; // 2년치 기간(일)
        }
        return Math.max(mobileTotalUsedDay, 1);
    }

    public String getFormattedCurrentTime(String format) {
        SimpleDateFormat df = new SimpleDateFormat(format);
        Date date = new Date(System.currentTimeMillis());
        return df.format(date);
    }
}
