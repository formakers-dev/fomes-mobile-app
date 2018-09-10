package com.formakers.fomes.provisioning.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.formakers.fomes.AppBeeApplication;
import com.formakers.fomes.R;
import com.formakers.fomes.activity.BaseActivity;
import com.formakers.fomes.dagger.ApplicationComponent;
import com.formakers.fomes.provisioning.contract.LoginContract;
import com.formakers.fomes.provisioning.presenter.LoginPresenter;

import butterknife.BindView;
import butterknife.OnClick;

public class LoginActivity extends BaseActivity implements LoginContract.View {

    public static final String TAG = LoginActivity.class.getSimpleName();

    private static final int REQUEST_CODE_SIGN_IN = 9001;

    @BindView(R.id.login_tnc) TextView loginTncTextView;

    LoginContract.Presenter presenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.setContentView(R.layout.activity_login);

        setPresenter(new LoginPresenter(this));
    }

    @Override
    public void setPresenter(LoginContract.Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public ApplicationComponent getApplicationComponent() {
        return ((AppBeeApplication) getApplication()).getComponent();
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
        Log.d(TAG, "requestCode=" + requestCode + " resultCode=" + resultCode + " data=" + data);
        if (requestCode == REQUEST_CODE_SIGN_IN) {
            if (resultCode != Activity.RESULT_OK) {
                Toast.makeText(this, "구글 로그인이 취소되었습니다.", Toast.LENGTH_LONG).show();
                return;
            }

            if (!this.presenter.requestSignUpBy(data)) {
                Toast.makeText(this, "구글 로그인에 실패하였습니다.", Toast.LENGTH_LONG).show();
            }
        }
    }

    @OnClick(R.id.login_google_button)
    public void onLoginButtonClick(View view) {
        Intent signInIntent = this.presenter.getGoogleSignInIntent();
        startActivityForResult(signInIntent, REQUEST_CODE_SIGN_IN);
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
}
