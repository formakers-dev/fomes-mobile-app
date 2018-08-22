package com.formakers.fomes.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.formakers.fomes.R;
import com.formakers.fomes.fragment.ReportMostUsedFragment;

/**
 * Created by yenar on 2018-08-22.
 */

public class ReportActivity extends BaseActivity {
    private FragmentManager fragmentManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        fragmentManager = getSupportFragmentManager();

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.most_used_container, new ReportMostUsedFragment(), ReportMostUsedFragment.TAG);
        fragmentTransaction.commit();
    }
}
