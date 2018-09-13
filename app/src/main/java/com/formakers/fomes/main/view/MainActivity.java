package com.formakers.fomes.main.view;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.formakers.fomes.AppBeeApplication;
import com.formakers.fomes.R;
import com.formakers.fomes.common.view.FomesBaseActivity;

public class MainActivity extends FomesBaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((AppBeeApplication) getApplication()).getComponent().inject(this);
        this.setContentView(R.layout.activity_main);
    }
}
