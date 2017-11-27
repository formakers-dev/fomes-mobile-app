package com.appbee.appbeemobile.adapter;

import com.appbee.appbeemobile.BuildConfig;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Java6Assertions.assertThat;


@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class DetailPlansAdapterTest {
    private DetailPlansAdapter subject;

    @Before
    public void setUp() throws Exception {

        List<String> timsSlots = Arrays.asList("time8", "time9", "time10");

        subject = new DetailPlansAdapter(timsSlots);
    }

    @Test
    public void Adapter생성하면_timeSlot를formatting하여_버튼에_셋팅한다() throws Exception {
        assertThat(subject.getItemCount()).isEqualTo(3);
        assertThat(subject.getItem(0)).isEqualTo("time8");
        assertThat(subject.getItem(1)).isEqualTo("time9");
        assertThat(subject.getItem(2)).isEqualTo("time10");
    }
}