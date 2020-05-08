package com.formakers.fomes.betatest;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.formakers.fomes.FomesApplication;
import com.formakers.fomes.R;
import com.formakers.fomes.common.constant.FomesConstants;
import com.formakers.fomes.common.network.vo.AwardRecord;
import com.formakers.fomes.common.network.vo.BetaTest;
import com.formakers.fomes.common.util.DateUtil;
import com.formakers.fomes.common.util.Log;
import com.formakers.fomes.common.view.FomesBaseActivity;

import java.text.SimpleDateFormat;
import java.util.Locale;

import javax.inject.Inject;

import butterknife.BindView;
import rx.subscriptions.CompositeSubscription;

public class BetaTestCertificateActivity extends FomesBaseActivity implements BetaTestCertificateContract.View {
    private static final String TAG = "BetaTestCertificateActivity";

    @BindView(R.id.betatest_app_icon) ImageView betaTestAppIcon;
    @BindView(R.id.betatest_game_title) TextView betaTestGameTitle;
    @BindView(R.id.betatest_period) TextView betaTestPeriod;
    @BindView(R.id.betatest_awards_title) TextView betaTestAwardsTitle;
    @BindView(R.id.betatest_awards_nickname) TextView betaTestAwardsNickName;
    @BindView(R.id.betatest_certificate_description) TextView betaTestCertificateDescription;
    @BindView(R.id.betatest_certificate_fomes_feedback) TextView betaTestCertificateFomesFeedback;

    @Inject BetaTestCertificateContract.Presenter presenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.v(TAG, "onCreate intent=" + getIntent().getStringExtra(FomesConstants.BetaTest.EXTRA_ID));

        this.setContentView(R.layout.activity_betatest_certificate);

        getSupportActionBar().setTitle("테스트 참여 확인증");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        DaggerBetaTestCertificateDagger_Component.builder()
                .applicationComponent(FomesApplication.get(this).getComponent())
                .module(new BetaTestCertificateDagger.Module(this))
                .build()
                .inject(this);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        if (getIntent() == null || getIntent().getExtras() == null) {
            return;
        }

        Bundle bundle = getIntent().getExtras();

        String betaTestId = bundle.getString(FomesConstants.BetaTest.EXTRA_ID);

        this.presenter.requestBetaTestDetail(betaTestId);
        this.presenter.requestUserNickName();
    }

    @Override
    public void bindBetaTestDetail(BetaTest betaTest) {
        this.presenter.getImageLoader().loadImage(betaTestAppIcon, betaTest.getIconImageUrl(),
                new RequestOptions().override(120, 120)
                        .centerCrop()
                        .transform(new RoundedCorners(8))
                , false);

        betaTestGameTitle.setText(betaTest.getTitle());

        SimpleDateFormat dateFormat = new SimpleDateFormat(DateUtil.YYYY_DOT_MM_DOT_DD, Locale.getDefault());
        String formattedOpenDate = dateFormat.format(betaTest.getOpenDate());
        String formattedCloseDate = dateFormat.format(betaTest.getCloseDate());

        if (formattedCloseDate.startsWith(formattedOpenDate.substring(0, 4))) {
            SimpleDateFormat shortenDateFormat = new SimpleDateFormat(DateUtil.MM_DOT_DD, Locale.getDefault());
            betaTestPeriod.setText(String.format("%s ~ %s",
                    formattedOpenDate,
                    shortenDateFormat.format(betaTest.getCloseDate())));
        } else {
            betaTestPeriod.setText(String.format("%s ~ %s",
                    formattedOpenDate,
                    formattedCloseDate));
        }
    }

    @Override
    public void bindCertificate(BetaTest betaTest, AwardRecord awardRecord) {
        String awardType = (awardRecord != null) ? awardRecord.getType() : "";
        switch(awardType) {
            case "best" :
                betaTestAwardsTitle.setText("수석 테스터");
                betaTestAwardsTitle.setTextColor(getResources().getColor(R.color.colorPrimary));
                betaTestCertificateDescription.setText(getString(R.string.betatest_certificate_description_for_best, betaTest.getTitle()));
                betaTestCertificateFomesFeedback.setText(getString(R.string.betatest_certificate_fomes_feedback));
                break;

            case "good" :
                betaTestAwardsTitle.setText("차석 테스터");
                betaTestAwardsTitle.setTextColor(getResources().getColor(R.color.fomes_squash));
                betaTestCertificateDescription.setText(getString(R.string.betatest_certificate_description_for_good, betaTest.getTitle()));
                betaTestCertificateFomesFeedback.setText(getString(R.string.betatest_certificate_fomes_feedback));
                break;

            case "normal" :
                betaTestAwardsTitle.setText("성실 테스터");
                betaTestAwardsTitle.setTextColor(getResources().getColor(R.color.fomes_blush_pink));
                betaTestCertificateDescription.setText(getString(R.string.betatest_certificate_description_for_normal, betaTest.getTitle()));
                betaTestCertificateFomesFeedback.setText(getString(R.string.betatest_certificate_fomes_feedback));
                break;

            case "participated" :
            case "etc" :
                betaTestAwardsTitle.setText("테스터");
                betaTestAwardsTitle.setTextColor(getResources().getColor(R.color.fomes_warm_gray));
                betaTestCertificateDescription.setText(getString(R.string.betatest_certificate_description, betaTest.getTitle()));
                betaTestCertificateFomesFeedback.setText(getString(R.string.betatest_certificate_fomes_feedback));
                break;

            default :
                betaTestAwardsTitle.setText("테스터");
                betaTestAwardsTitle.setTextColor(getResources().getColor(R.color.fomes_warm_gray));
                betaTestCertificateDescription.setText(getString(R.string.betatest_certificate_description, betaTest.getTitle()));
                betaTestCertificateFomesFeedback.setText(getString(R.string.betatest_certificate_fomes_feedback_for_better_try));
                break;
        }
    }


    @Override
    public void bindUserNickName(String nickName) {
        betaTestAwardsNickName.setText(getString(R.string.betatest_certificate_nick_name, nickName));
    }


    @Override
    public void showLoading() {

    }

    @Override
    public void hideLoading() {

    }

    @Override
    public CompositeSubscription getCompositeSubscription() {
        return null;
    }

    @Override
    public void setPresenter(BetaTestCertificateContract.Presenter presenter) {

    }
}
