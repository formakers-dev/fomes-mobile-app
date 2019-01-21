package com.formakers.fomes.common.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.formakers.fomes.FomesApplication;
import com.formakers.fomes.R;
import com.formakers.fomes.common.FomesConstants;
import com.formakers.fomes.helper.SharedPreferencesHelper;
import com.formakers.fomes.main.view.MainActivity;

import javax.inject.Inject;

public class NoticeMigrationActivity extends AppCompatActivity {

    @Inject SharedPreferencesHelper sharedPreferencesHelper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice_migration);

        ((FomesApplication) getApplication()).getComponent().inject(this);

        sharedPreferencesHelper.setOldLatestMigrationVersion(FomesConstants.MIGRATION_VERSION);

        this.findViewById(R.id.confirm_button).setOnClickListener(v -> this.onBackPressed());
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, MainActivity.class));

        super.onBackPressed();
    }
}
