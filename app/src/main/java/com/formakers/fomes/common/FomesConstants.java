package com.formakers.fomes.common;

public interface FomesConstants {
    int MIGRATION_VERSION = 1;

    interface EXTRA {
        String START_FRAGMENT_NAME = "EXTRA_FRAGEMENT_NAME";
        String PACKAGE_NAME="EXTRA_PACKAGE_NAME";
        String RECOMMEND_TYPE = "EXTRA_RECOMMEND_TYPE";
        String RECOMMEND_CRITERIA = "EXTRA_RECOMMEND_CRITERIA";
        String UNWISHED_APPS = "EXTRA_UNWISHED_APPS";
        String IS_FROM_NOTIFICATION = "EXTRA_IS_FROM_NOTIFICATION";
    }

    interface PROVISIONING {
        interface PROGRESS_STATUS {
            int COMPLETED = -1;
            int LOGIN = 0;
            int INTRO = 1;
            int PERMISSION = 2;
        }
    }

    interface Main {
        interface Logging {
            String TARGET_EVENT_BANNER = "EventBanner";
        }
    }

    interface BetaTest {
        String EXTRA_BETA_TEST = "EXTRA_BETA_TEST";
        String EXTRA_USER_EMAIL = "EXTRA_USER_EMAIL";

        interface Logging {
            String TARGET_BETA_TEST_ITEM = "BetaTestItem";
            String TARGET_BETA_TEST_DIALOG = "BetaTestDialog";
            String TARGET_BETA_TEST_DIALOG_BUTTON = "BetaTestDialogButton";
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
        String DEEPLINK = "deeplink";
    }

    interface EventLog {
        interface Code {
            String MAIN_ACTIVITY_ENTER = "MAIN_ENT";
            String MAIN_ACTIVITY_TAP_BETA_TEST = "MAIN_TAP_BETA";
            String MAIN_ACTIVITY_TAP_RECOMMEND = "MAIN_TAP_RCMD";
            String BETA_TEST_FRAGMENT_TAP_ITEM = "BETA_TAP_ITEM";
            String BETA_TEST_DETAIL_DIALOG_TAP_CONFIRM = "BETA_TAP_RGST";
            String NOTIFICATION_RECEIVED = "NOTI_RCV";
            String NOTIFICATION_TAP = "NOTI_TAP";
        }
    }
}