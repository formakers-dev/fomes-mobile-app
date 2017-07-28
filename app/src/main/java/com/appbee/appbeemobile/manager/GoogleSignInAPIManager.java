package com.appbee.appbeemobile.manager;

import android.content.Intent;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.api.GoogleApiClient;

public class GoogleSignInAPIManager {
    public Intent getSignInIntent(GoogleApiClient googleApiClient) {
        return Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
    }
    public GoogleSignInResult getSignInResult(Intent data) {
        return Auth.GoogleSignInApi.getSignInResultFromIntent(data);
    }
}
