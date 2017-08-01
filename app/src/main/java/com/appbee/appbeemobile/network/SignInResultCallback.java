package com.appbee.appbeemobile.network;

public interface SignInResultCallback {
    void onSuccess(String token);
    void onFail();
}