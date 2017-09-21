package com.appbee.appbeemobile.helper;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

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

    public double getMobileTotalUsedDay(long minStatedTime) {
        double mobileTotalUsedDay = (System.currentTimeMillis() - minStatedTime) / 86400000L; // 86400000L = 1000 * 60 * 60 * 24 (milliseconds / day);
        if (minStatedTime == 0L) {
            mobileTotalUsedDay = 7; // 기간 (일)
        }
        return Math.max(mobileTotalUsedDay, 1.0d);
    }

    public String getFormattedCurrentTime(String format) {
        SimpleDateFormat df = new SimpleDateFormat(format, Locale.getDefault());
        Date date = new Date(System.currentTimeMillis());
        return df.format(date);
    }
}
