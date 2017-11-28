package com.appbee.appbeemobile.util;

import org.junit.Test;

import java.util.Calendar;

import static org.assertj.core.api.Java6Assertions.assertThat;


public class FormatUtilTest {

    @Test
    public void Date유형의_날짜로_convertInputDateFormat호출시_입력받은_포맷으로_입력받은_날짜를_출력한다() throws Exception {
        Calendar calendar = Calendar.getInstance();
        calendar.set(2017, 11, 25);
        String result = FormatUtil.convertInputDateFormat(calendar.getTime(), "MM/dd");
        assertThat(result).isEqualTo("12/25");
    }

    @Test
    public void toShortDateFormat호출시_Date를_단순유형의_날짜로_출력한다() throws Exception {
        Calendar calendar = Calendar.getInstance();
        calendar.set(2017, 1, 2);
        String result = FormatUtil.toShortDateFormat(calendar.getTime());
        assertThat(result).isEqualTo("2월 2일 (목)");
    }

    @Test
    public void toLongDateFormat호출시_Date를_단순유형의_날짜로_출력한다() throws Exception {
        Calendar calendar = Calendar.getInstance();
        calendar.set(2017, 1, 2);
        String result = FormatUtil.toLongDateFormat(calendar.getTime());
        assertThat(result).isEqualTo("17.2.2 (목)");
    }
}