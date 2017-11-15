package com.appbee.appbeemobile.helper;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class TimeHelper {

    private static final long SHORT_TERM_STAT_PADDING_TIME = 300000L;   //5분 이내 데이터는 정확성을 보장하지 못하는 문제를 해결하기 위한 상수값

    @Inject
    public TimeHelper() {

    }

    public long getCurrentTime() {
        return System.currentTimeMillis();
    }

    public long getStatBasedCurrentTime() {
        return getCurrentTime() - SHORT_TERM_STAT_PADDING_TIME;
    }
}
