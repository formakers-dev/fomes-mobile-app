package com.formakers.fomes.common.helper;

import android.os.Bundle;

import com.formakers.fomes.common.util.Log;
import com.google.api.client.repackaged.org.apache.commons.codec.binary.Base64;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import rx.Single;

@Singleton
public class FomesUrlHelper {

    private static final String TAG = "FomesUrlHelper";

    public static final String EXTRA_BETA_TEST_ID = "BETA_TEST_ID";
    public static final String EXTRA_MISSION_ID = "MISSION_ID";

    public static final String COMMAND_EMAIL = "{email}";
    public static final String COMMAND_BETATEST_MISSION_IDS = "{b-m-ids}";

    public static final String SEPERATOR_BETATEST_MISSION_IDS = "$$";

    private Single<String> userEmail;

    @Inject
    public FomesUrlHelper(@Named("userEmail") Single<String> userEmail) {
        this.userEmail = userEmail;
    }

    public String interpretUrlParams(String originalUrl) {
        Log.v(TAG, "original=" + originalUrl);
        String interpretedUrl = originalUrl.replace(COMMAND_EMAIL, this.userEmail.toBlocking().value());
        return interpretedUrl;
    }

    public String interpretUrlParams(String originalUrl, Bundle params) {
        Log.v(TAG, "original=" + originalUrl);

        String betaTestId = params.getString(EXTRA_BETA_TEST_ID);
        String missionId = params.getString(EXTRA_MISSION_ID);

        String interpretedUrl = originalUrl.replace(COMMAND_BETATEST_MISSION_IDS,
                Base64.encodeBase64URLSafeString(Base64.encodeBase64URLSafe(betaTestId.getBytes()))
                        + SEPERATOR_BETATEST_MISSION_IDS
                        + Base64.encodeBase64URLSafeString(Base64.encodeBase64URLSafe(missionId.getBytes())));

        return interpretUrlParams(interpretedUrl);
    }
}
