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
        } else {
            this.channelManager.subscribeTopic(topic);
        }
    }
}
