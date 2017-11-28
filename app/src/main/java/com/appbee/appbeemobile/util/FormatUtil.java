package com.appbee.appbeemobile.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class FormatUtil {
    private static final String EMPTY_STRING = "";
    private static final SimpleDateFormat SHORT_DATE_FORMAT = new SimpleDateFormat("M월 d일 (E)", Locale.KOREA);
    private static final SimpleDateFormat LONG_DATE_FORMAT = new SimpleDateFormat("yy.M.d (E)", Locale.KOREA);

    public static String parseEmailName(String email) {
        return email.split("@")[0];
    }

    public static String convertInputDateFormat(Date inputDateString, String displayFormat) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(displayFormat, Locale.KOREA);
        return simpleDateFormat.format(inputDateString);
    }

    public static String toShortDateFormat(Date date) {
        return SHORT_DATE_FORMAT.format(date);
    }

    public static String toLongDateFormat(Date date) {
        return LONG_DATE_FORMAT.format(date);
    }
}
