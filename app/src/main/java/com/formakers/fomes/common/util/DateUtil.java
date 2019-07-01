package com.formakers.fomes.common.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateUtil {
    private static final SimpleDateFormat INPUT_DATE_FORMAT = new SimpleDateFormat("yyyyMMdd", Locale.KOREA);

    public static final int CONVERT_TYPE_SECONDS = 1;
    public static final int CONVERT_TYPE_MINUTES = 2;
    public static final int CONVERT_TYPE_HOURS = 3;
    public static final int CONVERT_TYPE_DAYS = 4;


    public static int calculateDdays(long fromTimestamp, long toTimestamp) {
        int dDay = 0;

        try {
            // 시간 단위 절사를 위함
            String from = INPUT_DATE_FORMAT.format(fromTimestamp);
            Date fromDate = INPUT_DATE_FORMAT.parse(from);

            String to = INPUT_DATE_FORMAT.format(toTimestamp);
            Date toDate = INPUT_DATE_FORMAT.parse(to);

            dDay = (int) ((toDate.getTime() - fromDate.getTime()) / (1000 * 60 * 60 * 24));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return dDay;
    }

    public static int calDateDiff(long fromTimestamp, long toTimestamp) {
        int dDay = 0;

        try {
            // 시간 단위 절사를 위함
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

    public static Date getDateWithoutTime(Date date) {
        SimpleDateFormat onlyDateFormat = new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault());
        String onlyDateString = onlyDateFormat.format(date);
        try {
            return onlyDateFormat.parse(onlyDateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getDateStringFromTimestamp(long timestamp) {
        return INPUT_DATE_FORMAT.format(new Date(timestamp));
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

    public static float convertDurationFromMilliseconds(int convertedType, long milliseconds, int floatPoint) {
            float converted = milliseconds;

            switch (convertedType) {
                case CONVERT_TYPE_DAYS:
                    converted = converted / 24;
                case CONVERT_TYPE_HOURS:
                    converted = converted / 60;
                case CONVERT_TYPE_MINUTES:
                    converted = converted / 60;
                case CONVERT_TYPE_SECONDS:
                    converted =  converted / 1000;
                    break;
                default:
                    converted = milliseconds;
            }

            int temp = 1;
            if (floatPoint > 0) {
                temp = 10 * floatPoint;
            }

            return (float) Math.ceil(converted * temp) / temp;
    }

    public static Date convertHTTPHeaderDateToDate(String dateString) {
        SimpleDateFormat format = new SimpleDateFormat("EEE, dd MMM yyyy hh:mm:ss zzz");

        try {
            return format.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return null;
    }
}
