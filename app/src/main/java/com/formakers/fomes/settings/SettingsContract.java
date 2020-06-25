package com.formakers.fomes.settings;

import com.formakers.fomes.common.mvp.BaseView;

public interface SettingsContract {
    interface Presenter {
        boolean isSubscribedTopic(String topic);

        void toggleNotification(String topic);
    }

    interface View extends BaseView<Presenter> {
    }
}
