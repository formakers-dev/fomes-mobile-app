package com.appbee.appbeemobile.util;


import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class DateUtil {
    public static final SimpleDateFormat INPUT_DATE_FORMAT = new SimpleDateFormat("yyyyMMdd", Locale.KOREA);

    public static int calDateDiff(long fromTimestamp, long toTimestamp) {
        return Integer.parseInt(FormatUtil.getDateFromTimestamp(toTimestamp)) - Integer.parseInt(FormatUtil.getDateFromTimestamp(fromTimestamp));
    }

    public static int calBeforeDate(int currentDate, int days) {
        int year = currentDate / 10000;
        int month = (currentDate - year * 10000) / 100;
        int day = (currentDate - year * 10000) % 100;

        Calendar calender = Calendar.getInstance();
        calender.set(year, month - 1, day);
        calender.add(Calendar.DATE, -days);

        return Integer.parseInt(INPUT_DATE_FORMAT.format(calender.getTime()));
    }
}
