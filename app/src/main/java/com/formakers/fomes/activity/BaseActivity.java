package com.formakers.fomes.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.tsengvn.typekit.TypekitContextWrapper;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import retrofit2.adapter.rxjava.HttpException;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

public abstract class BaseActivity extends AppCompatActivity {
    private Unbinder binder;

    final String TAG = "BaseActivity";

    final CompositeSubscription compositeSubscription = new CompositeSubscription();

    protected void addToCompositeSubscription(@NonNull final Subscription subscription) {
        compositeSubscription.add(subscription);
    }

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
        compositeSubscription.clear();

        if (binder != null) {
            binder.unbind();
        }

        super.onDestroy();
    }

    public void moveActivityTo(Class activityClass) {
        Intent intent = new Intent(this, activityClass);
        startActivity(intent);
        finish();
    }

    public void logError(Throwable error) {
        if (error instanceof HttpException) {
            Log.e(TAG, String.valueOf(((HttpException) error).code()));
        } else {
            Log.e(TAG, error.getMessage());
        }
    }
}
