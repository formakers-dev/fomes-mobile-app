package com.formakers.fomes.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.Toast;

import com.formakers.fomes.AppBeeApplication;
import com.formakers.fomes.BuildConfig;
import com.formakers.fomes.R;
import com.formakers.fomes.common.job.JobManager;
import com.formakers.fomes.custom.AppBeeAlertDialog;
import com.formakers.fomes.helper.AppBeeAndroidNativeHelper;
import com.formakers.fomes.helper.SharedPreferencesHelper;
import com.formakers.fomes.network.ConfigService;
import com.formakers.fomes.network.UserService;
import com.formakers.fomes.util.AppBeeConstants;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;
import retrofit2.adapter.rxjava.HttpException;
import rx.android.schedulers.AndroidSchedulers;

public class PermissionGuideActivity extends BaseActivity {

    private static final String TAG = "PermissionGuideActivity";
    static final int REQUEST_CODE_PACKAGE_USAGE_STATS_PERMISSION = 1001;

    @Inject
    AppBeeAndroidNativeHelper appBeeAndroidNativeHelper;

    @Inject
    SharedPreferencesHelper SharedPreferencesHelper;

    @Inject
    ConfigService configService;

    @Inject
    UserService userService;

    @Inject
    JobManager jobManager;

    @BindView(R.id.permission_button)
    Button permissionButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ((AppBeeApplication) getApplication()).getComponent().inject(this);
        setContentView(R.layout.activity_permission_guide);

        if (BuildConfig.VERSION_CODE < configService.getAppVersion().toBlocking().value()) {
            displayVersionUpdateDialog();
            return;
        }

        // TODO : 초기상태 - 인증완료 상태 패턴 적용하여 관리하는 방안 고려
        if (TextUtils.isEmpty(SharedPreferencesHelper.getInvitationCode())) {
            moveActivityTo(CodeVerificationActivity.class);
            return;
        }

        if (!SharedPreferencesHelper.isLoggedIn()) {
            moveActivityTo(LoginActivity.class);
            return;
        }

        if (appBeeAndroidNativeHelper.hasUsageStatsPermission()) {
            checkTokenAndMoveActivity();
        }
    }

    private void checkTokenAndMoveActivity() {
        userService.verifyToken()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> {
                    String notification = getIntent().getStringExtra(AppBeeConstants.EXTRA.NOTIFICATION_TYPE);
                    if ("확정".equals(notification)) {
                        moveActivityTo(MyInterviewActivity.class);
                    } else if (SharedPreferencesHelper.getLastUpdateAppUsageTimestamp() == 0L) {
                        moveActivityTo(LoadingActivity.class);
                    } else {
                        moveActivityTo(MainActivity.class);
                    }
                }, error -> {
                    if (error instanceof HttpException) {
                        int code = ((HttpException) error).code();
                        if (code == 401 || code == 403) {
                            moveActivityTo(LoginActivity.class);
                            return;
                        }
                    }

                    Toast.makeText(this, R.string.unexpected_error_message, Toast.LENGTH_LONG).show();
                    finishAffinity();
                });
    }

    private void displayVersionUpdateDialog() {
        AppBeeAlertDialog appBeeAlertDialog = new AppBeeAlertDialog(this, getString(R.string.app_update_dialog_title), getString(R.string.app_update_dialog_message), (dialog, which) -> moveToPlayStore());
        appBeeAlertDialog.setOnCancelListener(dialog -> finishAffinity());
        appBeeAlertDialog.show();
    }

    private void moveToPlayStore() {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("market://details?id=com.formakers.fomes"));
        startActivity(intent);
        finishAffinity();
    }

    @OnClick(R.id.permission_button)
    void onPermissionButtonClick() {
        Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
        startActivityForResult(intent, REQUEST_CODE_PACKAGE_USAGE_STATS_PERMISSION);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_PACKAGE_USAGE_STATS_PERMISSION
                && appBeeAndroidNativeHelper.hasUsageStatsPermission()) {
            checkTokenAndMoveActivity();
            jobManager.registerSendDataJob(JobManager.JOB_ID_SEND_DATA);
        }
    }
}
