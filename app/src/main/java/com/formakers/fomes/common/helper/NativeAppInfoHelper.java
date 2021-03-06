package com.formakers.fomes.common.helper;

import com.formakers.fomes.common.model.NativeAppInfo;

import javax.inject.Inject;

public class NativeAppInfoHelper {
    private AndroidNativeHelper androidNativeHelper;

    @Inject
    public NativeAppInfoHelper(AndroidNativeHelper androidNativeHelper) {
        this.androidNativeHelper = androidNativeHelper;
    }

    public NativeAppInfo getNativeAppInfo(String packageName) {
        return androidNativeHelper.getNativeAppInfo(packageName);
    }
}
