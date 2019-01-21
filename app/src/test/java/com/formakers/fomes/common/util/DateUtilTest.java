package com.formakers.fomes.common.util;

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

    @Test
    public void convertDurationFromMilliseconds_호출시__타입에_맞게_변환하고_올림하여_반환한다() {
        float day = DateUtil.convertDurationFromMilliseconds(DateUtil.CONVERT_TYPE_DAYS, 10000000L, 1);
        float hour = DateUtil.convertDurationFromMilliseconds(DateUtil.CONVERT_TYPE_HOURS, 10000000L, 1);
        float min = DateUtil.convertDurationFromMilliseconds(DateUtil.CONVERT_TYPE_MINUTES, 10000000L, 1);
        float sec = DateUtil.convertDurationFromMilliseconds(DateUtil.CONVERT_TYPE_SECONDS, 10000000L, 1);

        assertThat(day).isEqualTo(0.2f);
        assertThat(hour).isEqualTo(2.8f);
        assertThat(min).isEqualTo(166.7f);
        assertThat(sec).isEqualTo(10000f);
    }

    @Test
    public void convertDurationFromMilliseconds_호출시__소수점을_보여주지_않는경우도_정상적으로_반환한다() {
        float day = DateUtil.convertDurationFromMilliseconds(DateUtil.CONVERT_TYPE_DAYS, 10000000L, 0);
        float hour = DateUtil.convertDurationFromMilliseconds(DateUtil.CONVERT_TYPE_HOURS, 10000000L, 0);
        float min = DateUtil.convertDurationFromMilliseconds(DateUtil.CONVERT_TYPE_MINUTES, 10000000L, 0);
        float sec = DateUtil.convertDurationFromMilliseconds(DateUtil.CONVERT_TYPE_SECONDS, 10000000L, 0);

        assertThat(day).isEqualTo(1f);
        assertThat(hour).isEqualTo(3f);
        assertThat(min).isEqualTo(167f);
        assertThat(sec).isEqualTo(10000f);
    }
}