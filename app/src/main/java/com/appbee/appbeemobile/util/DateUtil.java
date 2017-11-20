package com.appbee.appbeemobile.util;


public class DateUtil {
    public static int calDateDiff(long fromTimestamp, long toTimestamp) {
        return Integer.parseInt(FormatUtil.getDateFromTimestamp(toTimestamp)) - Integer.parseInt(FormatUtil.getDateFromTimestamp(fromTimestamp));
    }
}
