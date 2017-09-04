package com.appbee.appbeemobile.util;

import android.support.annotation.DrawableRes;
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
        String NOTFOUND = "404";
    }

    enum CharacterType {
        GAMER(R.string.character_title_gamer, R.string.character_simple_desc_gamer, R.string.character_detail_desc_gamer, R.drawable.character_gamer),
        QUEEN(R.string.character_title_queen, R.string.character_simple_desc_queen, R.string.character_detail_desc_queen, R.drawable.character_queen),
        POISON(R.string.character_title_poison, R.string.character_simple_desc_poison, R.string.character_detail_desc_poison, R.drawable.character_poison),
        SOUL(R.string.character_title_soul, R.string.character_simple_desc_soul, R.string.character_detail_desc_soul, R.drawable.character_soul),
        ETC(R.string.character_title_alien, R.string.character_simple_desc_alien, R.string.character_detail_desc_alien, R.drawable.character_alien);

        @StringRes public final int title;
        @StringRes public final int simpleDescription;
        @StringRes public final int detailDescription;
        @DrawableRes public final int image;

        CharacterType(int title, int simpleDescription, int detailDescription, int image) {
            this.title = title;
            this.simpleDescription = simpleDescription;
            this.detailDescription = detailDescription;
            this.image = image;
        }
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
        MUSIC_AND_AUDIO("/store/apps/category/MUSIC_AND_AUDIO", "음악", Arrays.asList(R.string.brain_flower_desc_music, R.string.longest_used_app_desc_music_video)),
        VIDEO_PLAYERS("/store/apps/category/VIDEO_PLAYERS", "비디오", Arrays.asList(R.string.brain_flower_desc_video, R.string.longest_used_app_desc_music_video)),
        SOCIAL("/store/apps/category/SOCIAL", "소셜", Arrays.asList(R.string.brain_flower_desc_social, R.string.longest_used_app_desc_social)),
        PHOTOGRAPHY("/store/apps/category/PHOTOGRAPHY", "사진", Arrays.asList(R.string.brain_flower_desc_photography, R.string.longest_used_app_desc_photography)),
        PERSONALIZATION("/store/apps/category/PERSONALIZATION", "맞춤설정", Arrays.asList(R.string.brain_flower_desc_personalization, R.string.longest_used_app_desc_personalization)),
        SHOPPING("/store/apps/category/SHOPPING", "쇼핑", Arrays.asList(R.string.brain_flower_desc_shopping, R.string.longest_used_app_desc_shopping)),
        COMMUNICATION("/store/apps/category/COMMUNICATION", "커뮤니케이션", Arrays.asList(R.string.brain_flower_desc_communication, R.string.longest_used_app_desc_communication)),
        ENTERTAINMENT("/store/apps/category/ENTERTAINMENT", "엔터테인먼트", Arrays.asList(R.string.brain_flower_desc_entertainment, R.string.longest_used_app_desc_entertainment)),
        HEALTH_AND_FITNESS("/store/apps/category/HEALTH_AND_FITNESS", "건강/운동", Arrays.asList(R.string.brain_flower_desc_health_sports, R.string.longest_used_app_desc_health_sports)),
        SPORTS("/store/apps/category/SPORTS", "스포츠", Arrays.asList(R.string.brain_flower_desc_health_sports, R.string.longest_used_app_desc_health_sports)),
        EDUCATION("/store/apps/category/EDUCATION", "교육", Arrays.asList(R.string.brain_flower_desc_education, R.string.longest_used_app_desc_education)),
        WEATHER("/store/apps/category/WEATHER", "날씨", Arrays.asList(R.string.brain_flower_desc_weather, R.string.longest_used_app_desc_weather)),
        TRAVEL_AND_LOCAL("/store/apps/category/TRAVEL_AND_LOCAL", "여행", Arrays.asList(R.string.brain_flower_desc_travel, R.string.longest_used_app_desc_travel)),
        BUSINESS("/store/apps/category/BUSINESS", "비즈니스", Arrays.asList(R.string.brain_flower_desc_business_producitivity, R.string.longest_used_app_desc_business_productivity)),
        PRODUCTIVITY("/store/apps/category/PRODUCTIVITY", "생산성", Arrays.asList(R.string.brain_flower_desc_business_producitivity, R.string.longest_used_app_desc_business_productivity)),
        TOOLS("/store/apps/category/TOOLS", "도구", Arrays.asList(R.string.brain_flower_desc_tools, R.string.longest_used_app_desc_tools)),
        BOOKS_AND_REFERENCE("/store/apps/category/BOOKS_AND_REFERENCE", "도서", Arrays.asList(R.string.brain_flower_desc_book_news, R.string.longest_used_app_desc_book_news)),
        NEWS_AND_MAGAZINES("/store/apps/category/NEWS_AND_MAGAZINES", "뉴스/잡지", Arrays.asList(R.string.brain_flower_desc_book_news, R.string.longest_used_app_desc_book_news)),
        LIBRARIES_AND_DEMO("/store/apps/category/LIBRARIES_AND_DEMO", "데모", Arrays.asList(R.string.brain_flower_desc_library, R.string.longest_used_app_desc_library)),
        LIFESTYLE("/store/apps/category/LIFESTYLE", "라이프스타일", Arrays.asList(R.string.brain_flower_desc_lifestyle, R.string.longest_used_app_desc_lifestyle)),
        COMICS("/store/apps/category/COMICS", "만화", Arrays.asList(R.string.brain_flower_desc_comics, R.string.longest_used_app_desc_comics)),
        HOUSE_AND_HOME("/store/apps/category/HOUSE_AND_HOME", "부동산/인테리어", Arrays.asList(R.string.brain_flower_desc_house, R.string.longest_used_app_desc_house)),
        BEAUTY("/store/apps/category/BEAUTY", "뷰티", Arrays.asList(R.string.brain_flower_desc_beauty_design, R.string.longest_used_app_desc_beauty_design)),
        ART_AND_DESIGN("/store/apps/category/ART_AND_DESIGN", "예술/디자인", Arrays.asList(R.string.brain_flower_desc_beauty_design, R.string.longest_used_app_desc_beauty_design)),
        DATING("/store/apps/category/DATING", "데이트", Arrays.asList(R.string.brain_flower_desc_dating, R.string.longest_used_app_desc_dating)),
        FAMILY("/store/apps/category/FAMILY", "키즈", Arrays.asList(R.string.brain_flower_desc_kids, R.string.longest_used_app_desc_kids)),
        FAMILY_EDUCATION("/store/apps/category/FAMILY_EDUCATION", "키즈", Arrays.asList(R.string.brain_flower_desc_kids, R.string.longest_used_app_desc_kids)),
        FAMILY_BRAINGAMES("/store/apps/category/FAMILY_BRAINGAMES", "키즈", Arrays.asList(R.string.brain_flower_desc_kids, R.string.longest_used_app_desc_kids)),
        FAMILY_ACTION("/store/apps/category/FAMILY_ACTION", "키즈", Arrays.asList(R.string.brain_flower_desc_kids, R.string.longest_used_app_desc_kids)),
        FAMILY_PRETEND("/store/apps/category/FAMILY_PRETEND", "키즈", Arrays.asList(R.string.brain_flower_desc_kids, R.string.longest_used_app_desc_kids)),
        FAMILY_MUSICVIDEO("/store/apps/category/FAMILY_MUSICVIDEO", "키즈", Arrays.asList(R.string.brain_flower_desc_kids, R.string.longest_used_app_desc_kids)),
        FAMILY_CREATE("/store/apps/category/FAMILY_CREATE", "키즈", Arrays.asList(R.string.brain_flower_desc_kids, R.string.longest_used_app_desc_kids)),
        MEDICAL("/store/apps/category/MEDICAL", "의료", Arrays.asList(R.string.brain_flower_desc_medical, R.string.longest_used_app_desc_medical)),
        FOOD_AND_DRINK("/store/apps/category/FOOD_AND_DRINK", "식음료", Arrays.asList(R.string.brain_flower_desc_food_and_drink, R.string.longest_used_app_desc_food_and_drink)),
        EVENTS("/store/apps/category/EVENTS", "이벤트", Arrays.asList(R.string.brain_flower_desc_events, R.string.longest_used_app_desc_events)),
        AUTO_AND_VEHICLES("/store/apps/category/AUTO_AND_VEHICLES", "자동차", Arrays.asList(R.string.brain_flower_desc_auto_and_vehicles, R.string.longest_used_app_desc_auto_and_vehicles)),
        MAPS_AND_NAVIGATION("/store/apps/category/MAPS_AND_NAVIGATION", "지도/네비", Arrays.asList(R.string.brain_flower_desc_maps_and_navigation, R.string.longest_used_app_desc_maps_and_navigation)),
        PARENTING("/store/apps/category/PARENTING", "출산/육아", Arrays.asList(R.string.brain_flower_desc_parenting, R.string.longest_used_app_desc_parenting)),
        ANDROID_WEAR("/store/apps/category/ANDROID_WEAR", "안드로이드웨어러블", Arrays.asList(R.string.brain_flower_desc_android_wear, R.string.longest_used_app_desc_android_wear)),

        DEFAULT("DEFAULT", "DEFAULT", Arrays.asList(R.string.warn_default_category, R.string.warn_default_category));

        private static final Map<String, Category> map = new HashMap<>();

        static {
            for (Category category : values()) {
                map.put(category.categoryId, category);
            }
        }

        public static Category fromId(String categoryId) {
            Category category = map.get(categoryId);

            if (category == null) {
                category = DEFAULT;
            }

            return category;
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