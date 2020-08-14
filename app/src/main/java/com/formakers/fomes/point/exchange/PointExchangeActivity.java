package com.formakers.fomes.point.exchange;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.formakers.fomes.FomesApplication;
import com.formakers.fomes.R;
import com.formakers.fomes.common.util.StringFormatUtil;
import com.formakers.fomes.common.view.FomesBaseActivity;
import com.shawnlin.numberpicker.NumberPicker;

import java.text.NumberFormat;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnTextChanged;

public class PointExchangeActivity extends FomesBaseActivity implements PointExchangeContract.View {
    private static final String TAG = "PointExchangeActivity";

    @BindView(R.id.exchange_count) NumberPicker exchangeCountNumberPicker;
    @BindView(R.id.exchange_phone_number) EditText phoneNumberEditText;
    @BindView(R.id.phone_number_format_warning_textview) TextView phoneNumberFormatWarningTextView;
    @BindView(R.id.my_available_point) TextView availablePointTextView;
    @BindView(R.id.exchange_button) Button exchangeButton;

    @Inject PointExchangeContract.Presenter presenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_point_exchange);

        getSupportActionBar().setTitle("문화상품권 교환 신청");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        DaggerPointExchangeDagger_Component.builder()
                .applicationComponent(FomesApplication.get(this).getComponent())
                .module(new PointExchangeDagger.Module(this))
                .build()
                .inject(this);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // bind View
        this.presenter.bindAvailablePoint();

        this.exchangeButton.setOnClickListener(v -> {
            this.presenter.exchange(this.exchangeCountNumberPicker.getValue(),
                    String.valueOf(this.phoneNumberEditText.getText()));
        });
    }

    @OnTextChanged(value = R.id.exchange_phone_number, callback = OnTextChanged.Callback.TEXT_CHANGED)
    public void onPhoneNumberTextChanged(CharSequence text, int start, int before, int count) {
        if (StringFormatUtil.verifyPhoneNumberFormat(String.valueOf(text))) {
            phoneNumberFormatWarningTextView.setVisibility(View.GONE);
        } else {
            phoneNumberFormatWarningTextView.setVisibility(View.VISIBLE);
        }

        changeExchangeStatusView();
    }

    @Override
    public void setAvailablePoint(long point) {
        availablePointTextView.setText(String.format("%s P", NumberFormat.getInstance().format(point)));

        availablePointTextView.startAnimation(getFadeInAnimation(300));
    }

    @Override
    public void setMaxExchangeCount(int maxExchangeCount) {
        exchangeCountNumberPicker.setMaxValue(Math.max(1, maxExchangeCount));
    }

    @Override
    public void setInputComponentsEnabled(boolean enabled) {
        exchangeCountNumberPicker.setEnabled(enabled);
        phoneNumberEditText.setEnabled(enabled);
        changeExchangeStatusView();
    }

    @Override
    public void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void setPresenter(PointExchangeContract.Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void successfullyFinish() {
        this.setResult(Activity.RESULT_OK);
        this.finish();
    }

    private void changeExchangeStatusView() {
        exchangeButton.setEnabled(this.presenter.isAvailableToExchange(this.exchangeCountNumberPicker.getValue(), String.valueOf(this.phoneNumberEditText.getText())));
    }
}
