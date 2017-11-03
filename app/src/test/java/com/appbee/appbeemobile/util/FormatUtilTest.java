package com.appbee.appbeemobile.util;

import com.google.common.collect.Lists;
import org.junit.Test;
import java.util.List;

import static org.assertj.core.api.Java6Assertions.assertThat;


public class FormatUtilTest {

    @Test
    public void formatLongCategoryName호출시_카테고리명이4글자초과인경우_개행처리한결과를_리턴한다() throws Exception {
        assertThat(FormatUtil.formatLongCategoryName("커뮤니케이션")).isEqualTo("커뮤니\n케이션");
        assertThat(FormatUtil.formatLongCategoryName("커뮤니/케이션")).isEqualTo("커뮤니/\n케이션");
        assertThat(FormatUtil.formatLongCategoryName("지도/네비")).isEqualTo("지도/\n네비");
        assertThat(FormatUtil.formatLongCategoryName("부동산/인테리어")).isEqualTo("부동산/\n인테리어");
        assertThat(FormatUtil.formatLongCategoryName("안드로이드웨어러블")).isEqualTo("안드로이드\n웨어러블");
    }

    @Test
    public void formatLongCategoryName호출시_카테고리명이4글자이하인경우_그대로_리턴한다() throws Exception {
        assertThat(FormatUtil.formatLongCategoryName("금융")).isEqualTo("금융");
        assertThat(FormatUtil.formatLongCategoryName("비즈니스")).isEqualTo("비즈니스");
    }

    @Test
    public void formatAppsString호출시_대괄호로감싼Apps텍스트가리턴된다() throws Exception {
        List<String> apps = Lists.newArrayList("첫번째앱");
        assertThat(FormatUtil.formatAppsString(apps)).isEqualTo("[첫번째앱]");

        apps = Lists.newArrayList("첫번째앱", "두번째앱");
        assertThat(FormatUtil.formatAppsString(apps)).isEqualTo("[첫번째앱][두번째앱]");
    }

    @Test
    public void formatDisplayDateString호출시_화면에표시하기위한_DateFormat으로_입력날짜를_변환하여_리턴한다() throws Exception {
        String result = FormatUtil.formatDisplayDateString("20170102");
        assertThat(result).isEqualTo("17.01.02");
    }
}