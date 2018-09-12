package com.formakers.fomes.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.Button;
import android.widget.EditText;

import com.formakers.fomes.AppBeeApplication;
import com.formakers.fomes.R;
import com.formakers.fomes.custom.AppBeeAlertDialog;
import com.formakers.fomes.helper.SharedPreferencesHelper;
import com.formakers.fomes.network.UserService;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;
import rx.android.schedulers.AndroidSchedulers;

public class CodeVerificationActivity extends BaseActivity {

    @Inject
    UserService userService;

    @Inject
    SharedPreferencesHelper SharedPreferencesHelper;

    @BindView(R.id.code_verification_edittext)
    EditText codeVerificationEdittext;

    @BindView(R.id.code_verification_button)
    Button codeVerificationButton;

    @OnClick(R.id.code_verification_button)
    public void onClickCodeVerificationButton() {
        String code = codeVerificationEdittext.getText().toString().trim();

        addToCompositeSubscription(
                userService.verifyInvitationCode(code)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(() -> {
                            SharedPreferencesHelper.setInvitationCode(code);
                            Intent intent = new Intent(this, LoginActivity.class);
                            startActivity(intent);
                            finish();
                        }, throwable -> {
                            AppBeeAlertDialog appBeeAlertDialog = new AppBeeAlertDialog(this, getString(R.string.invitation_code_dialog_title), getString(R.string.invitation_code_dialog_message), (dialog, which) -> dialog.dismiss());
                            appBeeAlertDialog.show();
                        })
        );
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
