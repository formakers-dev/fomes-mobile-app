package com.formakers.fomes.main.view;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.formakers.fomes.R;
import com.formakers.fomes.common.FomesConstants;
import com.formakers.fomes.common.network.vo.BetaTest;
import com.formakers.fomes.common.util.DateUtil;
import com.formakers.fomes.common.util.Log;
import com.formakers.fomes.main.contract.BetaTestContract;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class BetaTestDetailAlertDialog extends DialogFragment {

    public static final String TAG = "BetaTestDetailAlertDialog";

    @BindView(R.id.betatest_detail_imageview)       ImageView overviewImageView;
    @BindView(R.id.betatest_detail_title_textview)  TextView titleTextView;
    @BindView(R.id.betatest_detail_target)          TextView targetTextView;
    @BindView(R.id.betatest_detail_test_type)       TextView testTypeTextView;
    @BindView(R.id.betatest_detail_project_status)  TextView projectStatusTextView;
    @BindView(R.id.betatest_detail_required_time)   TextView requiredTimeTextView;
    @BindView(R.id.betatest_detail_amount)          TextView amountTextView;
    @BindView(R.id.betatest_detail_reward)          TextView rewardTextView;
    @BindView(R.id.join_button)                     Button   joinButton;


    private Unbinder unbinder;

    private BetaTestContract.Presenter presenter;

    public void setPresenter(BetaTestContract.Presenter presenter) {
        this.presenter = presenter;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return getLayoutInflater().inflate(R.layout.dialog_beta_test_detail, container);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        unbinder = ButterKnife.bind(this, view);

        Bundle bundle = this.getArguments();
        if (bundle == null) {
            return;
        }

        BetaTest betaTest = bundle.getParcelable(FomesConstants.BetaTest.EXTRA_BETA_TEST);
        String userEmail = bundle.getString(FomesConstants.BetaTest.EXTRA_USER_EMAIL);

        Log.v(TAG, "BetaTest=" + betaTest);
        Log.v(TAG, "User Email=" + userEmail);

        if (betaTest == null) {
            return;
        }

        this.presenter.getAnalytics().setCurrentScreen(this);

        Glide.with(view.getContext()).load(betaTest.getOverviewImageUrl())
                .apply(new RequestOptions().override(219, 219)
                        .centerCrop()
                        .transform(new RoundedCorners(10))
                        .placeholder(new ColorDrawable(view.getResources().getColor(R.color.fomes_deep_gray))))
                .into(overviewImageView);

        titleTextView.setText(betaTest.getTitle());

        List<String> targetApps = betaTest.getApps();

        if (targetApps == null || targetApps.isEmpty()) {
            targetTextView.setText(String.format(getString(R.string.betatest_target_format), getString(R.string.app_name)));
        } else {
            targetTextView.setText(String.format(getString(R.string.betatest_target_format), targetApps.get(0)));
        }

        testTypeTextView.setText(betaTest.getTags().get(0));

        long remainDays = betaTest.getRemainDays();
        String projectStatus;
        if (remainDays > 0) {
            projectStatus = String.format(getString(R.string.betatest_project_status_format), remainDays);
        } else if (remainDays == 0) {
            projectStatus = getString(R.string.beta_test_today_close);
        } else {
            projectStatus = getString(R.string.common_close);
        }
        projectStatusTextView.setText(projectStatus);

        requiredTimeTextView.setText(String.format(getString(R.string.betatest_required_time_format), betaTest.getRequiredTime(DateUtil.CONVERT_TYPE_MINUTES)));
        amountTextView.setText(betaTest.getAmount());

        String reward = betaTest.getReward();
        if (TextUtils.isEmpty(reward)) {
            rewardTextView.setText(R.string.betatest_reward_none);
        } else {
            rewardTextView.setText(reward);
        }

        String actionType = betaTest.getActionType();
        if ("link".equals(actionType)) {
            joinButton.setOnClickListener(v -> {
                this.presenter.getAnalytics().sendClickEventLog(FomesConstants.BetaTest.Log.TARGET_DETAIL_DIALOG_JOIN_BUTTON, String.valueOf(betaTest.getId()));

                String url = betaTest.getAction() + userEmail;

                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);

                this.presenter.sendEventLog(FomesConstants.FomesEventLog.Code.BETA_TEST_DETAIL_DIALOG_TAP_CONFIRM, String.valueOf(betaTest.getId()));

                dismiss();
            });
        }
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        Fragment fragment = getTargetFragment();
        if (fragment != null) {
            fragment.onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, null);
        }

        super.onDismiss(dialog);
    }

    @Override
    public void onDestroyView() {
        if (unbinder != null) {
            unbinder.unbind();
        }

        super.onDestroyView();
    }
}
