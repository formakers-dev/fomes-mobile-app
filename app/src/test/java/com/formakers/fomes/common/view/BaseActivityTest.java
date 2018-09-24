package com.formakers.fomes.common.view;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.util.ArraySet;
import android.view.View;

import com.formakers.fomes.BuildConfig;
import com.formakers.fomes.TestAppBeeApplication;

import org.junit.After;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.android.controller.ActivityController;
import org.robolectric.annotation.Config;
import org.robolectric.util.ReflectionHelpers;

import java.util.ArrayList;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.robolectric.Shadows.shadowOf;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, application = TestAppBeeApplication.class)
public abstract class BaseActivityTest<T extends Activity> {
    public static final int LIFECYCLE_TYPE_CREATE = 1;
    public static final int LIFECYCLE_TYPE_POST_CREATE = 2;
    public static final int LIFECYCLE_TYPE_RESUME = 3;
    public static final int LIFECYCLE_TYPE_VISIBLE = 4;

    private Class activityClass;
    private ActivityController<T> activityController;
    protected T subject;

    public void launchActivity() {
        subject = getActivity();
    }

    public BaseActivityTest(Class<T> clazz) {
        this.activityClass = clazz;
    }

    protected T getActivity() {
        return getActivity(LIFECYCLE_TYPE_RESUME, null);    // = Roboletric.setupActivity()
    }

    protected T getActivity(Intent intent) {
        return getActivity(LIFECYCLE_TYPE_RESUME, intent);
    }

    protected T getActivity(int lifecycleType) {
        return getActivity(lifecycleType, null);
    }

    protected T getActivity(int lifecycleType, Intent intent) {
        switch (lifecycleType) {
            case LIFECYCLE_TYPE_CREATE:
                this.activityController = getActivityController(intent).create();
                break;
            case LIFECYCLE_TYPE_POST_CREATE:
                this.activityController = getActivityController(intent).create().start().postCreate(null);
                break;
            case LIFECYCLE_TYPE_RESUME:
                this.activityController = getActivityController(intent).create().start().postCreate(null).resume();    // = Roboletric.setupActivity()
                break;
            case LIFECYCLE_TYPE_VISIBLE:
                this.activityController = getActivityController(intent).create().start().postCreate(null).resume().visible();    // = Roboletric.setupActivity()
                break;
            default:
                throw new IllegalArgumentException("Check LIFECYCLE_TYPE");
        }
        return activityController.get();
    }

    protected ActivityController<T> getActivityController() {
        return getActivityController(null);
    }

    protected ActivityController<T> getActivityController(Intent intent) {
        if (this.activityController == null) {
            this.activityController = Robolectric.buildActivity(activityClass, intent);
        }
        return this.activityController;
    }

    @After
    public void tearDown() throws Exception {
        if (activityController != null) {
            activityController.pause().stop().destroy();
        }
    }

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

    protected void verifyMoveToActivity(Activity subject, Class expectActivityClass) {
        assertThat(shadowOf(subject).getNextStartedActivity().getComponent().getClassName()).isEqualTo(expectActivityClass.getName());
        assertThat(subject.isFinishing()).isTrue();
    }
}
