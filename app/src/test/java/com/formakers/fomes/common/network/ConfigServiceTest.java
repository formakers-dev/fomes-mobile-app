package com.formakers.fomes.common.network;

import com.formakers.fomes.helper.SharedPreferencesHelper;
import com.formakers.fomes.common.network.api.ConfigAPI;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;

import java.util.Arrays;
import java.util.List;

import edu.emory.mathcs.backport.java.util.Collections;
import rx.Observable;
import rx.observers.TestSubscriber;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
public class ConfigServiceTest extends AbstractServiceTest {

    private ConfigService subject;

    @Mock
    private ConfigAPI mockConfigAPI;

    @Mock
    private SharedPreferencesHelper mockSharedPreferencesHelper;

    @Before
    public void setUp() throws Exception {
        super.setUp();

        subject = new ConfigService(mockConfigAPI, mockSharedPreferencesHelper, getMockAPIHelper());

        when(mockSharedPreferencesHelper.getAccessToken()).thenReturn("myToken");
        when(mockConfigAPI.getAppVersion()).thenReturn(Observable.just(123L));
        when(mockConfigAPI.getExcludePackageNames(eq("myToken"))).thenReturn(Observable.just(Arrays.asList("com.kakao.talk", "com.naver.search")));
    }

    @Test
    public void getAppVersion호출시_앱버전을_리턴한다() throws Exception {
        TestSubscriber<Long> testSubscriber = new TestSubscriber<>();

        subject.getAppVersion().subscribe(testSubscriber);

        testSubscriber.assertReceivedOnNext(Collections.singletonList(123L));
    }

    @Test
    public void getExcludePackageNames호출시_앱분석시_제외할_앱목록을_리턴한다() throws Exception {
        TestSubscriber<List<String>> testSubscriber = new TestSubscriber<>();

        subject.getExcludePackageNames().subscribe(testSubscriber);

        testSubscriber.assertReceivedOnNext(Collections.singletonList(Arrays.asList("com.kakao.talk", "com.naver.search")));
    }

    @Test
    public void postCancelParticipate호출시_토큰_만료_여부를_확인한다() throws Exception {
        verifyToCheckExpiredToken(subject.getExcludePackageNames().toObservable());
    }
}