package com.formakers.fomes.betatest;


import android.net.Uri;

import androidx.annotation.DrawableRes;
import androidx.annotation.StringRes;

import com.formakers.fomes.common.dagger.AnalyticsModule;
import com.formakers.fomes.common.helper.ImageLoader;
import com.formakers.fomes.common.mvp.BaseView;
import com.formakers.fomes.common.network.vo.AwardRecord;
import com.formakers.fomes.common.network.vo.BetaTest;
import com.formakers.fomes.common.network.vo.Mission;

import java.util.List;

import rx.subscriptions.CompositeSubscription;

public interface FinishedBetaTestDetailContract {
    interface Presenter {
        void requestAwardRecordOfBest(String betaTestId);
        void requestEpilogue(String betaTestId);
        void requestRecheckableMissions(String betaTestId);

        //Base
        AnalyticsModule.Analytics getAnalytics();

        ImageLoader getImageLoader();

        void emitRecheckMyAnswer(Mission mission);
    }

    interface View extends BaseView<Presenter> {
        void bindEpilogue(BetaTest.Epilogue epilogue);
        void bindAwards(AwardRecord awardRecord);
        void bindMyAnswers(List<Mission> missions);

        void startWebViewActivity(String title, String url);
        void startByDeeplink(Uri parse);

        void disableEpilogue();
        void disableMyAnswers();

        void showLoading();
        void hideLoading();
        void hideAwardsView();
        void showNoticePopup(@StringRes int titleResId, @StringRes int subTitleResId, @DrawableRes int imageResId,
                             @StringRes int positiveButtonTextResId, android.view.View.OnClickListener positiveButtonClickListener);

        // TODO : BaseView 로 이동 고려

        CompositeSubscription getCompositeSubscription();
    }
}