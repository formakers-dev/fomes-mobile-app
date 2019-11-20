package com.formakers.fomes.common.view.custom;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.DrawableRes;

import com.formakers.fomes.R;
import com.formakers.fomes.common.helper.ResourceHelper;

public class FomesAlertDialog {

    private final Context context;
    private final String title;
    private final String message;
    private int iconResId = Integer.MIN_VALUE;
    private OnClickListener positiveButtonOnClickListener = null;
    private OnClickListener negativeButtonOnClickListener = null;
    private OnCancelListener onCancelListener = null;
    private AlertDialog alertDialog;

    public FomesAlertDialog(Context context, String title, String message, OnClickListener positiveButtonOnClickListener, OnClickListener negativeButtonOnClickListener) {
        this.context = context;
        this.title = title;
        this.message = message;
        this.positiveButtonOnClickListener = positiveButtonOnClickListener;
        this.negativeButtonOnClickListener = negativeButtonOnClickListener;
    }

    public FomesAlertDialog(Context context, String title, String message) {
        this(context, title, message, null, null);
    }

    public FomesAlertDialog(Context context, @DrawableRes int resId, String title, String message, OnClickListener positiveButtonOnClickListener) {
        this(context, title, message, positiveButtonOnClickListener, null);
        this.iconResId = resId;
    }

    public void setOnCancelListener(OnCancelListener onCancelListener) {
        this.onCancelListener = onCancelListener;
    }

    public void setPositiveButtonOnClickListener(OnClickListener listener) {
        this.positiveButtonOnClickListener = listener;
    }

    public void setNegativeButtonOnClickListener(OnClickListener listener) {
        this.negativeButtonOnClickListener = listener;
    }

    private void createDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        LayoutInflater inflater = (LayoutInflater) context.getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.custom_alert_dialog, null);

        ImageView imageView = ((ImageView) view.findViewById(R.id.dialog_image));
        TextView titleTextView = ((TextView) view.findViewById(R.id.dialog_title));
        TextView messageTextView = ((TextView) view.findViewById(R.id.dialog_message));
//        titleTextView.setTypeface(Typeface.createFromAsset(context.getAssets(), context.getString(R.string.font_bmjua_path)));
        titleTextView.setText(title);

        messageTextView.setText(message);
        if (iconResId != Integer.MIN_VALUE) {
            imageView.setImageResource(iconResId);
            imageView.setVisibility(View.VISIBLE);
        }

        builder.setView(view);

        if (positiveButtonOnClickListener != null) {
            builder.setPositiveButton(R.string.confirm_button_name, positiveButtonOnClickListener);
        }

        if (negativeButtonOnClickListener != null) {
            builder.setNegativeButton(R.string.negative_button_name, negativeButtonOnClickListener);
        }

        if (onCancelListener != null) {
            builder.setOnCancelListener(onCancelListener);
        }

        alertDialog = builder.create();
    }

    public void show() {
        createDialog();

        if (alertDialog != null) {
            alertDialog.show();
            setButtonStyle();
        }
    }

    private void setButtonStyle() {
        // TODO : 버튼 두개인 경우 처리 필요
        Button positiveButton = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        positiveButton.setTypeface(Typeface.createFromAsset(context.getAssets(), context.getString(R.string.font_medium_path)));
        positiveButton.setTextSize(20);
        positiveButton.setTextColor(new ResourceHelper(context).getColorValue(R.color.fomes_warm_gray));
        positiveButton.setLayoutParams(lp);
    }
}
