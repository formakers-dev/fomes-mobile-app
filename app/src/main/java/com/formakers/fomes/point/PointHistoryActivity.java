package com.formakers.fomes.point;

import android.os.Bundle;

import androidx.annotation.Nullable;

import com.formakers.fomes.FomesApplication;
import com.formakers.fomes.R;
import com.formakers.fomes.common.view.FomesBaseActivity;

import javax.inject.Inject;

public class PointHistoryActivity extends FomesBaseActivity
        implements PointHistoryContract.View {

    @Inject PointHistoryContract.Presenter presenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_point_history);

        getSupportActionBar().setTitle("나의 포인트 내역");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        DaggerPointHistoryDagger_Component.builder()
                .applicationComponent(FomesApplication.get(this).getComponent())
                .module(new PointHistoryDagger.Module(this))
                .build()
                .inject(this);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }

    @Override
    public void setPresenter(PointHistoryContract.Presenter presenter) {
        this.presenter = presenter;
    }
}
