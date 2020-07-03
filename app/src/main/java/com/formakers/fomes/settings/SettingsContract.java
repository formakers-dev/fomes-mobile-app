package com.formakers.fomes.settings;

import android.content.DialogInterface;

import com.formakers.fomes.common.dagger.AnalyticsModule;
import com.formakers.fomes.common.mvp.BaseView;

public interface SettingsContract {
    interface Presenter {
        //Base
        AnalyticsModule.Analytics getAnalytics();

        boolean isSubscribedTopic(String topic);

        void toggleNotification(String topic);
    }

    interface View extends BaseView<Presenter> {
        void showToast(String message);
        void showAlertDialog(String message,
                             DialogInterface.OnClickListener positiveClickListener,
                             DialogInterface.OnClickListener negativeClickListener);
    }
}
