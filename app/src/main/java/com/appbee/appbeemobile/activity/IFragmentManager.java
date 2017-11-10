package com.appbee.appbeemobile.activity;

import android.support.annotation.NonNull;

public interface IFragmentManager {
    void replaceFragment(@NonNull String fragmentTag);
    void startMainActivityAndFinish();
}
