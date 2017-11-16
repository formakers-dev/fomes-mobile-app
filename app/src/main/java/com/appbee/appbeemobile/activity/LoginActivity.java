package com.appbee.appbeemobile.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.appbee.appbeemobile.AppBeeApplication;
import com.appbee.appbeemobile.R;
import com.appbee.appbeemobile.helper.GoogleSignInAPIHelper;
import com.appbee.appbeemobile.helper.LocalStorageHelper;
import com.appbee.appbeemobile.network.UserService;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.api.services.people.v1.model.Person;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class LoginActivity extends BaseActivity {

    private static final String TAG = LoginActivity.class.getSimpleName();
    private static final int RC_SIGN_IN = 9001;
    private static GoogleApiClient mGoogleApiClient;

    @Inject
    UserService userService;

    @Inject
    GoogleSignInAPIHelper googleSignInAPIHelper;

    @Inject
    Context context;

    @Inject
    LocalStorageHelper localStorageHelper;

    @BindView(R.id.tnc_title_text)
    TextView tncAgreeTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ((AppBeeApplication) getApplication()).getComponent().inject(this);

        this.setContentView(R.layout.activity_login);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        tncAgreeTextView.setText(Html.fromHtml(getString(R.string.tnc_agree_text)));
        tncAgreeTextView.setMovementMethod(LinkMovementMethod.getInstance());
    }

    private void signIn() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        Intent signInIntent = googleSignInAPIHelper.requestSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = googleSignInAPIHelper.requestSignInResult(data);
            GoogleSignInAccount account = result.getSignInAccount();

            if (resultCode == Activity.RESULT_OK && result.isSuccess() && account != null) {
                googleSignInAPIHelper.getPerson(account).subscribeOn(Schedulers.io())
                        .subscribe(person -> signInUser(account.getIdToken(), account.getId(), account.getEmail(), person),
                                e -> finishActivityForFail(R.string.fail_to_connect_google_play));
            } else {
                finishActivityForFail(R.string.fail_to_connect_google_play);
            }
        }
    }

    void signInUser(final String googleIdToken, final String googleUserId, final String email, final Person person) {
        userService.signIn(googleIdToken).observeOn(AndroidSchedulers.mainThread())
                .subscribe(token -> {
                    Log.d(TAG, "signInUser success");
                    localStorageHelper.setAccessToken(token);
                    localStorageHelper.setUserId(googleUserId);
                    localStorageHelper.setEmail(email);

                    if (person != null) {
                        localStorageHelper.setBirthday(person.getBirthdays() != null ? person.getBirthdays().get(0).getDate().getYear() : 0);
                        localStorageHelper.setGender(person.getGenders() != null ? person.getGenders().get(0).getValue() : "");
                    }

                    Intent intent = new Intent(getBaseContext(), OnboardingActivity.class);
                    startActivity(intent);
                    setResult(Activity.RESULT_OK);
                    finish();
                }, e -> {
                    Log.d(TAG, "signInUser Failed e=" + e.getMessage() + ", cause=" + e.getCause());
                    finishActivityForFail(R.string.fail_to_sign_in);
                });
    }

    void finishActivityForFail(@StringRes int failStringId) {
        Toast.makeText(this, failStringId, Toast.LENGTH_SHORT).show();
        setResult(Activity.RESULT_CANCELED);
        finish();
    }

    @OnClick(R.id.login_button)
    void onLoginButtonClick() {
        signIn();
    }
}

