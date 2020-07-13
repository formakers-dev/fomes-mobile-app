package com.formakers.fomes.point.withdraw;

import android.os.Bundle;
import android.widget.EditText;

import androidx.annotation.Nullable;

import com.formakers.fomes.R;
import com.formakers.fomes.common.view.FomesBaseActivity;
import com.shawnlin.numberpicker.NumberPicker;

import butterknife.BindView;

public class PointWithdrawActivity extends FomesBaseActivity {

    @BindView(R.id.withdraw_count) NumberPicker withdrawCountNumberPicker;
    @BindView(R.id.withdraw_phone_number) EditText phoneNumberEditText;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_point_withdraw);

        getSupportActionBar().setTitle("문화상품권 교환 신청");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        //        bind
        withdrawCountNumberPicker.setMaxValue(2);
    }
}
