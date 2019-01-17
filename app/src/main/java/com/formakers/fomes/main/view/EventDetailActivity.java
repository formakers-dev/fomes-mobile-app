package com.formakers.fomes.main.view;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.formakers.fomes.R;
import com.formakers.fomes.common.view.FomesBaseActivity;

public class EventDetailActivity extends FomesBaseActivity {

    public static final String EXTRA_LAYOUT_RES_ID = "LAYOUT_RES_ID";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(getIntent().getIntExtra(EXTRA_LAYOUT_RES_ID, R.layout.activity_event_beta_test_open));

        getSupportActionBar().setTitle("이벤트");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
}
