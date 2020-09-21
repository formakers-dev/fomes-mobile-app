package com.formakers.fomes.settings;

import com.formakers.fomes.common.model.User;
import com.formakers.fomes.common.network.UserService;
import com.formakers.fomes.common.repository.dao.UserDAO;

import org.assertj.core.util.Lists;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import rx.Completable;
import rx.Scheduler;
import rx.Single;
import rx.android.plugins.RxAndroidPlugins;
import rx.android.plugins.RxAndroidSchedulersHook;
import rx.plugins.RxJavaHooks;
import rx.schedulers.Schedulers;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class MyInfoPresenterTest {

    @Mock MyInfoContract.View mockView;
    @Mock UserDAO mockUserDAO;
    @Mock UserService mockUserService;

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

        userInfo = new User()
                .setNickName("닉네임")
                .setBirthday(1991)
                .setJob(1000)
                .setGender("female")
                .setLifeApps(Lists.newArrayList("최애겜"))
                .setMonthlyPayment("5")
                .setFavoriteGenres(Lists.newArrayList("arcade"));
        when(mockUserService.getUser()).thenReturn(Single.just(userInfo));

        subject = new MyInfoPresenter(mockView, mockUserDAO, mockUserService);
    }

    @Test
    public void loadUserInfo_호출시__유저정보를_로드하고__뷰에_바인딩한다() {
        subject.loadUserInfo();

        verify(mockView).bind(eq(userInfo));
    }

    @Test
    public void updateUserInfo_호출시__변경된_정보만_서버에_업데이트_요청한다() {
        when(mockUserService.updateUserInfo(any(User.class))).thenReturn(Completable.complete());

        subject.loadUserInfo();
        User filledUserInfo = new User()
                .setNickName("닉네임")
                .setBirthday(1991)
                .setJob(2000)
                .setGender("female")
                .setLifeApps(Lists.newArrayList("최애겜"))
                .setMonthlyPayment("5")
                .setFavoriteGenres(Lists.newArrayList("arcade"));
        subject.updateUserInfo(filledUserInfo);

        ArgumentCaptor<User> userArgumentCaptor = ArgumentCaptor.forClass(User.class);
        verify(mockUserService).updateUserInfo(userArgumentCaptor.capture());
        User requestedUserInfo = userArgumentCaptor.getValue();

        assertThat(requestedUserInfo.getNickName()).isNull();
        assertThat(requestedUserInfo.getBirthday()).isNull();
        assertThat(requestedUserInfo.getJob()).isEqualTo(2000);
        assertThat(requestedUserInfo.getGender()).isNull();
        assertThat(requestedUserInfo.getLifeApps()).isNull();
        assertThat(requestedUserInfo.getMonthlyPayment()).isNull();
        assertThat(requestedUserInfo.getFavoriteGenres()).isNull();
    }

    @Test
    public void updateUserInfo_호출시__서버에_정상적으로_업데이트되면__내부디비에_업데이트를_한다() {
        when(mockUserService.updateUserInfo(any(User.class))).thenReturn(Completable.complete());

        subject.loadUserInfo();
        User filledUserInfo = new User()
                .setNickName("닉네임")
                .setBirthday(1991)
                .setJob(2000)
                .setGender("female")
                .setLifeApps(Lists.newArrayList("최애겜"))
                .setMonthlyPayment("5")
                .setFavoriteGenres(Lists.newArrayList("arcade"));
        subject.updateUserInfo(filledUserInfo);

        ArgumentCaptor<User> userArgumentCaptor = ArgumentCaptor.forClass(User.class);
        verify(mockUserDAO).updateUserInfo(userArgumentCaptor.capture());
        User requestedUserInfo = userArgumentCaptor.getValue();

        assertThat(requestedUserInfo.getNickName()).isNull();
        assertThat(requestedUserInfo.getBirthday()).isNull();
        assertThat(requestedUserInfo.getJob()).isEqualTo(2000);
        assertThat(requestedUserInfo.getGender()).isNull();
        assertThat(requestedUserInfo.getLifeApps()).isNull();
        assertThat(requestedUserInfo.getMonthlyPayment()).isNull();
        assertThat(requestedUserInfo.getFavoriteGenres()).isNull();
    }

    @Test
    public void updateUserInfo_호출시__서버_업데이트에_실패하면__내부디비에_업데이트를_하지않는다() {
        when(mockUserService.updateUserInfo(any(User.class)))
                .thenReturn(Completable.error(new Exception()));

        subject.loadUserInfo();
        User filledUserInfo = new User()
                .setNickName("닉네임")
                .setBirthday(1991)
                .setJob(2000)
                .setGender("female")
                .setLifeApps(Lists.newArrayList("최애겜"))
                .setMonthlyPayment("5")
                .setFavoriteGenres(Lists.newArrayList("arcade"));
        subject.updateUserInfo(filledUserInfo);

        verify(mockUserDAO, never()).updateUserInfo(any(User.class));
    }

    @Test
    public void updateUserInfo_호출시__서버_업데이트와_내부디비_업데이트에_성공하면__유저정보를_재로드한다() {
        when(mockUserService.updateUserInfo(any(User.class))).thenReturn(Completable.complete());

        subject.loadUserInfo();
        User filledUserInfo = new User()
                .setNickName("닉네임")
                .setBirthday(1991)
                .setJob(2000)
                .setGender("female")
                .setLifeApps(Lists.newArrayList("최애겜"))
                .setMonthlyPayment("5")
                .setFavoriteGenres(Lists.newArrayList("arcade"));

        subject.updateUserInfo(filledUserInfo);

        verify(mockUserService, times(2)).getUser();
        verify(mockView, times(2)).bind(any(User.class));
    }

    @Test
    public void isUpdated_호출시__기존정보에서_업데이트되었는지_체크한다() {
        subject.loadUserInfo();
        assertThat(subject.isUpdated(new User()
                .setNickName("닉네임")
                .setBirthday(1991)
                .setJob(1000)
                .setGender("female")
                .setLifeApps(Lists.newArrayList("최애겜"))
                .setMonthlyPayment("5")
                .setFavoriteGenres(Lists.newArrayList("arcade")))).isFalse();

        assertThat(subject.isUpdated(new User()
                .setNickName("닉네임99")
                .setBirthday(1991)
                .setJob(1000)
                .setGender("female")
                .setLifeApps(Lists.newArrayList("최애겜"))
                .setMonthlyPayment("5")
                .setFavoriteGenres(Lists.newArrayList("arcade")))).isTrue();

        assertThat(subject.isUpdated(new User()
                .setNickName("닉네임")
                .setBirthday(1999)
                .setJob(1000)
                .setGender("female")
                .setLifeApps(Lists.newArrayList("최애겜"))
                .setMonthlyPayment("5")
                .setFavoriteGenres(Lists.newArrayList("arcade")))).isTrue();

        assertThat(subject.isUpdated(new User()
                .setNickName("닉네임")
                .setBirthday(1991)
                .setJob(2000)
                .setGender("female")
                .setLifeApps(Lists.newArrayList("최애겜"))
                .setMonthlyPayment("5")
                .setFavoriteGenres(Lists.newArrayList("arcade")))).isTrue();

        assertThat(subject.isUpdated(new User()
                .setNickName("닉네임")
                .setBirthday(1991)
                .setJob(1000)
                .setGender("male")
                .setLifeApps(Lists.newArrayList("최애겜"))
                .setMonthlyPayment("5")
                .setFavoriteGenres(Lists.newArrayList("arcade")))).isTrue();

        assertThat(subject.isUpdated(new User()
                .setNickName("닉네임")
                .setBirthday(1991)
                .setJob(1000)
                .setGender("female")
                .setLifeApps(Lists.newArrayList("노잼겜"))
                .setMonthlyPayment("5")
                .setFavoriteGenres(Lists.newArrayList("arcade")))).isTrue();

        assertThat(subject.isUpdated(new User()
                .setNickName("닉네임")
                .setBirthday(1991)
                .setJob(1000)
                .setGender("female")
                .setLifeApps(Lists.newArrayList("최애겜"))
                .setMonthlyPayment("10")
                .setFavoriteGenres(Lists.newArrayList("arcade")))).isTrue();

        assertThat(subject.isUpdated(new User()
                .setNickName("닉네임")
                .setBirthday(1991)
                .setJob(1000)
                .setGender("female")
                .setLifeApps(Lists.newArrayList("최애겜"))
                .setMonthlyPayment("5")
                .setFavoriteGenres(Lists.newArrayList("arcade", "puzzle")))).isTrue();
    }
}