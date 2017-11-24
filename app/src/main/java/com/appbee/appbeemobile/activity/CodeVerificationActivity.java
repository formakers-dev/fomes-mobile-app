package com.appbee.appbeemobile.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.Button;
import android.widget.EditText;

import com.appbee.appbeemobile.AppBeeApplication;
import com.appbee.appbeemobile.R;
import com.appbee.appbeemobile.network.UserService;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;
import rx.android.schedulers.AndroidSchedulers;

public class CodeVerificationActivity extends BaseActivity {

    @Inject
    UserService userService;

    @BindView(R.id.code_verification_edittext)
    EditText codeVerificationEdittext;

    @BindView(R.id.code_verification_button)
    Button codeVerificationButton;

    @OnClick(R.id.code_verification_button)
    public void onClickCodeVerificationButton() {
        userService.verifyRegistrationCode(codeVerificationEdittext.getText().toString())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> {
                    Intent intent = new Intent(this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }, throwable -> {
                    AlertDialog.Builder warnDialogBuilder = new AlertDialog.Builder(this);
                    warnDialogBuilder.setTitle("초대장코드오류");
                    warnDialogBuilder.setMessage("초대 코드를 다시 확인해주세요.");
                    warnDialogBuilder.setPositiveButton("확인", (dialog, which) -> dialog.dismiss());
                    warnDialogBuilder.create().show();
                });
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ((AppBeeApplication) getApplication()).getComponent().inject(this);

        this.setContentView(R.layout.activity_code_verification);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }
}
