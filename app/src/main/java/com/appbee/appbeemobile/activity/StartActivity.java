package com.appbee.appbeemobile.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;

import com.appbee.appbeemobile.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class StartActivity extends AppCompatActivity {

    @BindView(R.id.start_analysis_button)
    Button startButton;

    private Unbinder binder;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_start);

        binder = ButterKnife.bind(this);

        startButton.setOnClickListener(v -> {
            Intent intent = new Intent(StartActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });
    }

    @Override
    protected void onDestroy() {
        binder.unbind();
        super.onDestroy();
    }
}
