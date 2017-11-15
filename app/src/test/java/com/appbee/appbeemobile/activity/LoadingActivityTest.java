package com.appbee.appbeemobile.activity;

import android.content.Intent;

import com.appbee.appbeemobile.BuildConfig;
import com.appbee.appbeemobile.TestAppBeeApplication;
import com.appbee.appbeemobile.helper.AppBeeAndroidNativeHelper;
import com.appbee.appbeemobile.helper.AppUsageDataHelper;
import com.appbee.appbeemobile.helper.LocalStorageHelper;
import com.appbee.appbeemobile.model.AppInfo;
import com.appbee.appbeemobile.model.User;
import com.appbee.appbeemobile.network.AppService;
import com.appbee.appbeemobile.network.AppStatService;
import com.appbee.appbeemobile.network.UserService;
import com.appbee.appbeemobile.repository.helper.AppRepositoryHelper;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.android.controller.ActivityController;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowActivity;
import org.robolectric.shadows.ShadowToast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import rx.Observable;
import rx.plugins.RxJavaHooks;
import rx.schedulers.Schedulers;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.robolectric.Shadows.shadowOf;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class LoadingActivityTest extends ActivityTest {

    private ActivityController<LoadingActivity> activityController;

    private Unbinder binder;

    @Inject
    AppStatService mockAppStatService;

    @Inject
    AppService mockAppService;

    @Inject
    AppRepositoryHelper mockAppRepositoryHelper;

    @Inject
    AppUsageDataHelper mockAppUsageDataHelper;

    @Inject
    AppBeeAndroidNativeHelper mockAppBeeAndroidNativeHelper;

    @Inject
    UserService mockUserService;

    @Inject
    LocalStorageHelper mockLocalStorageHelper;

    @Before
    public void setUp() throws Exception {
        ((TestAppBeeApplication) RuntimeEnvironment.application).getComponent().inject(this);
        activityController = Robolectric.buildActivity(LoadingActivity.class);

        when(mockAppBeeAndroidNativeHelper.hasUsageStatsPermission()).thenReturn(true);
        when(mockAppStatService.getLastUpdateStatTimestamp()).thenReturn(Observable.just(0L));
        when(mockAppStatService.sendShortTermStats(any(List.class), anyLong())).thenReturn(Observable.just(true));

        RxJavaHooks.reset();
        RxJavaHooks.setOnIOScheduler(scheduler -> Schedulers.immediate());
    }

    @After
    public void tearDown() throws Exception {
        if (binder != null) {
            binder.unbind();
        }
        RxJavaHooks.reset();
    }

    private LoadingActivity createSubjectWithPostCreateLifecycle() {
        LoadingActivity subject = activityController.create().postCreate(null).get();
        binder = ButterKnife.bind(this, subject);
        return subject;
    }

    @Test
    public void onPostCreate호출시_유저정보를_전송한다() throws Exception {
        when(mockLocalStorageHelper.getUserId()).thenReturn("userId");
        when(mockLocalStorageHelper.getEmail()).thenReturn("email@email.com");
        when(mockLocalStorageHelper.getGender()).thenReturn("male");
        when(mockLocalStorageHelper.getBirthday()).thenReturn(1999);
        when(mockLocalStorageHelper.getRegistrationToken()).thenReturn("registration-token");
        when(mockUserService.sendUser(any(User.class))).thenReturn(Observable.just(true));

        createSubjectWithPostCreateLifecycle();

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(mockUserService).sendUser(userCaptor.capture());

        assertThat(userCaptor.getValue().getUserId()).isEqualTo("userId");
        assertThat(userCaptor.getValue().getEmail()).isEqualTo("email@email.com");
        assertThat(userCaptor.getValue().getGender()).isEqualTo("male");
        assertThat(userCaptor.getValue().getBirthday()).isEqualTo(1999);
        assertThat(userCaptor.getValue().getRegistrationToken()).isEqualTo("registration-token");
    }

    @Test
    public void onPostCreate호출시_ShortTermStats을_가져와_가공() throws Exception {

    }
}