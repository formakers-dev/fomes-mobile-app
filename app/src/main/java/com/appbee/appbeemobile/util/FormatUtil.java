package com.appbee.appbeemobile.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class FormatUtil {
    private static final String EMPTY_STRING = "";
    public static final SimpleDateFormat INPUT_DATE_FORMAT = new SimpleDateFormat("yyyyMMdd", Locale.KOREA);

    public static String parseEmailName(String email) {
        return email.split("@")[0];
    }

    public static String formatAppsString(List<String> apps) {
        String result = EMPTY_STRING;
        for (String app : apps) {
            result += String.format("[%s]", app);
        }
        return result;
    }

    public static String convertInputDateFormat(Date inputDateString, String displayFormat) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(displayFormat, Locale.KOREA);
        return simpleDateFormat.format(inputDateString);
    }

    public static String getDateFromTimestamp(long timeStamp) {
        return INPUT_DATE_FORMAT.format(new Date(timeStamp));
    }

    public static long getTimestampFromDate(String date) {
        int integerDate = Integer.parseInt(date);
        int year = integerDate / 10000;
        int month = (integerDate - year * 10000) / 100;
        int day = (integerDate - year * 10000 - month * 100);

        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month - 1, day, 0, 0, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        return calendar.getTimeInMillis();
    }

    public static String getDayOfWeek(Date date) {
        String result = "";

        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

        int dayNum = cal.get(Calendar.DAY_OF_WEEK);
        switch (dayNum) {
            case Calendar.SUNDAY:
                result = "일";
                break;
            case Calendar.MONDAY:
                result = "월";
                break;
            case Calendar.TUESDAY:
                result = "화";
                break;
            case Calendar.WEDNESDAY:
                result = "수";
                break;
            case Calendar.THURSDAY:
                result = "목";
                break;
            case Calendar.FRIDAY:
                result = "금";
                break;
            case Calendar.SATURDAY:
                result = "토";
                break;
        }
        return result;
    }
}
