package com.formakers.fomes.provisioning;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.formakers.fomes.AppBeeApplication;
import com.formakers.fomes.R;
import com.formakers.fomes.activity.BaseActivity;
import com.formakers.fomes.helper.GoogleSignInAPIHelper;
import com.formakers.fomes.helper.LocalStorageHelper;
import com.formakers.fomes.model.User;
import com.formakers.fomes.network.UserService;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;
import rx.android.schedulers.AndroidSchedulers;

public class LoginActivity extends BaseActivity {

    private static final int REQUEST_CODE_SIGN_IN = 9001;

    @BindView(R.id.login_tnc) TextView loginTncTextView;

    @Inject GoogleSignInAPIHelper googleSignInAPIHelper;
    @Inject UserService userService;
    @Inject LocalStorageHelper localStorageHelper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ((AppBeeApplication) getApplication()).getComponent().inject(this);

        this.setContentView(R.layout.activity_login);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        loginTncTextView.setText(Html.fromHtml(getString(R.string.login_tnc)));
        loginTncTextView.setMovementMethod(LinkMovementMethod.getInstance());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_SIGN_IN) {
            if (resultCode != Activity.RESULT_OK) {
                Toast.makeText(this, "구글 로그인에 실패하였습니다.", Toast.LENGTH_LONG).show();
                return;
            }

            GoogleSignInResult result = googleSignInAPIHelper.getSignInResult(data);
            if (result == null || !result.isSuccess()) {
                Toast.makeText(this, "구글 로그인에 실패하였습니다.", Toast.LENGTH_LONG).show();
                return;
            }

            GoogleSignInAccount account = result.getSignInAccount();
            userService.signIn(account.getIdToken(), new User(account.getId(), account.getEmail(), localStorageHelper.getRegistrationToken()))
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(fomesToken -> {
                        // TODO : 리팩토링 고려 필요
                        localStorageHelper.setAccessToken(fomesToken);
                        localStorageHelper.setUserId(account.getId());
                        localStorageHelper.setEmail(account.getEmail());
                    }, e -> Toast.makeText(this, "가입에 실패하였습니다. 재시도 고고", Toast.LENGTH_LONG).show());
        }
    }

    @OnClick(R.id.login_google_button)
    public void onLoginButtonClick(View view) {
        Intent signInIntent = googleSignInAPIHelper.getSignInIntent();
        startActivityForResult(signInIntent, REQUEST_CODE_SIGN_IN);
    }
}
