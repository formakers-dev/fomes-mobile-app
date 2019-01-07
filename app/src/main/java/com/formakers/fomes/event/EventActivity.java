package com.formakers.fomes.event;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.formakers.fomes.R;
import com.formakers.fomes.common.view.FomesBaseActivity;

public class EventActivity extends FomesBaseActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_event);

        getSupportActionBar().setTitle("이벤트");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
}
