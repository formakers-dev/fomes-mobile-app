package com.appbee.appbeemobile.util;

import com.google.common.collect.Lists;

import org.junit.Test;

import java.util.List;

import static org.assertj.core.api.Java6Assertions.assertThat;


public class FormatUtilTest {

    @Test
    public void formatAppsString호출시_대괄호로감싼Apps텍스트가리턴된다() throws Exception {
        List<String> apps = Lists.newArrayList("첫번째앱");
        assertThat(FormatUtil.formatAppsString(apps)).isEqualTo("[첫번째앱]");

        apps = Lists.newArrayList("첫번째앱", "두번째앱");
        assertThat(FormatUtil.formatAppsString(apps)).isEqualTo("[첫번째앱][두번째앱]");
    }

    @Test
    public void convertInputDateFormat호출시_입력받은_포맷으로_입력받은_날짜를_출력한다() throws Exception {
        String result = FormatUtil.convertInputDateFormat("20170102", "MM.dd");
        assertThat(result).isEqualTo("01.02");
    }

    @Test
    public void getDateFromTimestamp() throws Exception {
        long timeStamp = 1511182800000L;        //2017-11-20 22:00:00
        assertThat(FormatUtil.getDateFromTimestamp(timeStamp)).isEqualTo("20171120");
        timeStamp = 1511193600000L; // 2017-11-21 01:00:00
        assertThat(FormatUtil.getDateFromTimestamp(timeStamp)).isEqualTo("20171121");
    }

    @Test
    public void getTimestampFromDate호출시_해달날짜의0시의_timestamp를_리턴한다() throws Exception {
        String date = "20171121";
        assertThat(FormatUtil.getTimestampFromDate(date)).isEqualTo(1511190000000L); // 2017-11-21 00:00:00   1511190000000
    }
}