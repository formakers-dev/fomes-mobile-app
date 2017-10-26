package com.appbee.appbeemobile.helper;

import android.content.Context;
import android.content.Intent;

import com.appbee.appbeemobile.R;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.people.v1.PeopleService;
import com.google.api.services.people.v1.model.Person;

import java.io.IOException;
import java.util.Collections;

import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Observable;

@Singleton
public class GoogleSignInAPIHelper {

    private static HttpTransport HTTP_TRANSPORT = AndroidHttp.newCompatibleTransport();
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

    private Context context;

    @Inject
    public GoogleSignInAPIHelper(Context context) {
        this.context = context;
    }

    public Intent requestSignInIntent(GoogleApiClient googleApiClient) {
        return Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
    }
    public GoogleSignInResult requestSignInResult(Intent data) {
        return Auth.GoogleSignInApi.getSignInResultFromIntent(data);
    }

    public Observable<Person> getPerson(final GoogleSignInAccount account) {
        return Observable.create(emitter -> {
            try {
                GoogleAccountCredential credential = GoogleAccountCredential.usingOAuth2(context, Collections.singleton(""))
                        .setSelectedAccount(account.getAccount());

                Person person = new PeopleService.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential)
                        .setApplicationName(context.getString(R.string.app_name))
                        .build()
                        .people()
                        .get("people/me")
                        .setPersonFields("birthdays,genders")
                        .execute();

                emitter.onNext(person);
                emitter.onCompleted();
            } catch (IOException e) {
                e.printStackTrace();
                emitter.onError(e);
            }
        });
    }
}