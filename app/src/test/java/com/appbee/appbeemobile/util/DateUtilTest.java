package com.appbee.appbeemobile.util;

import org.junit.Ignore;
import org.junit.Test;

import java.util.Calendar;

import static org.assertj.core.api.Java6Assertions.assertThat;

@Ignore
public class DateUtilTest {
    @Test
    public void calDateDiff호출시_날짜차이를_계산하여_리턴한다() throws Exception {
        assertThat(DateUtil.calDateDiff(1511182800000L, 1511193600000L)).isEqualTo(1);  //2017-11-20 22:00:00 ~ 2017-11-21 01:00:00
        assertThat(DateUtil.calDateDiff(1511182800000L, 1511182800000L)).isEqualTo(0);  //2017-11-20 22:00:00 ~ 2017-11-20 22:00:00
        assertThat(DateUtil.calDateDiff(1511182800000L, 1511182801000L)).isEqualTo(0);  //2017-11-20 22:00:00 ~ 2017-11-20 22:00:01
        assertThat(DateUtil.calDateDiff(1511189999999L, 1511190000000L)).isEqualTo(1);  //2017-11-20 23:59:59 999 ~ 2017-11-21 00:00:00
    }

    @Test
    public void calDateDiff호출시_다른월에_대한_날짜차이를_계산하여_리턴한다() throws Exception {
        assertThat(DateUtil.calDateDiff(1511182800000L, 1512054000000L)).isEqualTo(11);  //2017-11-20 22:00:00 ~ 2017-12-01 00:00:00
    }

    @Test
    public void calBeforeDate호출시_입력일자이전의_날짜를_계산하여_리턴한다() throws Exception {
        assertThat(DateUtil.calBeforeDate(20171121, 30)).isEqualTo(20171022);
    }

    @Test
    public void getDateFromTimestamp() throws Exception {
        long timestamp = 1511182800000L;        //2017-11-20 22:00:00
        assertThat(DateUtil.getDateFromTimestamp(timestamp)).isEqualTo("20171120");
        timestamp = 1511193600000L; // 2017-11-21 01:00:00
        assertThat(DateUtil.getDateFromTimestamp(timestamp)).isEqualTo("20171121");
    }

    @Test
    public void getTimestampFromDate호출시_해달날짜의0시의_timestamp를_리턴한다() throws Exception {
        String date = "20171121";
        assertThat(DateUtil.getTimestampFromDate(date)).isEqualTo(1511190000000L); // 2017-11-21 00:00:00   1511190000000
    }

    @Test
    public void getDayOfWeek호출시_해당날짜의_요일을_호출한다() throws Exception {
        Calendar calendar = Calendar.getInstance();
        calendar.set(2017, 11, 25); // 12월
        String result = DateUtil.getDayOfWeek(calendar.getTime());
        assertThat(result).isEqualTo("월");
    }
}