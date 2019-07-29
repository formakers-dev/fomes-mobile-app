package com.formakers.fomes.common.util;

import androidx.test.core.app.ApplicationProvider;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import static org.assertj.core.api.Java6Assertions.assertThat;

@RunWith(RobolectricTestRunner.class)
public class UnitConverterUtilTest {

    @Test
    public void pixelToDp_호출시__pixel을_dp로_단위변환한다() {
        float dp = UnitConverterUtil.pixelToDp(ApplicationProvider.getApplicationContext().getResources().getDisplayMetrics(), 320);
        assertThat(dp).isEqualTo(320);
    }

    @Test
    public void dpToPixel호출시__dp을_pixel로_단위변환한다() {
        float pixel = UnitConverterUtil.dpToPixel(ApplicationProvider.getApplicationContext().getResources().getDisplayMetrics(), 320);
        assertThat(pixel).isEqualTo(320);
    }
}