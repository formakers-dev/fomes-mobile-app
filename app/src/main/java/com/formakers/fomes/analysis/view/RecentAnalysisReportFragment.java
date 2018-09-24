package com.formakers.fomes.analysis.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.util.Pair;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.formakers.fomes.AppBeeApplication;
import com.formakers.fomes.R;
import com.formakers.fomes.analysis.contract.RecentAnalysisReportContract;
import com.formakers.fomes.analysis.presenter.RecentAnalysisReportPresenter;
import com.formakers.fomes.common.network.vo.Rank;
import com.formakers.fomes.common.network.vo.Usage;
import com.formakers.fomes.common.view.RankAppItemView;
import com.formakers.fomes.dagger.ApplicationComponent;
import com.formakers.fomes.fragment.BaseFragment;
import com.formakers.fomes.model.User;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import rx.android.schedulers.AndroidSchedulers;

public class RecentAnalysisReportFragment extends BaseFragment implements RecentAnalysisReportContract.View {
    public static final String TAG = RecentAnalysisReportFragment.class.getSimpleName();

    @BindView(R.id.current_analysis_loading_layout) ViewGroup loadingLayout;
    @BindView(R.id.current_analysis_layout) ViewGroup contentLayout;
    @BindView(R.id.current_analysis_error_layout) ViewGroup errorLayout;
    @BindView(R.id.analysis_my_genre_1) RankAppItemView myGenreItem1;
    @BindView(R.id.analysis_my_genre_2) RankAppItemView myGenreItem2;
    @BindView(R.id.analysis_my_genre_3) RankAppItemView myGenreItem3;
    @BindView(R.id.analysis_people_genre_gender_age) ViewGroup peopleGenreGenderAge;
    @BindView(R.id.analysis_people_genre_job) ViewGroup peopleGenreJob;
    @BindView(R.id.analysis_playtime_rank_best) ViewGroup rankBest;
    @BindView(R.id.analysis_playtime_rank_mine) ViewGroup rankMine;
    @BindView(R.id.analysis_playtime_rank_worst) ViewGroup rankWorst;
    @BindView(R.id.analysis_my_favorite_developer) ViewGroup myFavoriteDeveloper;
    @BindView(R.id.analysis_gender_age_favorite_developer) ViewGroup genderAgeFavoriteDeveloper;
    @BindView(R.id.analysis_job_favorite_developer) ViewGroup jobFavoriteDeveloper;


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
                .doOnCompleted(() -> initViews())
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

    /*
     * Applying style and attributes for analysis views
     */
    public void initViews() {
        myFavoriteDeveloper.setBackground(getResources().getDrawable(R.drawable.item_rect_solid_background, new ContextThemeWrapper(getContext(), R.style.FomesTheme_TurquoiseItem).getTheme()));
        genderAgeFavoriteDeveloper.setBackground(getResources().getDrawable(R.drawable.item_rect_solid_background, new ContextThemeWrapper(getContext(), R.style.FomesTheme_SquashItem).getTheme()));
        jobFavoriteDeveloper.setBackground(getResources().getDrawable(R.drawable.item_rect_solid_background, new ContextThemeWrapper(getContext(), R.style.FomesTheme_BlushPinkItem).getTheme()));
    }

    @Override
    public void bindMyGenreViews(List<Usage> categoryUsages) {
        List<Pair<Usage, Integer>> usagePercentagePair = this.presenter.getPercentage(categoryUsages, 0, 3);

        myGenreItem1.setTitleText(usagePercentagePair.get(0).first.getName());
        myGenreItem1.setDescriptionText(String.format(getString(R.string.analysis_my_genre_used_time_format), usagePercentagePair.get(0).second));
        myGenreItem2.setTitleText(usagePercentagePair.get(1).first.getName());
        myGenreItem2.setDescriptionText(String.format(getString(R.string.analysis_my_genre_used_time_format), usagePercentagePair.get(1).second));
        myGenreItem3.setTitleText(usagePercentagePair.get(2).first.getName());
        myGenreItem3.setDescriptionText(String.format(getString(R.string.analysis_my_genre_used_time_format), usagePercentagePair.get(2).second));
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

    @Override
    public void bindRankingViews(List<Rank> totalUsedTimeRank) {
        Log.d(TAG, String.valueOf(totalUsedTimeRank));

        ((TextView) rankBest.findViewById(R.id.rank_number))
                .setText(String.format(getString(R.string.analysis_playtime_rank_number), totalUsedTimeRank.get(0).getRank()));
        ((TextView) rankMine.findViewById(R.id.rank_number))
                .setText(String.format(getString(R.string.analysis_playtime_rank_number),totalUsedTimeRank.get(1).getRank()));
        ((TextView) rankWorst.findViewById(R.id.rank_number))
                .setText(R.string.analysis_playtime_rank_number_last);

        ((TextView) rankBest.findViewById(R.id.rank_content))
                .setText(String.format(getString(R.string.analysis_playtime_rank_hours),
                        totalUsedTimeRank.get(0).getContent(Rank.CONVERT_TYPE_HOURS)));
        ((TextView) rankMine.findViewById(R.id.rank_content))
                .setText(String.format(getString(R.string.analysis_playtime_rank_hours),
                        totalUsedTimeRank.get(1).getContent(Rank.CONVERT_TYPE_HOURS)));
        ((TextView) rankWorst.findViewById(R.id.rank_content))
                .setText(String.format(getString(R.string.analysis_playtime_rank_hours),
                        totalUsedTimeRank.get(2).getContent(Rank.CONVERT_TYPE_HOURS)));
    }

    @Override
    public void bindFavoriteDeveloperViews(List<Usage> myDeveloperUsages, List<Usage> genderAgeDeveloperUsages, List<Usage> jobDeveloperUsages) {
        User userInfo = presenter.getUserInfo();

        ((TextView) myFavoriteDeveloper.findViewById(R.id.group)).setText(R.string.analysis_favorite_developer_group_mine);
        ((TextView) genderAgeFavoriteDeveloper.findViewById(R.id.group))
                .setText(getString(R.string.analysis_favorite_developer_group,
                        getString(R.string.common_age, userInfo.getAge()) + " " +
                                getString(R.string.common_string, getString(userInfo.getGenderToStringResId()))));
        ((TextView) jobFavoriteDeveloper.findViewById(R.id.group))
                .setText(getString(R.string.analysis_favorite_developer_group, userInfo.getJob()));

        ((TextView) myFavoriteDeveloper.findViewById(R.id.developer_name)).setText(myDeveloperUsages.get(0).getName());
        ((TextView) genderAgeFavoriteDeveloper.findViewById(R.id.developer_name)).setText(genderAgeDeveloperUsages.get(0).getName());
        ((TextView) jobFavoriteDeveloper.findViewById(R.id.developer_name)).setText(jobDeveloperUsages.get(0).getName());

        ((TextView) myFavoriteDeveloper.findViewById(R.id.developer_description))
                .setText(getString(R.string.analysis_favorite_developer_description, myDeveloperUsages.get(0).getAppInfos().get(0).getAppName()));
        ((TextView) genderAgeFavoriteDeveloper.findViewById(R.id.developer_description))
                .setText(getString(R.string.analysis_favorite_developer_description, genderAgeDeveloperUsages.get(0).getAppInfos().get(0).getAppName()));
        ((TextView) jobFavoriteDeveloper.findViewById(R.id.developer_description))
                .setText(getString(R.string.analysis_favorite_developer_description, jobDeveloperUsages.get(0).getAppInfos().get(0).getAppName()));
    }

    @OnClick(R.id.current_analysis_exit_button)
    public void onConfirmButtonClick() {
        this.getActivity().finish();
    }
}
