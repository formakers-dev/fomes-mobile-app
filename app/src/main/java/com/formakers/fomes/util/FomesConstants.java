package com.formakers.fomes.util;

public interface FomesConstants {
    interface EXTRA {
        String START_FRAGMENT_NAME = "EXTRA_FRAGEMENT_NAME";

        String PROJECT_ID = "EXTRA_PROJECT_ID";
        String INTERVIEW_SEQ = "EXTRA_INTERVIEW_SEQ";
        String TIME_SLOT = "EXTRA_TIME_SLOT";
        String LOCATION = "EXTRA_LOCATION";
        String INTERVIEW_DATE = "EXTRA_INTERVIEW_DATE";
        String PROJECT_NAME = "EXTRA_PROJECT_NAME";
        String INTERVIEW_STATUS = "EXTRA_INTERVIEW_STATUS";
        String NOTIFICATION_TYPE = "EXTRA_NOTIFICATION_TYPE";
    }

    interface HTTP_STATUS {
        int CODE_405_METHOD_NOT_ALLOWED = 405;
        int CODE_409_CONFLICT = 409;
        int CODE_412_PRECONDITION_FAILED = 412;
    }

    interface PROVISIONING {
        interface PROGRESS_STATUS {
            int NOT_LOGIN = 0;
            int INTRO = 1;
            int NO_PERMISSION = 2;
            int COMPLETED = 9999;
        }
    }
}