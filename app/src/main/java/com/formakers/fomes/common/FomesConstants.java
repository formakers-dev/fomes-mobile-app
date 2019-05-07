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
        interface Log {
            String TARGET_EVENT_BANNER = "EventBanner";
        }
    }

    interface BetaTest {
        String EXTRA_BETA_TEST = "EXTRA_BETA_TEST";
        String EXTRA_USER_EMAIL = "EXTRA_USER_EMAIL";

        // TODO : Log? Analytic? Tracking? 네이밍 고민
        interface Log {
            String TARGET_ITEM = "BetaTest_Item";
            String TARGET_DETAIL_DIALOG_JOIN_BUTTON = "BetaTest_DetailDialog_JoinButton";
            String TARGET_EPILOGUE_BUTTON = "BetaTest_Epilogue_Button";
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

        // for Extras
        String DESTINATION_ACTIVITY = "DESTINATION_ACTIVITY_CLASS_NAME";

        // TODO : Log? Analytic? Tracking? 네이밍 고민
        interface Log {
            String ACTION_RECEIVE = "receive";
            String ACTION_OPEN = "open";
            String ACTION_DISMISS = "dismiss";

            String TARGET = "notification";
        }
    }

    // TODO : 리팩토링 필요. 실시간으로 봐야하는 로그만 포메스 이벤트로그로 관리하자
    interface FomesEventLog {
        // TODO : SCREEN(해당 이벤트가 일어나는 화면), EVENT(ACTION, 보통 이게 CODE로 불림.. ex. click, touch, remove 등등), TARGET (EVENT의 대상), DETAIL_INFOS 와 같은 구조로 나눠야 함
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

    interface Broadcast {
        String ACTION_NOTI_CANCELLED = "com.formakers.fomes.NOTI_CANCELLED";
        String ACTION_NOTI_CLICKED = "com.formakers.fomes.NOTI_CLICKED";
    }
}