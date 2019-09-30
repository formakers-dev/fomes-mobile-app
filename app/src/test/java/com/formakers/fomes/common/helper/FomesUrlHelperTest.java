package com.formakers.fomes.common.helper;

import org.junit.Before;
import org.junit.Test;

import rx.Single;

import static org.assertj.core.api.Java6Assertions.assertThat;


public class FomesUrlHelperTest {

    Single<String> mockUserEmailSingle = Single.just("test@gmail.com");

    FomesUrlHelper subject;

    @Before
    public void setUp() throws Exception {
        subject = new FomesUrlHelper(mockUserEmailSingle);
    }

    @Test
    public void interpretUrlParams_호출시__예약어를_해석한_새로운_URL을_반환한다() {
        String url = subject.interpretUrlParams("http://www.naver.com?email={email}");
        assertThat(url).isEqualTo("http://www.naver.com?email=test@gmail.com");

        String url2 = subject.interpretUrlParams("http://www.naver.com");
        assertThat(url2).isEqualTo("http://www.naver.com");
    }
}