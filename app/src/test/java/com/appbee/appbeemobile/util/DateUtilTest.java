package com.appbee.appbeemobile.util;

import org.junit.Ignore;
import org.junit.Test;

import static org.assertj.core.api.Java6Assertions.assertThat;

@Ignore
public class DateUtilTest {
    @Test
    public void calDateDiff호출시_날짜차이를_계산하여_리턴한다() throws Exception {

        assertThat(DateUtil.calDateDiff(1511182800000L, 1511193600000L)).isEqualTo(1);  //2017-11-20 22:00:00 ~ 2017-11-21 01:00:00
        assertThat(DateUtil.calDateDiff(1511182800000L, 1511182800000L)).isEqualTo(0);  //2017-11-20 22:00:00 ~ 2017-11-20 22:00:00
        assertThat(DateUtil.calDateDiff(1511182800000L, 1511182801000L)).isEqualTo(0);  //2017-11-20 22:00:00 ~ 2017-11-20 22:00:01

    }

    @Test
    public void calBeforeDate호출시_입력일자이전의_날짜를_계산하여_리턴한다() throws Exception {
        assertThat(DateUtil.calBeforeDate(20171121, 30)).isEqualTo(20171022);
    }
}