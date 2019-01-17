package com.formakers.fomes.main.view;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.formakers.fomes.R;
import com.formakers.fomes.common.FomesConstants;
import com.formakers.fomes.common.network.vo.BetaTest;

import java.util.Date;
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
    @BindView(R.id.betatest_detail_size)            TextView sizeTextView;
    @BindView(R.id.betatest_detail_reward)          TextView rewardTextView;
    @BindView(R.id.join_button)                     Button   joinButton;


    private Unbinder unbinder;

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
        BetaTest betaTest = bundle.getParcelable(FomesConstants.BetaTest.EXTRA_BETA_TEST);
        String userEmail = bundle.getString(FomesConstants.BetaTest.EXTRA_USER_EMAIL);

        if (betaTest == null) {
            return;
        }

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

        testTypeTextView.setText(betaTest.getTypeTags().get(0));
        projectStatusTextView.setText(String.format(getString(R.string.betatest_project_status_format), (betaTest.getCloseDate().getTime()/1000 - new Date().getTime()/1000) / (24*60*60)));
        requiredTimeTextView.setText(String.format(getString(R.string.betatest_required_time_format), 3));
//        sizeTextView.setText(betaTest.getTestSize());

        String reward = betaTest.getReward();
        if (TextUtils.isEmpty(reward)) {
            rewardTextView.setText(R.string.betatest_reward_none);
        } else {
            rewardTextView.setText(reward);
        }

        String actionType = betaTest.getActionType();
        if ("link".equals(actionType)) {
            joinButton.setOnClickListener(v -> {
                String url = betaTest.getAction() + userEmail;

                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            });
        }
    }

    @Override
    public void onDestroyView() {
        if (unbinder != null) {
            unbinder.unbind();
        }

        super.onDestroyView();
    }
}
