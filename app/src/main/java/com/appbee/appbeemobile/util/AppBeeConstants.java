package com.appbee.appbeemobile.util;

public interface AppBeeConstants {
    interface EXTRA {
        String PROJECT_ID = "EXTRA_PROJECT_ID";
        String INTERVIEW_SEQ = "EXTRA_INTERVIEW_SEQ";
        String TIME_SLOT = "EXTRA_TIME_SLOT";
        String LOCATION = "EXTRA_LOCATION";
        String INTERVIEW_DATE = "EXTRA_INTERVIEW_DATE";
        String PROJECT_NAME = "EXTRA_PROJECT_NAME";
        String INTERVIEW_STATUS = "EXTRA_INTERVIEW_STATUS";
    }

    interface HTTP_STATUS {
        int CODE_409_CONFLICT = 409;
        int CODE_412_PRECONDITION_FAILED = 412;
    }
}