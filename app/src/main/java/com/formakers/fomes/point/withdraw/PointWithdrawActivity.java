package com.formakers.fomes.point.withdraw;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.formakers.fomes.FomesApplication;
import com.formakers.fomes.R;
import com.formakers.fomes.common.view.FomesBaseActivity;
import com.shawnlin.numberpicker.NumberPicker;

import java.text.NumberFormat;

import javax.inject.Inject;

import butterknife.BindView;

public class PointWithdrawActivity extends FomesBaseActivity implements PointWithdrawContract.View {

    @BindView(R.id.withdraw_count) NumberPicker withdrawCountNumberPicker;
    @BindView(R.id.withdraw_phone_number) EditText phoneNumberEditText;
    @BindView(R.id.my_available_point) TextView availablePointTextView;
    @BindView(R.id.withdraw_button) Button withdrawButton;

    @Inject PointWithdrawContract.Presenter presenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_point_withdraw);

        getSupportActionBar().setTitle("문화상품권 교환 신청");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        DaggerPointWithdrawDagger_Component.builder()
                .applicationComponent(FomesApplication.get(this).getComponent())
                .module(new PointWithdrawDagger.Module(this))
                .build()
                .inject(this);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // bind View
        this.presenter.bindAvailablePoint();
    }

    @Override
    public void setAvailablePoint(long point) {
        availablePointTextView.setText(String.format("%s P", NumberFormat.getInstance().format(point)));

        availablePointTextView.startAnimation(getFadeInAnimation(300));
    }

    @Override
    public void setMaxWithdrawCount(int maxWithdrawCount) {
        withdrawCountNumberPicker.setMaxValue(Math.max(1, maxWithdrawCount));
    }

    @Override
    public void setInputComponentsEnabled(boolean enabled) {
        withdrawCountNumberPicker.setEnabled(enabled);
        phoneNumberEditText.setEnabled(enabled);
        withdrawButton.setEnabled(enabled);
    }

    @Override
    public void setPresenter(PointWithdrawContract.Presenter presenter) {
        this.presenter = presenter;
    }
}
