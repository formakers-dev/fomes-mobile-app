package com.formakers.fomes.analysis.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.formakers.fomes.AppBeeApplication;
import com.formakers.fomes.R;
import com.formakers.fomes.analysis.contract.RecentAnalysisReportContract;
import com.formakers.fomes.analysis.presenter.RecentAnalysisReportPresenter;
import com.formakers.fomes.common.network.vo.Usage;
import com.formakers.fomes.dagger.ApplicationComponent;
import com.formakers.fomes.fragment.BaseFragment;
import com.formakers.fomes.model.User;

import java.util.List;

import butterknife.BindView;
import rx.android.schedulers.AndroidSchedulers;

public class RecentAnalysisReportFragment extends BaseFragment implements RecentAnalysisReportContract.View {
    public static final String TAG = RecentAnalysisReportFragment.class.getSimpleName();

    @BindView(R.id.current_analysis_loading_layout) ViewGroup loadingLayout;
    @BindView(R.id.current_analysis_layout) ViewGroup contentLayout;
    @BindView(R.id.current_analysis_error_layout) ViewGroup errorLayout;
    @BindView(R.id.analysis_my_genre_1) ViewGroup myGenreItem1;
    @BindView(R.id.analysis_my_genre_2) ViewGroup myGenreItem2;
    @BindView(R.id.analysis_my_genre_3) ViewGroup myGenreItem3;
    @BindView(R.id.analysis_people_genre_gender_age) ViewGroup peopleGenreGenderAge;
    @BindView(R.id.analysis_people_genre_job) ViewGroup peopleGenreJob;

    RecentAnalysisReportContract.Presenter presenter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setPresenter(new RecentAnalysisReportPresenter(this));

        return inflater.inflate(R.layout.fragment_current_analysis_report, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        presenter.loading()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> {
                    // TODO : 아래 뷰들 Fragment 관리로 변경 필요
                    loadingLayout.setVisibility(View.GONE);
                    contentLayout.setVisibility(View.VISIBLE);
                    errorLayout.setVisibility(View.GONE);
                }, e -> {
                    loadingLayout.setVisibility(View.GONE);
                    contentLayout.setVisibility(View.GONE);
                    errorLayout.setVisibility(View.VISIBLE);
                });
    }

    @Override
    public void setPresenter(RecentAnalysisReportContract.Presenter presenter) {
        this.presenter = presenter;
    }


    @Override
    public ApplicationComponent getApplicationComponent() {
        return ((AppBeeApplication) this.getActivity().getApplication()).getComponent();
    }

    @Override
    public void bindMyGenreViews(List<Usage> categoryUsages) {
        List<Pair<Usage, Integer>> usagePercentagePair = this.presenter.getPercentage(categoryUsages, 0, 3);

        ((TextView) myGenreItem1.findViewById(R.id.name_textview)).setText(usagePercentagePair.get(0).first.getName());
        ((TextView) myGenreItem1.findViewById(R.id.used_time_textview))
                .setText(String.format(getString(R.string.analysis_my_genre_used_time_format), usagePercentagePair.get(0).second));
        ((TextView) myGenreItem2.findViewById(R.id.name_textview)).setText(usagePercentagePair.get(1).first.getName());
        ((TextView) myGenreItem2.findViewById(R.id.used_time_textview))
                .setText(String.format(getString(R.string.analysis_my_genre_used_time_format), usagePercentagePair.get(1).second));
        ((TextView) myGenreItem3.findViewById(R.id.name_textview)).setText(usagePercentagePair.get(2).first.getName());
        ((TextView) myGenreItem3.findViewById(R.id.used_time_textview))
                .setText(String.format(getString(R.string.analysis_my_genre_used_time_format), usagePercentagePair.get(2).second));
    }

    @Override
    public void bindPeopleGenreViews(List<Usage> genderAgeUsages, List<Usage> jobUsages) {
        User userInfo = this.presenter.getUserInfo();

        ((TextView) peopleGenreGenderAge.findViewById(R.id.demographic_title))
                .setText(String.format(getString(R.string.common_age) + getString(R.string.common_new_line) + getString(R.string.common_string),
                        userInfo.getAge(), getString(userInfo.getGenderToStringResId())));

        List<Pair<Usage, Integer>> genderAgeUsagePercentagePair
                = this.presenter.getPercentage(genderAgeUsages, 0, 3);

        ((TextView) peopleGenreGenderAge.findViewById(R.id.demographic_name_1))
                .setText(genderAgeUsagePercentagePair.get(0).first.getName());
        ((TextView) peopleGenreGenderAge.findViewById(R.id.demographic_name_2))
                .setText(genderAgeUsagePercentagePair.get(1).first.getName());
        ((TextView) peopleGenreGenderAge.findViewById(R.id.demographic_name_3))
                .setText(genderAgeUsagePercentagePair.get(2).first.getName());

        ((TextView) peopleGenreJob.findViewById(R.id.demographic_title))
                .setText(String.format(getString(R.string.common_string), userInfo.getJob()));

        List<Pair<Usage, Integer>> jobUsagePercentagePair
                = this.presenter.getPercentage(jobUsages, 0, 3);

        ((TextView) peopleGenreJob.findViewById(R.id.demographic_name_1))
                .setText(jobUsagePercentagePair.get(0).first.getName());
        ((TextView) peopleGenreJob.findViewById(R.id.demographic_name_2))
                .setText(jobUsagePercentagePair.get(1).first.getName());
        ((TextView) peopleGenreJob.findViewById(R.id.demographic_name_3))
                .setText(jobUsagePercentagePair.get(2).first.getName());
    }
}
