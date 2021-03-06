package com.formakers.fomes.common.view;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
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
import androidx.viewpager2.widget.ViewPager2;

import com.formakers.fomes.FomesApplication;
import com.formakers.fomes.R;
import com.formakers.fomes.common.helper.ImageLoader;
import com.formakers.fomes.common.view.custom.adapter.NetworkImageViewPagerAdapter;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class FomesNoticeDialog extends DialogFragment {
    public static final String TAG = "FomesNoticeDialog";

    public static final String EXTRA_TITLE = "extra_title";
    public static final String EXTRA_SUBTITLE = "extra_subtitle";
    public static final String EXTRA_IMAGE_RES_ID = "extra_image_res_id";
    public static final String EXTRA_IMAGE_URL = "extra_image_url";
    public static final String EXTRA_IMAGE_URL_LIST = "extra_image_url_list";
    public static final String EXTRA_DESCRIPTION = "extra_description";

    @BindView(R.id.dialog_title) TextView titleTextView;
    @BindView(R.id.dialog_subtitle) TextView subTitleTextView;
    @BindView(R.id.dialog_image) ImageView imageView;
    @BindView(R.id.dialog_image_view_pager) ViewPager2 imageViewPager;
    @BindView(R.id.dialog_image_view_pager_indicator) TabLayout imageViewPagerIndicator;
    @BindView(R.id.dialog_message) TextView descriptionTextView;
    @BindView(R.id.dialog_positive_button) Button positiveButton;
    @BindView(R.id.dialog_neutral_button) Button neutralButton;
    @BindView(R.id.dialog_negative_button) Button negativeButton;

    private Unbinder unbinder;

    private String positiveButtonText;
    private View.OnClickListener positiveButtonClickListener;
    private String negativeButtonText;
    private View.OnClickListener negativeButtonClickListener;
    private String neutralButtonText;
    private View.OnClickListener neutralButtonClickListener;

    @Inject ImageLoader imageLoader;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getDialog().setCanceledOnTouchOutside(true);

        ((FomesApplication) this.getActivity().getApplication()).getComponent().inject(this);

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

        String title = bundle.getString(EXTRA_TITLE);
        String subtitle = bundle.getString(EXTRA_SUBTITLE);
        @DrawableRes int imageResId = bundle.getInt(EXTRA_IMAGE_RES_ID);
        String imageUrl = bundle.getString(EXTRA_IMAGE_URL);
        ArrayList<String> imageUrls = bundle.getStringArrayList(EXTRA_IMAGE_URL_LIST);
        String description = bundle.getString(EXTRA_DESCRIPTION);

        titleTextView.setText(title);
        subTitleTextView.setText(subtitle);

        if (imageUrls == null || imageUrls.size() <= 0) {
            imageViewPager.setVisibility(View.GONE);
            imageViewPagerIndicator.setVisibility(View.GONE);

            if (!TextUtils.isEmpty(imageUrl)) {
                imageLoader.loadImage(imageView, imageUrl);
            } else if (imageResId > 0) {
                imageView.setImageDrawable(getResources().getDrawable(imageResId, null));
            } else {
                imageView.setVisibility(View.GONE);
            }
        } else {
            imageViewPager.setAdapter(new NetworkImageViewPagerAdapter(imageLoader, imageUrls));
            new TabLayoutMediator(imageViewPagerIndicator, imageViewPager, (tab, position) -> {
            }).attach();
            imageView.setVisibility(View.GONE);
        }


        if (TextUtils.isEmpty(description)) {
            descriptionTextView.setVisibility(View.GONE);
        } else {
            descriptionTextView.setText(description);
            descriptionTextView.setVisibility(View.VISIBLE);
        }

        if (!TextUtils.isEmpty(positiveButtonText)) {
            positiveButton.setText(positiveButtonText);
            positiveButton.setOnClickListener(v -> {
                positiveButtonClickListener.onClick(v);
                dismiss();
            });
            positiveButton.setVisibility(View.VISIBLE);
        }

        if (!TextUtils.isEmpty(neutralButtonText)) {
            neutralButton.setText(neutralButtonText);
            neutralButton.setOnClickListener(v -> {
                neutralButtonClickListener.onClick(v);
                dismiss();
            });
            neutralButton.setVisibility(View.VISIBLE);
        }

        if (!TextUtils.isEmpty(negativeButtonText)) {
            negativeButton.setText(negativeButtonText);
            negativeButton.setOnClickListener(v -> {
                negativeButtonClickListener.onClick(v);
                dismiss();
            });
            negativeButton.setVisibility(View.VISIBLE);
        }
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

    public void setNegativeButton(String buttonText, View.OnClickListener clickListener) {
        this.negativeButtonText = buttonText;
        this.negativeButtonClickListener = clickListener;
    }

    public void setNeutralButton(String buttonText, View.OnClickListener clickListener) {
        this.neutralButtonText = buttonText;
        this.neutralButtonClickListener = clickListener;
    }
}
