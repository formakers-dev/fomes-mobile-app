package com.appbee.appbeemobile.util;

public interface AppBeeConstants {
    interface API_RESPONSE_CODE {
        String UNAUTHORIZED = "401";
        String FORBIDDEN = "403";
    }

    interface CHARACTER_TYPE {
        int GAMER = 0;
        int QUEEN = 1;
        int POISON = 2;
        int SOUL = 3;
        int ETC = 4;
    }

    interface APP_USAGE_TIME_TYPE {
        int LEAST = 0;
        int LESS = 1;
        int NORMAL = 2;
        int MORE = 3;
        int MOST = 4;
    }

    interface APP_LIST_COUNT_TYPE {
        int LEAST = 0;
        int LESS = 1;
        int NORMAL = 2;
        int MORE = 3;
        int MOST = 4;
    }
}