package com.formakers.fomes.common.view;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.formakers.fomes.AppBeeApplication;
import com.formakers.fomes.BuildConfig;
import com.formakers.fomes.R;
import com.formakers.fomes.appbee.custom.AppBeeAlertDialog;
import com.formakers.fomes.common.network.ConfigService;
import com.tsengvn.typekit.TypekitContextWrapper;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import retrofit2.adapter.rxjava.HttpException;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.subscriptions.CompositeSubscription;

public class BaseActivity extends AppCompatActivity {

    @Inject ConfigService configService;

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
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((AppBeeApplication) getApplication()).getComponent().inject(this);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        binder = ButterKnife.bind(this);

        checkUpdatePopup();
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

    private void checkUpdatePopup() {
        configService.getAppVersion()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(version -> {
                    if (BuildConfig.VERSION_CODE < version) {
                        displayVersionUpdateDialog();
                    }
                }, e -> Log.e(TAG, String.valueOf(e)));
    }

    private void displayVersionUpdateDialog() {
        AppBeeAlertDialog appBeeAlertDialog = new AppBeeAlertDialog(this, getString(R.string.update_dialog_title),
                getString(R.string.update_dialog_message), (dialog, which) -> {
            moveToPlayStore();
            dialog.dismiss();
        });
        appBeeAlertDialog.setOnCancelListener(dialog -> {
//            finishAffinity();
            dialog.dismiss();
        });
        appBeeAlertDialog.show();
    }

    private void moveToPlayStore() {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("market://details?id=com.formakers.fomes"));
        startActivity(intent);
//        finishAffinity();
    }
}