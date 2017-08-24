package com.appbee.appbeemobile.helper;


import org.junit.Before;
import org.junit.Test;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class NativeAppInfoHelperTest {

    private AppBeeAndroidNativeHelper mockAppBeeAndroidNativeHelper;

    private NativeAppInfoHelper subject;

    @Before
    public void setUp() throws Exception {
        mockAppBeeAndroidNativeHelper = mock(AppBeeAndroidNativeHelper.class);
        subject = new NativeAppInfoHelper(mockAppBeeAndroidNativeHelper);
    }

    @Test
    public void getNativeAppInfo호출시_AppBeeAndroidNativieHelper_getNativeAppInfo메소드를_호출한다() throws Exception {
        subject.getNativeAppInfo("com.package.name");
        verify(mockAppBeeAndroidNativeHelper).getNativeAppInfo(eq("com.package.name"));
    }
}