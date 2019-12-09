package com.formakers.fomes.betatest;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.formakers.fomes.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class FomesNoticeDialog extends DialogFragment {
    public static final String TAG = "FomesNoticeDialog";

    public static final String EXTRA_TITLE = "extra_title";
    public static final String EXTRA_SUBTITLE = "extra_subtitle";
    public static final String EXTRA_IMAGE_RES_ID = "extra_image_res_id";
    public static final String EXTRA_DESCRIPTION = "extra_description";

    @BindView(R.id.dialog_title) TextView titleTextView;
    @BindView(R.id.dialog_subtitle) TextView subTitleTextView;
    @BindView(R.id.dialog_image) ImageView imageView;
    @BindView(R.id.dialog_message) TextView descriptionTextView;
    @BindView(R.id.dialog_positive_button) Button postivieButton;

    private Unbinder unbinder;
    private String positiveButtonText;
    private View.OnClickListener positiveButtonClickListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getDialog().setCanceledOnTouchOutside(true);

        return getLayoutInflater().inflate(R.layout.dialog_notice_recheck_my_answer, container);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        unbinder = ButterKnife.bind(this, view);

        Bundle bundle = this.getArguments();
        if (bundle == null) {
            return;
        }
//
        String title = bundle.getString(EXTRA_TITLE);
        String subtitle = bundle.getString(EXTRA_SUBTITLE);
        @DrawableRes int imageResId = bundle.getInt(EXTRA_IMAGE_RES_ID);
        String description = bundle.getString(EXTRA_DESCRIPTION);

        titleTextView.setText(title);
        subTitleTextView.setText(subtitle);
        imageView.setImageDrawable(getResources().getDrawable(imageResId, null));
        descriptionTextView.setText(description);

        postivieButton.setText(positiveButtonText);
        postivieButton.setOnClickListener(v -> {
            positiveButtonClickListener.onClick(v);
            dismiss();
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        WindowManager.LayoutParams params = getDialog().getWindow().getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        getDialog().getWindow().setAttributes(params);
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
//        Fragment fragment = getTargetFragment();
//        if (fragment != null) {
//            fragment.onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, null);
//        }

        super.onDismiss(dialog);
    }

    @Override
    public void onDestroyView() {
        if (unbinder != null) {
            unbinder.unbind();
        }

        super.onDestroyView();
    }

    public void setPositiveButton(String buttonText, View.OnClickListener clickListener) {
        this.positiveButtonText = buttonText;
        this.positiveButtonClickListener = clickListener;
    }
}
