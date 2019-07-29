package com.formakers.fomes.common.util;

import org.junit.Test;

import java.util.Calendar;

import static org.assertj.core.api.Java6Assertions.assertThat;


public class StringFormatUtilTest {

    @Test
    public void Date유형의_날짜로_convertInputDateFormat호출시_입력받은_포맷으로_입력받은_날짜를_출력한다() throws Exception {
        Calendar calendar = Calendar.getInstance();
        calendar.set(2017, 11, 25);
        String result = StringFormatUtil.convertInputDateFormat(calendar.getTime(), "MM/dd");
        assertThat(result).isEqualTo("12/25");
    }

    @Test
    public void toShortDateFormat호출시_Date를_단순유형의_날짜로_출력한다() throws Exception {
        Calendar calendar = Calendar.getInstance();
        calendar.set(2017, 1, 2);
        String result = StringFormatUtil.toShortDateFormat(calendar.getTime());
        assertThat(result).isEqualTo("2월 2일 (목)");
    }

    @Test
    public void toLongDateFormat호출시_Date를_단순유형의_날짜로_출력한다() throws Exception {
        Calendar calendar = Calendar.getInstance();
        calendar.set(2017, 1, 2);
        String result = StringFormatUtil.toLongDateFormat(calendar.getTime());
        assertThat(result).isEqualTo("17.2.2 (목)");
    }

    @Test
    public void parseYouTubeId호출시_url내용중_유투브ID를_파싱하여_리턴한다() throws Exception {
        assertThat(StringFormatUtil.parseYouTubeId("www.youtube.com/watch?v=O92yHB0MDZ8&index=9&list=RDYso2Bbo5Ptk")).isEqualTo("O92yHB0MDZ8");
        assertThat(StringFormatUtil.parseYouTubeId("http://www.youtube.com/watch?v=O92yHB0MDZ8&index=9&list=RDYso2Bbo5Ptk")).isEqualTo("O92yHB0MDZ8");
        assertThat(StringFormatUtil.parseYouTubeId("youtu.be/O92yHB0MDZ8")).isEqualTo("O92yHB0MDZ8");
        assertThat(StringFormatUtil.parseYouTubeId("https://youtu.be/O92yHB0MDZ8?list=RDYso2Bbo5Ptk&t=2039")).isEqualTo("O92yHB0MDZ8");
    }

    @Test
    public void parseYouTubeId호출시_유투브URL이_아닌경우_null을_리턴한다() throws Exception {
        assertThat(StringFormatUtil.parseYouTubeId("www.dyoudfagtubse.com/watch?v=O92yHB0MDZ8&index=9&list=RDYso2Bbo5Ptk")).isNull();
    }

    @Test
    public void parseYouTubeId호출시_유투브URL이_null인경우_null을_리턴한다() throws Exception {
        assertThat(StringFormatUtil.parseYouTubeId(null)).isNull();
    }
}