package com.appbee.appbeemobile.activity;

import android.annotation.SuppressLint;
import android.util.ArraySet;
import android.view.View;

import org.junit.After;
import org.robolectric.util.ReflectionHelpers;

import java.util.ArrayList;

public class ActivityTest {
    @SuppressLint("NewApi")
    @After
    public void resetWindowManager() {
        Class clazz = ReflectionHelpers.loadClass(getClass().getClassLoader(), "android.view.WindowManagerGlobal");
        Object instance = ReflectionHelpers.callStaticMethod(clazz, "getInstance");
        Object lock = ReflectionHelpers.getField(instance, "mLock");

        ArrayList<Object> roots = ReflectionHelpers.getField(instance, "mRoots");

        synchronized (lock) {
            for (int i = 0; i < roots.size(); i++) {
                ReflectionHelpers.callInstanceMethod(instance, "removeViewLocked",
                        ReflectionHelpers.ClassParameter.from(int.class, i),
                        ReflectionHelpers.ClassParameter.from(boolean.class, false));
            }
        }

        ArraySet<View> dyingViews = ReflectionHelpers.getField(instance, "mDyingViews");
        dyingViews.clear();
    }
}
