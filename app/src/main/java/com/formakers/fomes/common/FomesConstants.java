package com.formakers.fomes.common;

public interface FomesConstants {
    interface EXTRA {
        String START_FRAGMENT_NAME = "EXTRA_FRAGEMENT_NAME";
        String APPINFO="EXTRA_APPINFO";
        String PACKAGE_NAME="EXTRA_PACKAGE_NAME";
        String RECOMMEND_TYPE = "EXTRA_RECOMMEND_TYPE";
        String RECOMMEND_CRITERIA = "EXTRA_RECOMMEND_CRITERIA";

        String PROJECT_ID = "EXTRA_PROJECT_ID";
        String INTERVIEW_SEQ = "EXTRA_INTERVIEW_SEQ";
        String TIME_SLOT = "EXTRA_TIME_SLOT";
        String LOCATION = "EXTRA_LOCATION";
        String INTERVIEW_DATE = "EXTRA_INTERVIEW_DATE";
        String PROJECT_NAME = "EXTRA_PROJECT_NAME";
        String INTERVIEW_STATUS = "EXTRA_INTERVIEW_STATUS";
        String NOTIFICATION_TYPE = "EXTRA_NOTIFICATION_TYPE";
    }

    interface PROVISIONING {
        interface PROGRESS_STATUS {
            int COMPLETED = -1;
            int LOGIN = 0;
            int INTRO = 1;
            int PERMISSION = 2;
        }
    }

    interface Settings {
        interface Menu {
            int VERSION = 1;
            int TNC_USAGE = 2;
            int TNC_PRIVATE = 3;
            int CONTACTS_US = 4;
        }
    }

    interface Notification {
        String TITLE = "title";
        String MESSAGE = "message";
    }
}