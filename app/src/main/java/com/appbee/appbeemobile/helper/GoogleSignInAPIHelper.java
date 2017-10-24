package com.appbee.appbeemobile.helper;

import android.content.Intent;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;

public class GoogleSignInAPIHelper {
    public Intent requestSignInIntent(GoogleApiClient googleApiClient) {
        return Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
    }
    public GoogleSignInResult requestSignInResult(Intent data) {
        return Auth.GoogleSignInApi.getSignInResultFromIntent(data);
    }
    public Person getCurrentPerson(GoogleApiClient googleApiClient) {
        return Plus.PeopleApi.getCurrentPerson(googleApiClient);
    }
}