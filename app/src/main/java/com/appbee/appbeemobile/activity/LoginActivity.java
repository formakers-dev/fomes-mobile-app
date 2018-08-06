package com.appbee.appbeemobile.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.text.Html;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.appbee.appbeemobile.AppBeeApplication;
import com.appbee.appbeemobile.R;
import com.appbee.appbeemobile.helper.GoogleSignInAPIHelper;
import com.appbee.appbeemobile.helper.LocalStorageHelper;
import com.appbee.appbeemobile.model.User;
import com.appbee.appbeemobile.network.UserService;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.api.services.people.v1.model.Person;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class LoginActivity extends BaseActivity {
    private static final String TAG = "LoginActivity";
    private static final String UNKNOWN_GENDER = "unknown";
    private static final int RC_SIGN_IN = 9001;

    @Inject
    UserService userService;

    @Inject
    GoogleSignInAPIHelper googleSignInAPIHelper;

    @Inject
    LocalStorageHelper localStorageHelper;

    @BindView(R.id.tnc_title_text)
    TextView tncAgreeTextView;

    @BindView(R.id.login_button_layout)
    View loginButtonLayout;

    private boolean isSilentSignedIn = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ((AppBeeApplication) getApplication()).getComponent().inject(this);

        setContentView(R.layout.activity_login);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        tncAgreeTextView.setText(Html.fromHtml(getString(R.string.tnc_agree_text)));
        tncAgreeTextView.setMovementMethod(LinkMovementMethod.getInstance());

        silentSignIn();
    }

    private void silentSignIn() {
        googleSignInAPIHelper.requestSilentSignInResult()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(googleSignInResult -> {
                    if (googleSignInResult != null && googleSignInResult.isSuccess()) {
                        isSilentSignedIn = true;
                        verifyGoogleSignInResult(googleSignInResult);
                    } else {
                        isSilentSignedIn = false;
                        loginButtonLayout.setVisibility(View.VISIBLE);
                    }
                });
    }

    @OnClick(R.id.login_button)
    void onLoginButtonClick() {
        signIn();
    }

    private void signIn() {
        Intent signInIntent = googleSignInAPIHelper.requestSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            if (resultCode == Activity.RESULT_CANCELED) {
                return;
            }

            if (resultCode == Activity.RESULT_OK) {
                GoogleSignInResult result = googleSignInAPIHelper.requestSignInResult(data);
                verifyGoogleSignInResult(result);
            } else {
                finishActivityForFail(R.string.fail_to_connect_google_play);
            }
        }
    }

    private void verifyGoogleSignInResult(GoogleSignInResult result) {
        GoogleSignInAccount account = result.getSignInAccount();
        if (result.isSuccess() && account != null) {
            addToCompositeSubscription(
                    googleSignInAPIHelper.getPerson(account)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(person -> signInUser(account.getIdToken(), account.getId(), account.getEmail(), person),
                                    e -> signInUser(account.getIdToken(), account.getId(), account.getEmail(), null))
            );
        } else {
            finishActivityForFail(R.string.fail_to_connect_google_play);
        }
    }

    private void signInUser(final String googleIdToken, final String googleUserId, final String email, final Person person) {
        final String userId = googleSignInAPIHelper.getProvider() + googleUserId;

        final User user = new User(userId, email, localStorageHelper.getRegistrationToken());

        if (person != null) {
            user.setBirthday(person.getBirthdays() != null ? person.getBirthdays().get(0).getDate().getYear() : 0);
            user.setGender(person.getGenders() != null ? person.getGenders().get(0).getValue() : UNKNOWN_GENDER);
        } else {
            user.setBirthday(0);
            user.setGender(UNKNOWN_GENDER);
        }

        String signUpCode = localStorageHelper.getInvitationCode();
        if (!TextUtils.isEmpty(signUpCode)) {
            user.setSignUpCode(new User.SignUpCode(User.SignUpCode.BETA, signUpCode));
        }

        addToCompositeSubscription(
                userService.signIn(googleIdToken, user)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(accessToken -> {
                            localStorageHelper.setAccessToken(accessToken);
                            localStorageHelper.setUserId(userId);
                            localStorageHelper.setEmail(email);

                            moveToNextActivity();
                        }, e -> {
                            Log.e(TAG, "signInUser Failed");
                            finishActivityForFail(R.string.fail_to_sign_in);
                        })
        );
    }

    private void moveToNextActivity() {
        Intent intent;

        if (isSilentSignedIn) {
            intent = new Intent(getBaseContext(), MainActivity.class);
        } else {
            intent = new Intent(getBaseContext(), OnboardingActivity.class);
        }

        startActivity(intent);
        setResult(Activity.RESULT_OK);
        finish();
    }

    void finishActivityForFail(@StringRes int failStringId) {
        Toast.makeText(LoginActivity.this, failStringId, Toast.LENGTH_SHORT).show();
        setResult(Activity.RESULT_CANCELED);
        finish();
    }
}
