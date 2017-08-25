package com.appbee.appbeemobile.util;

import android.support.annotation.StringRes;

import com.appbee.appbeemobile.R;

import java.util.HashMap;
import java.util.Map;

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

    interface CATEGORY_GROUP {
        int FINANCE = 0;
        int GAME = 1;
        int MUSIC_VIDEO = 2;
        int SOCIAL = 3;
        int PHOTOGRAPHY = 4;
        int PERSONALIZATION = 5;
        int SHOPPING = 6;
        int COMMUNICATION = 7;
        int ENTERTAINMENT = 8;
        int HEALTH_SPORTS = 9;
        int EDUCATION = 10;
        int WEATHER = 11;
        int TRAVEL = 12;
        int BUSINESS_PRODUCTIVITY = 13;
        int TOOLS = 14;
        int BOOK_NEWS = 15;
        int LIBRARY = 16;
        int LIFESTYLE = 17;
        int COMICS = 18;
        int HOUSE = 19;
        int BEAUTY_DESIGN = 20;
        int DATING = 21;
    }

    enum Category {
        FINANCE("/store/apps/category/FINANCE", R.string.brain_flower_desc_finance),
        GAME("/store/apps/category/GAME", R.string.brain_flower_desc_game),
        GAME_EDUCATIONAL("/store/apps/category/GAME_EDUCATIONAL", R.string.brain_flower_desc_game),
        GAME_WORD("/store/apps/category/GAME_WORD", R.string.brain_flower_desc_game),
        GAME_ROLE_PLAYING("/store/apps/category/GAME_ROLE_PLAYING", R.string.brain_flower_desc_game),
        GAME_BOARD("/store/apps/category/GAME_BOARD", R.string.brain_flower_desc_game),
        GAME_SPORTS("/store/apps/category/GAME_SPORTS", R.string.brain_flower_desc_game),
        GAME_SIMULATION("/store/apps/category/GAME_SIMULATION", R.string.brain_flower_desc_game),
        GAME_ARCADE("/store/apps/category/GAME_ARCADE", R.string.brain_flower_desc_game),
        GAME_ACTION("/store/apps/category/GAME_ACTION", R.string.brain_flower_desc_game),
        GAME_ADVENTURE("/store/apps/category/GAME_ADVENTURE", R.string.brain_flower_desc_game),
        GAME_MUSIC("/store/apps/category/GAME_MUSIC", R.string.brain_flower_desc_game),
        GAME_RACING("/store/apps/category/GAME_RACING", R.string.brain_flower_desc_game),
        GAME_STRATEGY("/store/apps/category/GAME_STRATEGY", R.string.brain_flower_desc_game),
        GAME_CARD("/store/apps/category/GAME_CARD", R.string.brain_flower_desc_game),
        GAME_CASINO("/store/apps/category/GAME_CASINO", R.string.brain_flower_desc_game),
        GAME_CASUAL("/store/apps/category/GAME_CASUAL", R.string.brain_flower_desc_game),
        GAME_TRIVIA("/store/apps/category/GAME_TRIVIA", R.string.brain_flower_desc_game),
        GAME_PUZZLE("/store/apps/category/GAME_PUZZLE", R.string.brain_flower_desc_game),

        MUSIC_AND_AUDIO("/store/apps/category/MUSIC_AND_AUDIO", R.string.brain_flower_desc_music_video),
        VIDEO_PLAYERS("/store/apps/category/VIDEO_PLAYERS", R.string.brain_flower_desc_music_video),
        SOCIAL("/store/apps/category/SOCIAL", R.string.brain_flower_desc_social),
        PHOTOGRAPHY("/store/apps/category/PHOTOGRAPHY", R.string.brain_flower_desc_photography),
        PERSONALIZATION("/store/apps/category/PERSONALIZATION", R.string.brain_flower_desc_personalization),
        SHOPPING("/store/apps/category/SHOPPING", R.string.brain_flower_desc_shopping),
        COMMUNICATION("/store/apps/category/COMMUNICATION", R.string.brain_flower_desc_communication),
        ENTERTAINMENT("/store/apps/category/ENTERTAINMENT", R.string.brain_flower_desc_entertainment),
        HEALTH_AND_FITNESS("/store/apps/category/HEALTH_AND_FITNESS", R.string.brain_flower_desc_health_sports),
        SPORTS("/store/apps/category/SPORTS", R.string.brain_flower_desc_health_sports),
        EDUCATION("/store/apps/category/EDUCATION", R.string.brain_flower_desc_education),
        WEATHER("/store/apps/category/WEATHER", R.string.brain_flower_desc_weather),
        TRAVEL_AND_LOCAL("/store/apps/category/TRAVEL_AND_LOCAL", R.string.brain_flower_desc_travel),
        BUSINESS("/store/apps/category/BUSINESS", R.string.brain_flower_desc_business_producitivity),
        PRODUCTIVITY("/store/apps/category/PRODUCTIVITY", R.string.brain_flower_desc_business_producitivity),
        TOOLS("/store/apps/category/TOOLS", R.string.brain_flower_desc_tools),
        BOOKS_AND_REFERENCE("/store/apps/category/BOOKS_AND_REFERENCE", R.string.brain_flower_desc_book_news),
        NEWS_AND_MAGAZINES("/store/apps/category/NEWS_AND_MAGAZINES", R.string.brain_flower_desc_book_news),
        LIBRARIES_AND_DEMO("/store/apps/category/LIBRARIES_AND_DEMO", R.string.brain_flower_desc_library),
        LIFESTYLE("/store/apps/category/LIFESTYLE", R.string.brain_flower_desc_lifestyle),
        COMICS("/store/apps/category/COMICS", R.string.brain_flower_desc_comics),
        HOUSE("/store/apps/category/HOUSE", R.string.brain_flower_desc_house),
        BEAUTY_DESIGN("/store/apps/category/BEAUTY_DESIGN", R.string.brain_flower_desc_beauty_design),
        DATING("/store/apps/category/DATING", R.string.brain_flower_desc_dating),
        FAMILY("/store/apps/category/FAMILY", R.string.brain_flower_desc_kids),
        FAMILY_EDUCATION("/store/apps/category/FAMILY_EDUCATION", R.string.brain_flower_desc_kids),
        FAMILY_BRAINGAMES("/store/apps/category/FAMILY_BRAINGAMES", R.string.brain_flower_desc_kids),
        FAMILY_ACTION("/store/apps/category/FAMILY_ACTION", R.string.brain_flower_desc_kids),
        FAMILY_PRETEND("/store/apps/category/FAMILY_PRETEND", R.string.brain_flower_desc_kids),
        FAMILY_MUSICVIDEO("/store/apps/category/FAMILY_MUSICVIDEO", R.string.brain_flower_desc_kids),
        FAMILY_CREATE("/store/apps/category/FAMILY_CREATE", R.string.brain_flower_desc_kids);
        private static final Map<String, Category> map = new HashMap<>();

        static {
            for (Category category : values()) {
                map.put(category.categoryId, category);
            }
        }

        public static Category fromId(String categoryId) {
            return map.get(categoryId);
        }

        String categoryId;
        @StringRes
        int detailString;

        Category(String categoryId, @StringRes int detailString) {
            this.categoryId = categoryId;
            this.detailString = detailString;
        }

        public
        @StringRes
        int getDetailString() {
            return detailString;
        }

        public String toString() {
            return this.categoryId;
        }
    }
}