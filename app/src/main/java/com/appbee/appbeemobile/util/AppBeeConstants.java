package com.appbee.appbeemobile.util;

import android.support.annotation.StringRes;

import com.appbee.appbeemobile.R;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
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

    enum Category {
        FINANCE("/store/apps/category/FINANCE", Arrays.asList(R.string.brain_flower_desc_finance, R.string.longest_used_app_desc_finance)),
        GAME("/store/apps/category/GAME", Arrays.asList(R.string.brain_flower_desc_game, R.string.longest_used_app_desc_game)),
        GAME_EDUCATIONAL("/store/apps/category/GAME_EDUCATIONAL", Arrays.asList(R.string.brain_flower_desc_game, R.string.longest_used_app_desc_game)),
        GAME_WORD("/store/apps/category/GAME_WORD", Arrays.asList(R.string.brain_flower_desc_game, R.string.longest_used_app_desc_game)),
        GAME_ROLE_PLAYING("/store/apps/category/GAME_ROLE_PLAYING", Arrays.asList(R.string.brain_flower_desc_game, R.string.longest_used_app_desc_game)),
        GAME_BOARD("/store/apps/category/GAME_BOARD", Arrays.asList(R.string.brain_flower_desc_game, R.string.longest_used_app_desc_game)),
        GAME_SPORTS("/store/apps/category/GAME_SPORTS", Arrays.asList(R.string.brain_flower_desc_game, R.string.longest_used_app_desc_game)),
        GAME_SIMULATION("/store/apps/category/GAME_SIMULATION", Arrays.asList(R.string.brain_flower_desc_game, R.string.longest_used_app_desc_game)),
        GAME_ARCADE("/store/apps/category/GAME_ARCADE", Arrays.asList(R.string.brain_flower_desc_game, R.string.longest_used_app_desc_game)),
        GAME_ACTION("/store/apps/category/GAME_ACTION", Arrays.asList(R.string.brain_flower_desc_game, R.string.longest_used_app_desc_game)),
        GAME_ADVENTURE("/store/apps/category/GAME_ADVENTURE", Arrays.asList(R.string.brain_flower_desc_game, R.string.longest_used_app_desc_game)),
        GAME_MUSIC("/store/apps/category/GAME_MUSIC", Arrays.asList(R.string.brain_flower_desc_game, R.string.longest_used_app_desc_game)),
        GAME_RACING("/store/apps/category/GAME_RACING", Arrays.asList(R.string.brain_flower_desc_game, R.string.longest_used_app_desc_game)),
        GAME_STRATEGY("/store/apps/category/GAME_STRATEGY", Arrays.asList(R.string.brain_flower_desc_game, R.string.longest_used_app_desc_game)),
        GAME_CARD("/store/apps/category/GAME_CARD", Arrays.asList(R.string.brain_flower_desc_game, R.string.longest_used_app_desc_game)),
        GAME_CASINO("/store/apps/category/GAME_CASINO", Arrays.asList(R.string.brain_flower_desc_game, R.string.longest_used_app_desc_game)),
        GAME_CASUAL("/store/apps/category/GAME_CASUAL", Arrays.asList(R.string.brain_flower_desc_game, R.string.longest_used_app_desc_game)),
        GAME_TRIVIA("/store/apps/category/GAME_TRIVIA", Arrays.asList(R.string.brain_flower_desc_game, R.string.longest_used_app_desc_game)),
        GAME_PUZZLE("/store/apps/category/GAME_PUZZLE", Arrays.asList(R.string.brain_flower_desc_game, R.string.longest_used_app_desc_game)),
        MUSIC_AND_AUDIO("/store/apps/category/MUSIC_AND_AUDIO", Arrays.asList(R.string.brain_flower_desc_music_video, R.string.longest_used_app_desc_music_video)),
        VIDEO_PLAYERS("/store/apps/category/VIDEO_PLAYERS", Arrays.asList(R.string.brain_flower_desc_music_video, R.string.longest_used_app_desc_music_video)),
        SOCIAL("/store/apps/category/SOCIAL", Arrays.asList(R.string.brain_flower_desc_social, R.string.longest_used_app_desc_social)),
        PHOTOGRAPHY("/store/apps/category/PHOTOGRAPHY", Arrays.asList(R.string.brain_flower_desc_photography, R.string.longest_used_app_desc_photography)),
        PERSONALIZATION("/store/apps/category/PERSONALIZATION", Arrays.asList(R.string.brain_flower_desc_personalization, R.string.longest_used_app_desc_personalization)),
        SHOPPING("/store/apps/category/SHOPPING", Arrays.asList(R.string.brain_flower_desc_shopping, R.string.longest_used_app_desc_shopping)),
        COMMUNICATION("/store/apps/category/COMMUNICATION", Arrays.asList(R.string.brain_flower_desc_communication, R.string.longest_used_app_desc_communication)),
        ENTERTAINMENT("/store/apps/category/ENTERTAINMENT", Arrays.asList(R.string.brain_flower_desc_entertainment, R.string.longest_used_app_desc_entertainment)),
        HEALTH_AND_FITNESS("/store/apps/category/HEALTH_AND_FITNESS", Arrays.asList(R.string.brain_flower_desc_health_sports, R.string.longest_used_app_desc_health_sports)),
        SPORTS("/store/apps/category/SPORTS", Arrays.asList(R.string.brain_flower_desc_health_sports, R.string.longest_used_app_desc_health_sports)),
        EDUCATION("/store/apps/category/EDUCATION", Arrays.asList(R.string.brain_flower_desc_education, R.string.longest_used_app_desc_education)),
        WEATHER("/store/apps/category/WEATHER", Arrays.asList(R.string.brain_flower_desc_weather, R.string.longest_used_app_desc_weather)),
        TRAVEL_AND_LOCAL("/store/apps/category/TRAVEL_AND_LOCAL", Arrays.asList(R.string.brain_flower_desc_travel, R.string.longest_used_app_desc_travel)),
        BUSINESS("/store/apps/category/BUSINESS", Arrays.asList(R.string.brain_flower_desc_business_producitivity, R.string.longest_used_app_desc_business_producitivity)),
        PRODUCTIVITY("/store/apps/category/PRODUCTIVITY", Arrays.asList(R.string.brain_flower_desc_business_producitivity, R.string.longest_used_app_desc_business_producitivity)),
        TOOLS("/store/apps/category/TOOLS", Arrays.asList(R.string.brain_flower_desc_tools, R.string.longest_used_app_desc_tools)),
        BOOKS_AND_REFERENCE("/store/apps/category/BOOKS_AND_REFERENCE", Arrays.asList(R.string.brain_flower_desc_book_news, R.string.longest_used_app_desc_book_news)),
        NEWS_AND_MAGAZINES("/store/apps/category/NEWS_AND_MAGAZINES", Arrays.asList(R.string.brain_flower_desc_book_news, R.string.longest_used_app_desc_book_news)),
        LIBRARIES_AND_DEMO("/store/apps/category/LIBRARIES_AND_DEMO", Arrays.asList(R.string.brain_flower_desc_library, R.string.longest_used_app_desc_library)),
        LIFESTYLE("/store/apps/category/LIFESTYLE", Arrays.asList(R.string.brain_flower_desc_lifestyle, R.string.longest_used_app_desc_lifestyle)),
        COMICS("/store/apps/category/COMICS", Arrays.asList(R.string.brain_flower_desc_comics, R.string.longest_used_app_desc_comics)),
        HOUSE("/store/apps/category/HOUSE", Arrays.asList(R.string.brain_flower_desc_house, R.string.longest_used_app_desc_house)),
        BEAUTY_DESIGN("/store/apps/category/BEAUTY_DESIGN", Arrays.asList(R.string.brain_flower_desc_beauty_design, R.string.longest_used_app_desc_beauty_design)),
        DATING("/store/apps/category/DATING", Arrays.asList(R.string.brain_flower_desc_dating, R.string.longest_used_app_desc_dating)),
        FAMILY("/store/apps/category/FAMILY", Arrays.asList(R.string.brain_flower_desc_kids, R.string.longest_used_app_desc_kids)),
        FAMILY_EDUCATION("/store/apps/category/FAMILY_EDUCATION", Arrays.asList(R.string.brain_flower_desc_kids, R.string.longest_used_app_desc_kids)),
        FAMILY_BRAINGAMES("/store/apps/category/FAMILY_BRAINGAMES", Arrays.asList(R.string.brain_flower_desc_kids, R.string.longest_used_app_desc_kids)),
        FAMILY_ACTION("/store/apps/category/FAMILY_ACTION", Arrays.asList(R.string.brain_flower_desc_kids, R.string.longest_used_app_desc_kids)),
        FAMILY_PRETEND("/store/apps/category/FAMILY_PRETEND", Arrays.asList(R.string.brain_flower_desc_kids, R.string.longest_used_app_desc_kids)),
        FAMILY_MUSICVIDEO("/store/apps/category/FAMILY_MUSICVIDEO", Arrays.asList(R.string.brain_flower_desc_kids, R.string.longest_used_app_desc_kids)),
        FAMILY_CREATE("/store/apps/category/FAMILY_CREATE", Arrays.asList(R.string.brain_flower_desc_kids, R.string.longest_used_app_desc_kids));
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
        int mostUsedCategoryDescription;
        @StringRes
        int longestUsedAppDescription;

        Category(String categoryId, List<Integer> descriptionList) {
            this.categoryId = categoryId;
            this.mostUsedCategoryDescription = descriptionList.get(0);
            this.longestUsedAppDescription = descriptionList.get(1);
        }

        public
        @StringRes
        int getMostUsedCategoryDescription() {
            return mostUsedCategoryDescription;
        }

        public
        @StringRes
        int getLongestUsedAppDescription() {
            return longestUsedAppDescription;
        }


        public String toString() {
            return this.categoryId;
        }
    }
}