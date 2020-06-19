package com.formakers.fomes.common.view;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;

import androidx.annotation.Nullable;

import com.formakers.fomes.betatest.FinishedBetaTestFragment;
import com.formakers.fomes.common.util.Log;
import com.formakers.fomes.betatest.BetaTestFragment;
import com.formakers.fomes.main.MainActivity;
import com.formakers.fomes.recommend.RecommendFragment;

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
                case "recommend": {
                    destIntent.setClass(this, MainActivity.class);
                    destIntent.putExtra("EXTRA_SELECTED_TAB", RecommendFragment.TAG);

                    break;
                }
                case "betatest": {
                    String id = uri.getQueryParameter("id");

                    destIntent.setClass(this, MainActivity.class);
                    destIntent.putExtra("EXTRA_SELECTED_TAB", BetaTestFragment.TAG);
                    destIntent.putExtra("EXTRA_SELECTED_ITEM_ID", id);
                    break;
                }
                case "finished-betatest": {
                    String id = uri.getQueryParameter("id");

                    destIntent.setClass(this, MainActivity.class);
                    destIntent.putExtra("EXTRA_SELECTED_TAB", FinishedBetaTestFragment.TAG);
                    destIntent.putExtra("EXTRA_SELECTED_ITEM_ID", id);
                    break;
                }
            }
        }

        return destIntent;
    }
}
