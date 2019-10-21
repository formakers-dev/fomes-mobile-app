package com.formakers.fomes.settings;

import android.os.Bundle;

import androidx.annotation.Nullable;

import com.formakers.fomes.R;
import com.formakers.fomes.common.view.FomesBaseActivity;

public class MyInfoActivity extends FomesBaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().setTitle(R.string.main_menu_my_info);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
}
