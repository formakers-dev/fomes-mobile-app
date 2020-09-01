package com.formakers.fomes.common.view;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Html;
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

public class FomesCharacterDialog extends DialogFragment {
    public static final String TAG = "FomesCharacterDialog";

    public static final String EXTRA_TITLE = "extra_title";
    public static final String EXTRA_SUBTITLE = "extra_subtitle";
    public static final String EXTRA_IMAGE_RES_ID = "extra_image_res_id";
    public static final String EXTRA_BUTTON_TEXT = "extra_button_text";

    @BindView(R.id.dialog_title) TextView titleTextView;
    @BindView(R.id.dialog_subtitle) TextView subTitleTextView;
    @BindView(R.id.dialog_image) ImageView imageView;
    @BindView(R.id.dialog_button) Button okButton;

    private View.OnClickListener positiveButtonClickListener;

    private Unbinder unbinder;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getDialog().setCanceledOnTouchOutside(true);
        getDialog().getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        getDialog().getWindow().getAttributes().windowAnimations = R.style.SlideAnimation;

        return getLayoutInflater().inflate(R.layout.dialog_fomes_character, container);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        unbinder = ButterKnife.bind(this, view);

        Bundle bundle = this.getArguments();
        if (bundle == null) {
            return;
        }

        String title = bundle.getString(EXTRA_TITLE);
        String subTitle = bundle.getString(EXTRA_SUBTITLE);
        @DrawableRes int imageRestId = bundle.getInt(EXTRA_IMAGE_RES_ID);
        String buttonText = bundle.getString(EXTRA_BUTTON_TEXT);

        titleTextView.setText(title);
        subTitleTextView.setText(Html.fromHtml(subTitle.replace("\n", "<br>")));
        imageView.setImageResource(imageRestId);
        okButton.setText(buttonText);

        okButton.setOnClickListener(this::onOkClickButton);
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

    public void setPositiveButtonClickListener(View.OnClickListener positiveButtonClickListener) {
        this.positiveButtonClickListener = positiveButtonClickListener;
    }

    private void onOkClickButton(View v) {
        if (this.positiveButtonClickListener != null) {
            this.positiveButtonClickListener.onClick(v);
        }
        this.dismiss();
    }
}
