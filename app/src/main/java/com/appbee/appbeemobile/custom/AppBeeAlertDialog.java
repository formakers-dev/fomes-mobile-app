package com.appbee.appbeemobile.custom;

import android.app.AlertDialog;
import android.content.Context;

import com.appbee.appbeemobile.R;

public class AppBeeAlertDialog extends AlertDialog {

    private final Context context;
    private final String title;
    private final String message;
    private OnClickListener positiveButtonOnClickListener = null;
    private OnClickListener negativeButtonOnClickListener = null;
    private AlertDialog alertDialog;

    public AppBeeAlertDialog(Context context, String title, String message, OnClickListener positiveButtonOnClickListener) {
        super(context);
        this.context = context;
        this.title = title;
        this.message = message;
        this.positiveButtonOnClickListener = positiveButtonOnClickListener;
        createDialog();
    }

    public AppBeeAlertDialog(Context context, String title, String message, OnClickListener positiveButtonOnClickListener, OnClickListener negativeButtonOnClickListener) {
        super(context);
        this.context = context;
        this.title = title;
        this.message = message;
        this.positiveButtonOnClickListener = positiveButtonOnClickListener;
        this.negativeButtonOnClickListener = negativeButtonOnClickListener;

        createDialog();
    }

    private void createDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(message);
        if (positiveButtonOnClickListener != null) {
            builder.setPositiveButton(R.string.confirm_button_name, positiveButtonOnClickListener);
        }
        if (negativeButtonOnClickListener != null) {
            builder.setNegativeButton(R.string.negative_button_name, negativeButtonOnClickListener);
        }
        alertDialog = builder.create();
    }

    public void show() {
        if (alertDialog != null) {
            alertDialog.show();
        }
    }
}
