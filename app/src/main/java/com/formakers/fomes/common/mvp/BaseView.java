package com.formakers.fomes.common.mvp;

import android.content.Intent;

public interface BaseView<T> {
    void setPresenter(T presenter);

    void startActivity(Intent intent);
}
