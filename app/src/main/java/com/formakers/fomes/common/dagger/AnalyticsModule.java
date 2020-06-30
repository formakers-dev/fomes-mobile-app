package com.formakers.fomes.common.dagger;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.formakers.fomes.common.constant.FomesConstants;
import com.formakers.fomes.common.noti.ChannelManager;
import com.formakers.fomes.common.util.Log;
import com.google.common.base.Strings;
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

    /*** TODO : 임시구조. 코드 분리 필요, 네이밍 고민(Tracking? Log?) ***/
    public interface Analytics {
        void setCurrentScreen(Activity activity);
        void setCurrentScreen(Fragment fragment);
        void sendClickEventLog(String contentType, String id);
        void sendClickEventLog(String contentType, String id, String name);
        void sendClickEventLog(String contentType, String id, String name, Long value);
        void sendNotificationEventLog(String action, ChannelManager.Channel channel, String title);
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
        public void sendClickEventLog(String target, String id) {
            sendClickEventLog(target, id, null);
        }

        @Override
        public void sendClickEventLog(String target, String id, String name) {
            sendClickEventLog(target, id, name, null);
        }

        @Override
        public void sendClickEventLog(String target, String id, String name, Long value) {
            Bundle params = new Bundle();
            if (!Strings.isNullOrEmpty(target)) {
                params.putString(FirebaseAnalytics.Param.CONTENT_TYPE, target);
            }
            if (!Strings.isNullOrEmpty(id)) {
                params.putString(FirebaseAnalytics.Param.ITEM_ID, id);
            }
            if (!Strings.isNullOrEmpty(name)) {
                params.putString(FirebaseAnalytics.Param.ITEM_NAME, name);
            }
            if (value != null) {
                params.putLong(FirebaseAnalytics.Param.VALUE, value);
            }

            sendClickEvent(params);
        }

        private void sendClickEvent(Bundle params) {
            this.firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, params);
        }

        @Override
        public void sendNotificationEventLog(@NonNull String action, ChannelManager.Channel channel, String title) {
            boolean isShowing = false;

            String event = FirebaseAnalytics.Event.VIEW_ITEM;

            switch (action) {
                case FomesConstants.Notification.Log.ACTION_OPEN: {
                    event = FirebaseAnalytics.Event.SELECT_CONTENT;
                    break;
                }
                case FomesConstants.Notification.Log.ACTION_RECEIVE: {
                    isShowing = true;
                    break;
                }
                case FomesConstants.Notification.Log.ACTION_DISMISS: {
                    isShowing = false;
                    break;
                }
            }

            Bundle params = new Bundle();
            params.putString(FirebaseAnalytics.Param.ITEM_ID, title);   // TODO : 노티에 ID 생기고 난 이후에 변경 필요
            params.putString(FirebaseAnalytics.Param.ITEM_NAME, title);
            params.putString(FirebaseAnalytics.Param.ITEM_CATEGORY, channel.name());
            params.putDouble(FirebaseAnalytics.Param.VALUE, isShowing ? 1d : 0d);
            this.firebaseAnalytics.logEvent(event, params);
        }
    }
}
