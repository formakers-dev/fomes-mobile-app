package com.formakers.fomes.betatest;

import android.os.Bundle;

import androidx.annotation.Nullable;

import com.formakers.fomes.R;
import com.formakers.fomes.common.constant.FomesConstants;
import com.formakers.fomes.common.util.Log;
import com.formakers.fomes.common.view.FomesBaseActivity;

public class BetaTestCertificateActivity extends FomesBaseActivity {
    private static final String TAG = "BetaTestCertificateActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.v(TAG, "onCreate intent=" + getIntent().getStringExtra(FomesConstants.BetaTest.EXTRA_ID));

        this.setContentView(R.layout.activity_betatest_certificate);

        getSupportActionBar().setTitle("테스트 참여 확인증");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
}
