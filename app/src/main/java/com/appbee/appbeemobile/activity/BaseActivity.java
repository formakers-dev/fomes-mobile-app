package com.appbee.appbeemobile.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.tsengvn.typekit.TypekitContextWrapper;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import retrofit2.adapter.rxjava.HttpException;

public class BaseActivity extends AppCompatActivity {
    private Unbinder binder;

    final String TAG = BaseActivity.class.getSimpleName();

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(TypekitContextWrapper.wrap(newBase));
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        binder = ButterKnife.bind(this);
    }

    @Override
    protected void onDestroy() {
        if (binder != null) {
            binder.unbind();
        }
        super.onDestroy();
    }

    public void logError(Throwable error) {
        if (error instanceof HttpException) {
            Log.e(TAG, String.valueOf(((HttpException) error).code()));
        } else {
            Log.e(TAG, error.getMessage());
        }
    }
}
