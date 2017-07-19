package com.appbee.appbeemobile.util;

import org.junit.Test;
import static org.assertj.core.api.Java6Assertions.assertThat;

public class TimeUtilTest {

    @Test
    public void getCurrentTime_시스템_현재시간에서_millsecond단위_절삭하여_결과를리턴한다() throws Exception {
        assertThat(TimeUtil.getCurrentTime()%1000).isEqualTo(0L);
    }
}