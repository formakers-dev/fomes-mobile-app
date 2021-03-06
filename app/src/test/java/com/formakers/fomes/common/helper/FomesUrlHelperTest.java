package com.formakers.fomes.common.helper;

import android.os.Bundle;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import rx.Single;

import static org.assertj.core.api.Java6Assertions.assertThat;

@RunWith(RobolectricTestRunner.class)
public class FomesUrlHelperTest {

    Single<String> mockUserEmailSingle = Single.just("test@gmail.com");

    FomesUrlHelper subject;

    @Before
    public void setUp() throws Exception {
        subject = new FomesUrlHelper(mockUserEmailSingle);
    }

    // TODO : 케이스별로 테스트 하나씩 분리
    @Test
    public void interpretUrlParams_호출시__예약어를_해석한_새로운_URL을_반환한다() {
        Bundle params = new Bundle();
        params.putString(FomesUrlHelper.EXTRA_BETA_TEST_ID, "bid");
        params.putString(FomesUrlHelper.EXTRA_MISSION_ID, "mid");

        String url = subject.interpretUrlParams("http://www.naver.com?email={email}&ids={b-m-ids}", params);
        assertThat(url).isEqualTo("http://www.naver.com?email=test@gmail.com&ids=WW1saw$$Yldsaw");

        String url2 = subject.interpretUrlParams("http://www.naver.com");
        assertThat(url2).isEqualTo("http://www.naver.com");

        String url3 = subject.interpretUrlParams("http://www.naver.com?email={email}");
        assertThat(url3).isEqualTo("http://www.naver.com?email=test@gmail.com");
    }
}