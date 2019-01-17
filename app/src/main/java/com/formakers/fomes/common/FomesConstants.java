package com.formakers.fomes.common;

public interface FomesConstants {
    interface EXTRA {
        String START_FRAGMENT_NAME = "EXTRA_FRAGEMENT_NAME";
        String APPINFO="EXTRA_APPINFO";
        String PACKAGE_NAME="EXTRA_PACKAGE_NAME";
        String RECOMMEND_TYPE = "EXTRA_RECOMMEND_TYPE";
        String RECOMMEND_CRITERIA = "EXTRA_RECOMMEND_CRITERIA";
        String UNWISHED_APPS = "EXTRA_UNWISHED_APPS";
        String PACKAGE_NAMES = "EXTRA_PACKAGE_NAMES";
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
        String TOPIC_NOTICE_ALL = "notice-all";

        // Request Body Key
        String CHANNEL = "channel";
        String TITLE = "title";
        String MESSAGE = "message";
        String IS_SUMMARY = "isSummary";
        String SUMMARY_SUB_TEXT = "summarySubText";
        String SUB_TITLE = "subTitle";
    }
}