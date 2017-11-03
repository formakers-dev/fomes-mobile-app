package com.appbee.appbeemobile.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
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

    public static String convertInputDateFormat(String inputDateString, String displayFormat) {
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(displayFormat, Locale.KOREA);
            Date fromDate = INPUT_DATE_FORMAT.parse(inputDateString);
            return simpleDateFormat.format(fromDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return EMPTY_STRING;
    }
}
