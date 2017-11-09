package com.appbee.appbeemobile.helper;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class TimeHelper {

    public static final long MILLISECONDS_OF_MONTH = 30*24*60*60*1000L;

    @Inject
    public TimeHelper() {

    }

    public long getCurrentTime() {
        return System.currentTimeMillis();
    }
}
