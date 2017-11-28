package com.appbee.appbeemobile.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateUtil {
    private static final SimpleDateFormat INPUT_DATE_FORMAT = new SimpleDateFormat("yyyyMMdd", Locale.KOREA);

    public static int calDateDiff(long fromTimestamp, long toTimestamp) {
        int dDay = 0;

        try {
            String from = INPUT_DATE_FORMAT.format(fromTimestamp);
            Date fromDate = INPUT_DATE_FORMAT.parse(from);

            String to = INPUT_DATE_FORMAT.format(toTimestamp);
            Date toDate = INPUT_DATE_FORMAT.parse(to);

            if (fromDate.compareTo(toDate) < 0) {
                dDay = (int) ((toDate.getTime() - fromDate.getTime()) / (1000 * 60 * 60 * 24));
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return dDay;
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
