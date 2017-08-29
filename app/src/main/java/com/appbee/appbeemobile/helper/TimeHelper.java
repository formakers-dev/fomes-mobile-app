package com.appbee.appbeemobile.helper;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class TimeHelper {

    @Inject
    public TimeHelper() {

    }

    public long getCurrentTime() {
        return System.currentTimeMillis();
    }

    public long getMobileTotalUsedDay(long minStatedTime) {
        long mobileTotalUsedDay = (System.currentTimeMillis() - minStatedTime) / (1000 * 60 * 60 * 24);
        if (minStatedTime == 0L) {
            mobileTotalUsedDay = 365 * 2; // 2년치 기간(일)
        }
        return Math.max(mobileTotalUsedDay, 1);
    }
}
