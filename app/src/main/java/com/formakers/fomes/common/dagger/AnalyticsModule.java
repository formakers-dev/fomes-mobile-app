package com.formakers.fomes.common.dagger;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.formakers.fomes.common.util.Log;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.Objects;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(includes = { ApplicationModule.class })
public class AnalyticsModule {
    @Singleton
    @Provides
    public FirebaseAnalytics firebaseAnalytics(Context context) {
        return FirebaseAnalytics.getInstance(context);
    }

    @Singleton
    @Provides
    public Analytics analytics(FirebaseAnalytics firebaseAnalytics) {
        return new AnalyticsImpl(firebaseAnalytics);
    }

    /*** TODO : 임시구조. 코드 분리 필요, 네이밍 고민(Tracking? Logging?) ***/
    public interface Analytics {
        void setCurrentScreen(Activity activity);
        void setCurrentScreen(Fragment fragment);
        void sendClickEventLog(String contentType, String id);
    }

    public class AnalyticsImpl implements Analytics {
        FirebaseAnalytics firebaseAnalytics;

        public AnalyticsImpl(FirebaseAnalytics firebaseAnalytics) {
            this.firebaseAnalytics = firebaseAnalytics;
        }

        @Override
        public void setCurrentScreen(Activity activity) {
            Log.v("Analytics", "setCurrentScreen(" + activity + ")");
            this.firebaseAnalytics.setCurrentScreen(activity, activity.getClass().getSimpleName(), activity.getClass().getSimpleName());
        }

        @Override
        public void setCurrentScreen(Fragment fragment) {
            Log.v("Analytics", "setCurrentScreen(" + fragment + ")");
            this.firebaseAnalytics.setCurrentScreen(Objects.requireNonNull(fragment.getActivity()), fragment.getClass().getSimpleName(), fragment.getClass().getSimpleName());
        }

        @Override
        public void sendClickEventLog(String contentType, String id) {
            Bundle params = new Bundle();
            params.putString("CONTENT_TYPE", contentType);
            params.putString("ITEM_ID", id);
            this.firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, params);
        }
    }
}
