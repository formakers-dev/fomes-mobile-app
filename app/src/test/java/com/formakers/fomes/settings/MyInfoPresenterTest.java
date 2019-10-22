package com.formakers.fomes.settings;

import com.formakers.fomes.common.model.User;
import com.formakers.fomes.common.repository.dao.UserDAO;

import org.assertj.core.util.Lists;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import rx.Scheduler;
import rx.Single;
import rx.android.plugins.RxAndroidPlugins;
import rx.android.plugins.RxAndroidSchedulersHook;
import rx.plugins.RxJavaHooks;
import rx.schedulers.Schedulers;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class MyInfoPresenterTest {

    @Mock MyInfoContract.View mockView;
    @Mock UserDAO mockUserDAO;

    User userInfo;
    MyInfoPresenter subject;

    @Before
    public void setUp() throws Exception {
        RxJavaHooks.reset();
        RxJavaHooks.setOnIOScheduler(scheduler -> Schedulers.immediate());
        RxJavaHooks.setOnNewThreadScheduler(scheduler -> Schedulers.immediate());
        RxJavaHooks.setOnComputationScheduler(scheduler -> Schedulers.immediate());

        RxAndroidPlugins.getInstance().reset();
        RxAndroidPlugins.getInstance().registerSchedulersHook(new RxAndroidSchedulersHook() {
            @Override
            public Scheduler getMainThreadScheduler() {
                return Schedulers.immediate();
            }
        });

        MockitoAnnotations.initMocks(this);

        userInfo = new User().setNickName("닉네임").setBirthday(1991).setJob(1000).setGender("female").setLifeApps(Lists.newArrayList("최애겜"));
        when(mockUserDAO.getUserInfo()).thenReturn(Single.just(userInfo));

        subject = new MyInfoPresenter(mockView, mockUserDAO);
    }

    @Test
    public void loadUserInfo_호출시__유저정보를_로드하고__뷰에_바인딩한다() {
        subject.loadUserInfo();

        verify(mockView).bind(eq(userInfo));
    }

    @Test
    public void isUpdated_호출시__기존정보에서_업데이트되었는지_체크한다() {
        subject.loadUserInfo();
        assertThat(subject.isUpdated(1991, 1000, "female", "최애겜")).isFalse();
        assertThat(subject.isUpdated(1992, 1000, "female", "최애겜")).isTrue();
        assertThat(subject.isUpdated(1991, 2000, "female", "최애겜")).isTrue();
        assertThat(subject.isUpdated(1991, 1000, "male", "최애겜")).isTrue();
        assertThat(subject.isUpdated(1991, 1000, "female", "노잼겜")).isTrue();

    }
}