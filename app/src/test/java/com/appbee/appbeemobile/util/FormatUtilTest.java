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
}