package com.formakers.fomes.common.util;

import androidx.annotation.Nullable;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FormatUtil {
    private static final SimpleDateFormat SHORT_DATE_FORMAT = new SimpleDateFormat("M월 d일 (E)", Locale.KOREA);
    private static final SimpleDateFormat LONG_DATE_FORMAT = new SimpleDateFormat("yy.M.d (E)", Locale.KOREA);

    private static final String YOUTUBE_ID_REGEX = "(http[s]?:\\/\\/)?(www\\\\.)?youtu[\\\\.]?be([\\\\.]com)?\\/(watch\\?v=)?([^&?]*)";

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

    @Nullable
    public static String parseYouTubeId(String url) {
        if (url == null) {
            return null;
        }

        Pattern pattern = Pattern.compile(YOUTUBE_ID_REGEX);
        Matcher matcher = pattern.matcher(url);

        if (matcher.find()) {
            return matcher.group(5);
        } else {
            return null;
        }
    }
}
