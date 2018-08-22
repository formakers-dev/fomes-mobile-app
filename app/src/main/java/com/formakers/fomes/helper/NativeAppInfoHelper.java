package com.formakers.fomes.helper;

import com.formakers.fomes.model.NativeAppInfo;

import javax.inject.Inject;

public class NativeAppInfoHelper {
    private AppBeeAndroidNativeHelper appBeeAndroidNativeHelper;

    @Inject
    public NativeAppInfoHelper(AppBeeAndroidNativeHelper appBeeAndroidNativeHelper) {
        this.appBeeAndroidNativeHelper = appBeeAndroidNativeHelper;
    }

    public NativeAppInfo getNativeAppInfo(String packageName) {
        return appBeeAndroidNativeHelper.getNativeAppInfo(packageName);
    }
}
