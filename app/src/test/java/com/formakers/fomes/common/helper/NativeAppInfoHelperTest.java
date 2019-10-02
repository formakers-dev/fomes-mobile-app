package com.formakers.fomes.common.helper;


import org.junit.Before;
import org.junit.Test;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class NativeAppInfoHelperTest {

    private AndroidNativeHelper mockAndroidNativeHelper;

    private NativeAppInfoHelper subject;

    @Before
    public void setUp() throws Exception {
        mockAndroidNativeHelper = mock(AndroidNativeHelper.class);
        subject = new NativeAppInfoHelper(mockAndroidNativeHelper);
    }

    @Test
    public void getNativeAppInfo호출시_AppBeeAndroidNativieHelper_getNativeAppInfo메소드를_호출한다() throws Exception {
        subject.getNativeAppInfo("com.package.name");
        verify(mockAndroidNativeHelper).getNativeAppInfo(eq("com.package.name"));
    }
}