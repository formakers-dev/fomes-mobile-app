package com.appbee.appbeemobile.custom;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

        LayoutInflater inflater = (LayoutInflater) context.getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.custom_alert_dialog, null);

        ImageView imageView = ((ImageView) view.findViewById(R.id.dialog_image));
        TextView titleTextView = ((TextView) view.findViewById(R.id.dialog_title));
        TextView messageTextView = ((TextView) view.findViewById(R.id.dialog_message));
        titleTextView.setTypeface(Typeface.createFromAsset(context.getAssets(), context.getString(R.string.type_face_bmjua)));
        titleTextView.setText(title);
        messageTextView.setText(message);
        if (resId != Integer.MIN_VALUE) {
            imageView.setImageResource(resId);
            imageView.setVisibility(View.VISIBLE);
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
            setButtonStyle();
        }
    }

    private void setButtonStyle() {
        // TODO : 버튼 두개인 경우 처리 필요
        Button positiveButton = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        positiveButton.setTypeface(Typeface.createFromAsset(context.getAssets(), context.getString(R.string.type_face_medium)));
        positiveButton.setTextSize(20);
        positiveButton.setTextColor(getColorValue(R.color.appbee_warm_gray));
        positiveButton.setLayoutParams(lp);
    }

    private
    @ColorInt
    int getColorValue(@ColorRes int colorResId) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return context.getResources().getColor(colorResId);
        } else {
            return context.getResources().getColor(colorResId, null);
        }
    }
}
