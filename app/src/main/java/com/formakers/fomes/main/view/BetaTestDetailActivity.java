package com.formakers.fomes.main.view;

import android.os.Bundle;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.formakers.fomes.R;
import com.formakers.fomes.common.FomesConstants;
import com.formakers.fomes.common.util.Log;
import com.formakers.fomes.common.view.FomesBaseActivity;

import butterknife.BindView;

public class BetaTestDetailActivity extends FomesBaseActivity {

    private static final String TAG = "BetaTestDetailActivity";

    @BindView(R.id.betatest_detail_title_textview) TextView titleTextView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.v(TAG,"onCreate");

        this.setContentView(R.layout.activity_betatest_detail);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        Bundle bundle = getIntent().getExtras();

        if (bundle == null) {
            Toast.makeText(this, "문제가 발생했습니다. 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        String id = bundle.getString(FomesConstants.BetaTest.EXTRA_GROUP_ID);
        titleTextView.setText(id);
    }
}
