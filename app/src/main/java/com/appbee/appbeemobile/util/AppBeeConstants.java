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
        FINANCE("/store/apps/category/FINANCE", "금융", Arrays.asList(R.string.brain_flower_desc_finance, R.string.longest_used_app_desc_finance)),
        GAME("/store/apps/category/GAME", "게임", Arrays.asList(R.string.brain_flower_desc_game, R.string.longest_used_app_desc_game)),
        GAME_EDUCATIONAL("/store/apps/category/GAME_EDUCATIONAL", "게임", Arrays.asList(R.string.brain_flower_desc_game, R.string.longest_used_app_desc_game)),
        GAME_WORD("/store/apps/category/GAME_WORD", "게임", Arrays.asList(R.string.brain_flower_desc_game, R.string.longest_used_app_desc_game)),
        GAME_ROLE_PLAYING("/store/apps/category/GAME_ROLE_PLAYING", "게임", Arrays.asList(R.string.brain_flower_desc_game, R.string.longest_used_app_desc_game)),
        GAME_BOARD("/store/apps/category/GAME_BOARD", "게임", Arrays.asList(R.string.brain_flower_desc_game, R.string.longest_used_app_desc_game)),
        GAME_SPORTS("/store/apps/category/GAME_SPORTS", "게임", Arrays.asList(R.string.brain_flower_desc_game, R.string.longest_used_app_desc_game)),
        GAME_SIMULATION("/store/apps/category/GAME_SIMULATION", "게임", Arrays.asList(R.string.brain_flower_desc_game, R.string.longest_used_app_desc_game)),
        GAME_ARCADE("/store/apps/category/GAME_ARCADE", "게임", Arrays.asList(R.string.brain_flower_desc_game, R.string.longest_used_app_desc_game)),
        GAME_ACTION("/store/apps/category/GAME_ACTION", "게임", Arrays.asList(R.string.brain_flower_desc_game, R.string.longest_used_app_desc_game)),
        GAME_ADVENTURE("/store/apps/category/GAME_ADVENTURE", "게임", Arrays.asList(R.string.brain_flower_desc_game, R.string.longest_used_app_desc_game)),
        GAME_MUSIC("/store/apps/category/GAME_MUSIC", "게임", Arrays.asList(R.string.brain_flower_desc_game, R.string.longest_used_app_desc_game)),
        GAME_RACING("/store/apps/category/GAME_RACING", "게임", Arrays.asList(R.string.brain_flower_desc_game, R.string.longest_used_app_desc_game)),
        GAME_STRATEGY("/store/apps/category/GAME_STRATEGY", "게임", Arrays.asList(R.string.brain_flower_desc_game, R.string.longest_used_app_desc_game)),
        GAME_CARD("/store/apps/category/GAME_CARD", "게임", Arrays.asList(R.string.brain_flower_desc_game, R.string.longest_used_app_desc_game)),
        GAME_CASINO("/store/apps/category/GAME_CASINO", "게임", Arrays.asList(R.string.brain_flower_desc_game, R.string.longest_used_app_desc_game)),
        GAME_CASUAL("/store/apps/category/GAME_CASUAL", "게임", Arrays.asList(R.string.brain_flower_desc_game, R.string.longest_used_app_desc_game)),
        GAME_TRIVIA("/store/apps/category/GAME_TRIVIA", "게임", Arrays.asList(R.string.brain_flower_desc_game, R.string.longest_used_app_desc_game)),
        GAME_PUZZLE("/store/apps/category/GAME_PUZZLE", "게임", Arrays.asList(R.string.brain_flower_desc_game, R.string.longest_used_app_desc_game)),
        MUSIC_AND_AUDIO("/store/apps/category/MUSIC_AND_AUDIO", "음악/비디오", Arrays.asList(R.string.brain_flower_desc_music_video, R.string.longest_used_app_desc_music_video)),
        VIDEO_PLAYERS("/store/apps/category/VIDEO_PLAYERS", "음악/비디오", Arrays.asList(R.string.brain_flower_desc_music_video, R.string.longest_used_app_desc_music_video)),
        SOCIAL("/store/apps/category/SOCIAL", "소셜", Arrays.asList(R.string.brain_flower_desc_social, R.string.longest_used_app_desc_social)),
        PHOTOGRAPHY("/store/apps/category/PHOTOGRAPHY", "사진", Arrays.asList(R.string.brain_flower_desc_photography, R.string.longest_used_app_desc_photography)),
        PERSONALIZATION("/store/apps/category/PERSONALIZATION", "개인화(=맞춤설정)", Arrays.asList(R.string.brain_flower_desc_personalization, R.string.longest_used_app_desc_personalization)),
        SHOPPING("/store/apps/category/SHOPPING", "쇼핑", Arrays.asList(R.string.brain_flower_desc_shopping, R.string.longest_used_app_desc_shopping)),
        COMMUNICATION("/store/apps/category/COMMUNICATION", "커뮤니케이션", Arrays.asList(R.string.brain_flower_desc_communication, R.string.longest_used_app_desc_communication)),
        ENTERTAINMENT("/store/apps/category/ENTERTAINMENT", "엔터테인먼트", Arrays.asList(R.string.brain_flower_desc_entertainment, R.string.longest_used_app_desc_entertainment)),
        HEALTH_AND_FITNESS("/store/apps/category/HEALTH_AND_FITNESS", "건강", Arrays.asList(R.string.brain_flower_desc_health_sports, R.string.longest_used_app_desc_health_sports)),
        SPORTS("/store/apps/category/SPORTS", "건강", Arrays.asList(R.string.brain_flower_desc_health_sports, R.string.longest_used_app_desc_health_sports)),
        EDUCATION("/store/apps/category/EDUCATION", "교육", Arrays.asList(R.string.brain_flower_desc_education, R.string.longest_used_app_desc_education)),
        WEATHER("/store/apps/category/WEATHER", "날씨", Arrays.asList(R.string.brain_flower_desc_weather, R.string.longest_used_app_desc_weather)),
        TRAVEL_AND_LOCAL("/store/apps/category/TRAVEL_AND_LOCAL", "여행", Arrays.asList(R.string.brain_flower_desc_travel, R.string.longest_used_app_desc_travel)),
        BUSINESS("/store/apps/category/BUSINESS", "비즈니스/생산성", Arrays.asList(R.string.brain_flower_desc_business_producitivity, R.string.longest_used_app_desc_business_producitivity)),
        PRODUCTIVITY("/store/apps/category/PRODUCTIVITY", "비즈니스/생산성", Arrays.asList(R.string.brain_flower_desc_business_producitivity, R.string.longest_used_app_desc_business_producitivity)),
        TOOLS("/store/apps/category/TOOLS", "도구", Arrays.asList(R.string.brain_flower_desc_tools, R.string.longest_used_app_desc_tools)),
        BOOKS_AND_REFERENCE("/store/apps/category/BOOKS_AND_REFERENCE", "도서/뉴스/잡지", Arrays.asList(R.string.brain_flower_desc_book_news, R.string.longest_used_app_desc_book_news)),
        NEWS_AND_MAGAZINES("/store/apps/category/NEWS_AND_MAGAZINES", "도서/뉴스/잡지", Arrays.asList(R.string.brain_flower_desc_book_news, R.string.longest_used_app_desc_book_news)),
        LIBRARIES_AND_DEMO("/store/apps/category/LIBRARIES_AND_DEMO", "데모", Arrays.asList(R.string.brain_flower_desc_library, R.string.longest_used_app_desc_library)),
        LIFESTYLE("/store/apps/category/LIFESTYLE", "라이프스타일", Arrays.asList(R.string.brain_flower_desc_lifestyle, R.string.longest_used_app_desc_lifestyle)),
        COMICS("/store/apps/category/COMICS", "만화", Arrays.asList(R.string.brain_flower_desc_comics, R.string.longest_used_app_desc_comics)),
        HOUSE("/store/apps/category/HOUSE", "부동산/인테리어", Arrays.asList(R.string.brain_flower_desc_house, R.string.longest_used_app_desc_house)),
        BEAUTY_DESIGN("/store/apps/category/BEAUTY_DESIGN", "뷰티/예술/디자인", Arrays.asList(R.string.brain_flower_desc_beauty_design, R.string.longest_used_app_desc_beauty_design)),
        DATING("/store/apps/category/DATING", "데이트", Arrays.asList(R.string.brain_flower_desc_dating, R.string.longest_used_app_desc_dating)),
        FAMILY("/store/apps/category/FAMILY", "키즈", Arrays.asList(R.string.brain_flower_desc_kids, R.string.longest_used_app_desc_kids)),
        FAMILY_EDUCATION("/store/apps/category/FAMILY_EDUCATION", "키즈", Arrays.asList(R.string.brain_flower_desc_kids, R.string.longest_used_app_desc_kids)),
        FAMILY_BRAINGAMES("/store/apps/category/FAMILY_BRAINGAMES", "키즈", Arrays.asList(R.string.brain_flower_desc_kids, R.string.longest_used_app_desc_kids)),
        FAMILY_ACTION("/store/apps/category/FAMILY_ACTION", "키즈", Arrays.asList(R.string.brain_flower_desc_kids, R.string.longest_used_app_desc_kids)),
        FAMILY_PRETEND("/store/apps/category/FAMILY_PRETEND", "키즈", Arrays.asList(R.string.brain_flower_desc_kids, R.string.longest_used_app_desc_kids)),
        FAMILY_MUSICVIDEO("/store/apps/category/FAMILY_MUSICVIDEO", "키즈", Arrays.asList(R.string.brain_flower_desc_kids, R.string.longest_used_app_desc_kids)),
        FAMILY_CREATE("/store/apps/category/FAMILY_CREATE", "키즈", Arrays.asList(R.string.brain_flower_desc_kids, R.string.longest_used_app_desc_kids));
        private static final Map<String, Category> map = new HashMap<>();

        static {
            for (Category category : values()) {
                map.put(category.categoryId, category);
            }
        }

        public static Category fromId(String categoryId) {
            return map.get(categoryId);
        }

        public final String categoryId;
        public final String categoryName;
        @StringRes
        public final int description;
        @StringRes
        public final int appDescription;

        Category(String categoryId, String categoryName, List<Integer> descriptionList) {
            this.categoryId = categoryId;
            this.categoryName = categoryName;
            this.description = descriptionList.get(0);
            this.appDescription = descriptionList.get(1);
        }


        public String toString() {
            return this.categoryId;
        }
    }
}