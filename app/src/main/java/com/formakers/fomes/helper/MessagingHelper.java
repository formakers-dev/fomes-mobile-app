package com.formakers.fomes.helper;


import android.support.annotation.Nullable;

import com.google.firebase.iid.FirebaseInstanceId;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class MessagingHelper {

    @Inject
    public MessagingHelper() {
    }

    @Nullable
    public String getMessagingToken() {
        return FirebaseInstanceId.getInstance().getToken();
    }
}
