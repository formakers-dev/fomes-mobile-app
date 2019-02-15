package com.formakers.fomes.common.view;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.formakers.fomes.common.util.Log;
import com.formakers.fomes.main.view.MainActivity;

public class DeeplinkActivity extends Activity {

    public static final String TAG = "DeeplinkActivity";

    public static final String SCHEME_FOMES = "fomes";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        Intent intent = getIntent();

        if (intent == null) {
            return;
        }

        Uri uri = intent.getData();

        if (uri == null) {
            Log.d(TAG, "Uri is null.");
            return;
        }

        Log.i(TAG, "uri=" + uri);
        startActivity(getIntentFromDeeplink(intent, uri));
        finish();
    }

    // Util로 따로 뺄까...?
    private Intent getIntentFromDeeplink(Intent intent, Uri uri) {
        String host = uri.getHost();

        Intent destIntent = new Intent();

        if ("launch".equals(host)) {
            String action = uri.getQueryParameter("action");

            if (TextUtils.isEmpty(action)) {
                throw new IllegalArgumentException("action is null");
            }

            switch (action) {
                case "main": {
                    destIntent.setClass(this, MainActivity.class);
                    break;
                }
            }
        }

        return destIntent;
    }
}
