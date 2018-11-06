package com.formakers.fomes.wishList;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.formakers.fomes.R;
import com.formakers.fomes.common.view.FomesBaseActivity;

public class WishListActivity extends FomesBaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_wish_list);

        getSupportActionBar().setTitle(R.string.wish_list);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

}
