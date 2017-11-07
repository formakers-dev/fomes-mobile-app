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
}
