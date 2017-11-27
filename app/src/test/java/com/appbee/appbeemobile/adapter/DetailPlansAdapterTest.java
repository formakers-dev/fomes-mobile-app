package com.appbee.appbeemobile.adapter;

import android.view.LayoutInflater;
import android.view.View;

import com.appbee.appbeemobile.BuildConfig;
import com.appbee.appbeemobile.R;
import com.appbee.appbeemobile.adapter.holder.DetailPlansHolder;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
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
    public void getItemCountTest() throws Exception {
        assertThat(subject.getItemCount()).isEqualTo(3);
    }

    @Test
    public void getItemTest() throws Exception {
        assertThat(subject.getItem(0)).isEqualTo("time8");
        assertThat(subject.getItem(1)).isEqualTo("time9");
        assertThat(subject.getItem(2)).isEqualTo("time10");
    }

    @Test
    public void onBindViewHolderTest() throws Exception {
        View itemView = LayoutInflater.from(RuntimeEnvironment.application).inflate(R.layout.item_time_button, null, false);
        DetailPlansHolder holder = new DetailPlansHolder(itemView);

        subject.onBindViewHolder(holder, 0);

        assertThat(holder.button.getText()).isEqualTo("08:00");
    }

    @Test
    public void onBindViewHolder_buttonOnClickTest() throws Exception {
        View itemView = LayoutInflater.from(RuntimeEnvironment.application).inflate(R.layout.item_time_button, null, false);
        DetailPlansHolder holder = new DetailPlansHolder(itemView);

        subject.onBindViewHolder(holder, 2);

        holder.button.performClick();

        assertThat(subject.getSelectedTimeSlot()).isEqualTo("time10");
    }

    @Test
    public void setAndGetSelectedTimeSlotTest() throws Exception {
        subject.setSelectedTimeSlot(1);
        assertThat(subject.getSelectedTimeSlot()).isEqualTo("time9");
    }
}