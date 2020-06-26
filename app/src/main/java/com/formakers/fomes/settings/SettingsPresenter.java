package com.formakers.fomes.settings;

import com.formakers.fomes.common.noti.ChannelManager;

import javax.inject.Inject;

@SettingsDagger.Scope
public class SettingsPresenter implements SettingsContract.Presenter {

    private SettingsContract.View view;
    private ChannelManager channelManager;

    @Inject
    public SettingsPresenter(SettingsContract.View view, ChannelManager channelManager) {
        this.view = view;
        this.channelManager = channelManager;
    }

    @Override
    public boolean isSubscribedTopic(String topic) {
        return this.channelManager.isSubscribedTopic(topic);
    }

    @Override
    public void toggleNotification(String topic) {
        if (this.isSubscribedTopic(topic)) {
            this.channelManager.unsubscribeTopic(topic);
            this.view.showToast("전체 알림이 수신 거부되었습니다");
        } else {
            this.channelManager.subscribeTopic(topic);
            this.view.showToast("전체 알림이 수신 허용되었습니다");
        }
    }
}
