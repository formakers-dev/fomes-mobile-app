package com.appbee.appbeemobile.adapter.holder;

import android.view.LayoutInflater;

import com.appbee.appbeemobile.BuildConfig;
import com.appbee.appbeemobile.R;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import static org.assertj.core.api.Java6Assertions.assertThat;


@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class HeaderViewHolderTest {

    private HeaderViewHolder subject;

    @Test
    public void 추천앱의_HeaderViewHolder를_생성하면_추천앱타이틀을_보여준다() throws Exception {
        subject = new HeaderViewHolder(LayoutInflater.from(RuntimeEnvironment.application).inflate(R.layout.item_header, null), HeaderViewHolder.HEADER_TYPE_RECOMMENDATION);

        assertThat(subject.titleTextView.getText()).isEqualTo("당신을 위한 추천 앱*");
        assertThat(subject.subtitleTextView.getText()).isEqualTo("인터뷰나 테스트 참여가 가능한 프로젝트입니다.");
    }

    @Test
    public void Clab의_HeaderViewHolder를_생성하면_추천앱타이틀을_보여준다() throws Exception {
        subject = new HeaderViewHolder(LayoutInflater.from(RuntimeEnvironment.application).inflate(R.layout.item_header, null), HeaderViewHolder.HEADER_TYPE_CLAB);

        assertThat(subject.titleTextView.getText()).isEqualTo("취향저격 C-lab 프로젝트 둘러보기");
        assertThat(subject.subtitleTextView.getText()).isEqualTo("앱 사용패턴 기반으로 당신만을 위해 선별된\nC-lab Fair프로젝트를 소개합니다.");
    }
}