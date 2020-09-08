package com.formakers.fomes.common.constant;

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
            String TARGET_EVENT_BANNER = "Main_EventBanner";
        }
    }

    interface More {
        int MENU_HOW_TO_PC = 1;
        int MENU_PROFILE = 2;
        int MENU_GAME_ANALYSIS = 3;
        int MENU_WISH_LIST = 4;
        int MENU_SETTINGS = 5;
        int MENU_ADVERTISING = 6;
    }

    interface BetaTest {
        String EXTRA_BETA_TEST = "EXTRA_BETA_TEST";
        String EXTRA_ID = "EXTRA_ID";
        String EXTRA_REMAIN_DAYS = "EXTRA_REMAIN_DAYS";
        String EXTRA_COVER_IMAGE_URL = "EXTRA_COVER_IMAGE_URL";
        String EXTRA_TITLE = "EXTRA_TITLE";
        String EXTRA_SUBTITLE = "EXTRA_SUBTITLE";
        String EXTRA_PLAN = "EXTRA_PLAN";
        String EXTRA_REWARD_ITEMS = "EXTRA_REWARD_ITEMS";
        String EXTRA_IS_COMPLETED = "EXTRA_IS_COMPLETED";
        String EXTRA_IS_PREMIUM_PLAN = "EXTRA_IS_PREMIUM_PLAN";
        String EXTRA_TAGS_STRING = "EXTRA_TAGS_STRING";
        String EXTRA_TAG_LIST = "EXTRA_TAG_LIST";

        // TODO : Log? Analytic? Tracking? 네이밍 고민
        interface Log {
            String TARGET_ITEM = "BetaTest_Item";
            String TARGET_DETAIL_DIALOG_JOIN_BUTTON = "BetaTest_DetailDialog_JoinButton";
            String TARGET_EPILOGUE_BUTTON = "BetaTest_Epilogue_Button";
        }

        interface Plan {
            String STANDARD = "standard";
            String SIMPLE = "simple";
        }

        interface Mission {
            String TYPE_INSTALL = "install";
            String TYPE_PLAY = "play";
            String TYPE_DEFAULT = "default";

            String ACTION_TYPE_INTERNAL_WEB = "internal_web";
        }

        interface Reward {
            String PAYMENT_TYPE_POINT = "point";
            String PAYMENT_TYPE_GAME_ITEM = "game-item";
            String PAYMENT_TYPE_ETC = "etc";
        }

        interface Award {
            int TYPE_CODE_BEST = 9000;
            int TYPE_CODE_GOOD = 7000;
            int TYPE_CODE_NORMAL_BONUS = 5001;
            int TYPE_CODE_NORMAL = 5000;
            int TYPE_CODE_PARTICIPATED = 3000;
            int TYPE_CODE_ETC = 1000;
        }
    }

    interface Settings {
        interface Menu {
            int VERSION = 1;
            int TNC_USAGE = 2;
            int TNC_PRIVATE = 3;
            int CONTACTS_US = 4;
            int NOTIFICATION_PUBLIC = 5;
        }

        // TODO : Log? Analytic? Tracking? 네이밍 고민
        interface Log {
            String TARGET = "configuration";

            long VALUE_UNCHECKED = 0L;
            long VALUE_CHECKED = 1L;
        }
    }

    interface WebView {
        String EXTRA_TITLE = "EXTRA_TITLE";
        String EXTRA_CONTENTS = "EXTRA_CONTENTS";
        String EXTRA_IS_PREVENT_BACK_PRESSED = "EXTRA_IS_PREVENT_BACK_PRESSED";
        String EXTRA_MISSION_ID = "EXTRA_MISSION_ID";
    }

    interface Notification {
        String TOPIC_NOTICE_ALL = "notice-all";

        // Request Body Key
        String TYPE = "type";
        String CHANNEL = "channel";
        String TITLE = "title";
        String MESSAGE = "message";
        String IS_SUMMARY = "isSummary";
        String SUMMARY_SUB_TEXT = "summarySubText";
        String SUB_TITLE = "subTitle";
        String DEEPLINK = "deeplink";

        // Values
        String TYPE_SIGNAL = "signal";
        String SIGNAL_REGISTER_SEND_DATA_JOB = "register_send_data_job";

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
            String MAIN_ACTIVITY_TAP_FINISHED_BETA_TEST = "MAIN_TAP_FINISHED_BETA";
            String MAIN_ACTIVITY_TAP_RECOMMEND = "MAIN_TAP_RCMD";
            String BETA_TEST_FRAGMENT_TAP_ITEM = "BETA_TAP_ITEM";
            String BETA_TEST_FRAGMENT_TAP_BUG_REPORT = "BETA_TAP_BUG_REPORT";
            String BETA_TEST_DETAIL_ENTER = "BETA_TEST_DETAIL_ENTER";
            String BETA_TEST_DETAIL_TAP_LOCK = "BETA_TEST_DETAIL_TAP_LOCK";
            String BETA_TEST_DETAIL_TAP_MISSION_ITEM = "BETA_TEST_DETAIL_TAP_MISSION_ITEM";
            String BETA_TEST_DETAIL_TAP_MISSION_REFRESH = "BETA_TEST_DETAIL_TAP_MISSION_REFRESH";
            String FINISHED_BETA_TEST_TAP_EPILOGUE = "FINISHED_BETA_TEST_TAP_EPILOGUE";
            String NOTIFICATION_RECEIVED = "NOTI_RCV";
            String NOTIFICATION_TAP = "NOTI_TAP";
        }
    }

    interface Broadcast {
        String ACTION_NOTI_CANCELLED = "com.formakers.fomes.NOTI_CANCELLED";
        String ACTION_NOTI_CLICKED = "com.formakers.fomes.NOTI_CLICKED";
    }

    interface RemoteConfig {
        String SIGNUP_ALALYSIS_SCREEN_IS_VISIBLE ="signup_analysis_screen_is_visible";
        String FEATURE_CALCULATE_PLAYTIME = "feature_calculate_playtime";
        String FEATURE_ADVERTISING = "feature_advertising";

        String MIGRATION_NOTICE ="migration_notice";
    }

    interface DeepLink {
        String SLASH = "/";
        String SCHEME = "fomes";

        String HOST_POSTS = "posts";
        String HOST_WEB = "web";

        String PATH_EXTERNAL = SLASH + "external";
        String PATH_DETAIL = SLASH + "detail";

        String QUERY_PARAM_ID = "id";
        String QUERY_PARAM_TITLE = "title";
        String QUERY_PARAM_URL = "url";
    }
}