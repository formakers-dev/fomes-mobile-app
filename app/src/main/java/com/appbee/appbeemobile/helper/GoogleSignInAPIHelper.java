package com.appbee.appbeemobile.helper;

import android.content.Intent;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.api.GoogleApiClient;

public class GoogleSignInAPIHelper {
    public Intent requestSignInIntent(GoogleApiClient googleApiClient) {
        return Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
    }
    public GoogleSignInResult requestSignInResult(Intent data) {
        return Auth.GoogleSignInApi.getSignInResultFromIntent(data);
    }
}
