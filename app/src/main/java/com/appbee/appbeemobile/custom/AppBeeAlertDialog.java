package com.appbee.appbeemobile.custom;

import android.app.AlertDialog;
import android.content.Context;
import android.support.annotation.DrawableRes;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.appbee.appbeemobile.R;

public class AppBeeAlertDialog extends AlertDialog {

    private final Context context;
    private final String title;
    private final String message;
    private int resId = Integer.MIN_VALUE;
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

    public AppBeeAlertDialog(Context context, @DrawableRes int resId, String title, String message, OnClickListener positiveButtonOnClickListener) {
        super(context);
        this.context = context;
        this.resId = resId;
        this.title = title;
        this.message = message;
        this.positiveButtonOnClickListener = positiveButtonOnClickListener;

        createDialog();
    }

    private void createDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        LayoutInflater inflater = (LayoutInflater)context.getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.custom_alert_dialog, null);

        ImageView imageView = ((ImageView) view.findViewById(R.id.dialog_image));
        TextView titleTextViewe = ((TextView) view.findViewById(R.id.dialog_title));
        TextView messageTextViewe = ((TextView) view.findViewById(R.id.dialog_message));

        titleTextViewe.setText(title);
        messageTextViewe.setText(message);
        if(resId != Integer.MIN_VALUE) {
            imageView.setImageResource(resId);
        }

        builder.setView(view);

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
