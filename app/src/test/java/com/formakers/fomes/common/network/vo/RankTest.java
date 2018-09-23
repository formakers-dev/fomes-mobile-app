package com.formakers.fomes.common.network.vo;

import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Java6Assertions.assertThat;

public class RankTest {

    Rank subject;

    @Before
    public void setUp() throws Exception {
        subject = new Rank("userId", 1, 10000000L);
    }

    @Test
    public void getContent_호출시__타입에_맞게_변환하고_올림하여_반환한다() {
        float day = subject.getContent(Rank.CONVERT_TYPE_DAYS);
        float hour = subject.getContent(Rank.CONVERT_TYPE_HOURS);
        float min = subject.getContent(Rank.CONVERT_TYPE_MINUTES);
        float sec = subject.getContent(Rank.CONVERT_TYPE_SECONDS);

        assertThat(day).isEqualTo(0.2f);
        assertThat(hour).isEqualTo(2.8f);
        assertThat(min).isEqualTo(166.7f);
        assertThat(sec).isEqualTo(10000f);
    }
}