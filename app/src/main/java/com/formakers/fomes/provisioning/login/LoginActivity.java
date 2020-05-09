package com.formakers.fomes.provisioning.login;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.formakers.fomes.FomesApplication;
import com.formakers.fomes.R;
import com.formakers.fomes.common.util.Log;
import com.formakers.fomes.common.view.BaseActivity;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;
import rx.android.schedulers.AndroidSchedulers;

public class LoginActivity extends BaseActivity implements LoginContract.View {

    public static final String TAG = "LoginActivity";

    private static final int REQUEST_CODE_SIGN_IN = 9001;

    @BindView(R.id.login_tnc) TextView loginTncTextView;
    @BindView(R.id.login_google_button) Button loginButton;

    @Inject LoginContract.Presenter presenter;

    @Override
    public void setPresenter(LoginContract.Presenter presenter) {
        if (this.presenter == null) {
            this.presenter = presenter;
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DaggerLoginDagger_Component.builder()
                .applicationComponent(FomesApplication.get(this).getComponent())
                .module(new LoginDagger.Module(this))
                .build()
                .inject(this);

        this.setContentView(R.layout.activity_login);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        loginTncTextView.setText(Html.fromHtml(getString(R.string.login_tnc)));
        loginTncTextView.setMovementMethod(LinkMovementMethod.getInstance());

        if (presenter.isProvisioningProgress()) {
            this.showLoginButton();
        } else {
            addToCompositeSubscription(
                    presenter.googleSilentSignIn()
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(googleSignInResult -> {
                                this.presenter.signUpOrSignIn(googleSignInResult);
                            }, e -> this.showLoginButton())
            );
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "requestCode=" + requestCode + " resultCode=" + resultCode + " data=" + data);

        if (requestCode == REQUEST_CODE_SIGN_IN) {
            if (resultCode != Activity.RESULT_OK) {
                showToast("구글 로그인이 취소되었습니다.");
                return;
            }

            GoogleSignInResult googleSignInResult = this.presenter.convertGoogleSignInResult(data);
            if (googleSignInResult == null) {
                showToast("구글 로그인에 실패하였습니다.");
                return;
            }

            this.presenter.signUpOrSignIn(googleSignInResult);
        }
    }

    @Override
    protected void onDestroy() {
        presenter.unsubscribe();
        super.onDestroy();
    }

    @Override
    public void showToast(String toastMessage) {
        Toast.makeText(this, toastMessage, Toast.LENGTH_LONG).show();
    }

    @Override
    public void startActivityAndFinish(Class<?> destActivity) {
        Intent intent = new Intent(this, destActivity);
        this.startActivity(intent);
        this.finish();
    }

    @Override
    public void showLoginButton() {
        loginButton.setVisibility(View.VISIBLE);
        loginTncTextView.setVisibility(View.VISIBLE);
    }

    @OnClick(R.id.login_google_button)
    public void onLoginButtonClick(View view) {
        Intent signInIntent = this.presenter.getGoogleSignInIntent();
        startActivityForResult(signInIntent, REQUEST_CODE_SIGN_IN);
    }
}
