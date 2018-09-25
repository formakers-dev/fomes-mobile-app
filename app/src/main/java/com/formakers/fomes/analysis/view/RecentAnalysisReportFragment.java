package com.formakers.fomes.analysis.view;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.util.Pair;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.formakers.fomes.AppBeeApplication;
import com.formakers.fomes.R;
import com.formakers.fomes.analysis.contract.RecentAnalysisReportContract;
import com.formakers.fomes.analysis.presenter.RecentAnalysisReportPresenter;
import com.formakers.fomes.common.network.vo.Rank;
import com.formakers.fomes.common.network.vo.Usage;
import com.formakers.fomes.common.view.BaseFragment;
import com.formakers.fomes.common.view.RankAppItemView;
import com.formakers.fomes.common.view.RankItemView;
import com.formakers.fomes.dagger.ApplicationComponent;
import com.formakers.fomes.main.view.MainActivity;
import com.formakers.fomes.model.User;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import rx.android.schedulers.AndroidSchedulers;

public class RecentAnalysisReportFragment extends BaseFragment implements RecentAnalysisReportContract.View {
    public static final String TAG = RecentAnalysisReportFragment.class.getSimpleName();

    @BindView(R.id.current_analysis_loading_layout) ViewGroup loadingLayout;
    @BindView(R.id.current_analysis_layout) ViewGroup contentLayout;
    @BindView(R.id.current_analysis_error_layout) ViewGroup errorLayout;
    @BindView(R.id.analysis_my_genre_chart) PieChart myGenrePieChart;
    @BindView(R.id.analysis_my_genre_1) RankAppItemView myGenreItem1;
    @BindView(R.id.analysis_my_genre_2) RankAppItemView myGenreItem2;
    @BindView(R.id.analysis_my_genre_3) RankAppItemView myGenreItem3;
    @BindView(R.id.analysis_people_genre_gender_age) ViewGroup peopleGenreGenderAge;
    @BindView(R.id.analysis_people_genre_job) ViewGroup peopleGenreJob;
    @BindView(R.id.analysis_playtime_rank_best) RankItemView rankBest;
    @BindView(R.id.analysis_playtime_rank_mine) RankItemView rankMine;
    @BindView(R.id.analysis_playtime_rank_worst) RankItemView rankWorst;
    @BindView(R.id.analysis_my_favorite_developer) ViewGroup myFavoriteDeveloper;
    @BindView(R.id.analysis_gender_age_favorite_developer) ViewGroup genderAgeFavoriteDeveloper;
    @BindView(R.id.analysis_job_favorite_developer) ViewGroup jobFavoriteDeveloper;
    @BindView(R.id.analysis_my_games_1) RankAppItemView myGamesItem1;
    @BindView(R.id.analysis_my_games_2) RankAppItemView myGamesItem2;
    @BindView(R.id.analysis_my_games_3) RankAppItemView myGamesItem3;
    @BindView(R.id.analysis_people_games_gender_age) ViewGroup genderAgeGames;
    @BindView(R.id.analysis_people_games_job) ViewGroup jobGames;

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

    private void bindChart(PieChart pieChart, List<Number> datas) {
        List<PieEntry> pieEntries = new ArrayList<>();
        float total = 0;
        for (Number data : datas) {
            pieEntries.add(new PieEntry(data.floatValue()));
            total += data.floatValue();
        }
        pieEntries.add(new PieEntry(100 - total));
        PieDataSet pieDataSet = new PieDataSet(pieEntries,"");

        Resources res = getResources();
        List<Integer> colors = Arrays.asList(res.getColor(R.color.colorPrimary), res.getColor(R.color.fomes_squash),
                res.getColor(R.color.fomes_blush_pink), res.getColor(R.color.fomes_gray));
        pieDataSet.setColors(colors);
        pieDataSet.setDrawValues(false);

        pieChart.setData(new PieData(pieDataSet));
        pieChart.setDrawSlicesUnderHole(false);
        pieChart.setDrawEntryLabels(false);
        pieChart.getDescription().setEnabled(false);
        pieChart.getLegend().setEnabled(false);
        pieChart.setHoleRadius(0.0f);
        pieChart.setTransparentCircleRadius(72f);
        pieChart.setTransparentCircleAlpha(255);
        pieChart.setTouchEnabled(false);
    }

    @Override
    public void bindMyGenreViews(List<Usage> categoryUsages) {
        List<Pair<Usage, Integer>> usagePercentagePair = this.presenter.getPercentage(categoryUsages, 0, 3);

        int bestPercent = usagePercentagePair.get(0).second;
        int myPercent = usagePercentagePair.get(1).second;
        int worstPercent = usagePercentagePair.get(2).second;

        List<Number> percentages = new ArrayList<>();
        for (Pair<Usage, Integer> pair : usagePercentagePair) {
            percentages.add(pair.second);
        }
        bindChart(myGenrePieChart, percentages);

        myGenreItem1.setTitleText(usagePercentagePair.get(0).first.getName());
        myGenreItem1.setDescriptionText(String.format(getString(R.string.analysis_my_genre_used_time_format), bestPercent));
        myGenreItem2.setTitleText(usagePercentagePair.get(1).first.getName());
        myGenreItem2.setDescriptionText(String.format(getString(R.string.analysis_my_genre_used_time_format), myPercent));
        myGenreItem3.setTitleText(usagePercentagePair.get(2).first.getName());
        myGenreItem3.setDescriptionText(String.format(getString(R.string.analysis_my_genre_used_time_format), worstPercent));
    }

    @Override
    public void bindPeopleGenreViews(List<Usage> genderAgeCategoryUsages, List<Usage> jobCategoryUsages) {
        User userInfo = this.presenter.getUserInfo();

        // 동일 성별/나이
        ((TextView) peopleGenreGenderAge.findViewById(R.id.group))
                .setText(String.format(getString(R.string.common_age) + getString(R.string.common_new_line) + getString(R.string.common_string),
                        userInfo.getAge(), getString(userInfo.getGenderToStringResId())));

        List<Pair<Usage, Integer>> genderAgeUsagePercentagePair
                = this.presenter.getPercentage(genderAgeCategoryUsages, 0, 3);

        List<Number> genderAgePercentages = new ArrayList<>();
        for (Pair<Usage, Integer> pair : genderAgeUsagePercentagePair) {
            genderAgePercentages.add(pair.second);
        }
        bindChart(peopleGenreGenderAge.findViewById(R.id.chart), genderAgePercentages);
        Log.d(TAG, genderAgeCategoryUsages.toString());

        ((TextView) peopleGenreGenderAge.findViewById(R.id.title_1))
                .setText(genderAgeUsagePercentagePair.get(0).first.getName());
        ((TextView) peopleGenreGenderAge.findViewById(R.id.title_2))
                .setText(genderAgeUsagePercentagePair.get(1).first.getName());
        ((TextView) peopleGenreGenderAge.findViewById(R.id.title_3))
                .setText(genderAgeUsagePercentagePair.get(2).first.getName());

        // 동일 직업군
        ((TextView) peopleGenreJob.findViewById(R.id.group))
                .setText(String.format(getString(R.string.common_string), userInfo.getJob()));

        List<Pair<Usage, Integer>> jobUsagePercentagePair
                = this.presenter.getPercentage(jobCategoryUsages, 0, 3);

        List<Number> jobPercentages = new ArrayList<>();
        for (Pair<Usage, Integer> pair : jobUsagePercentagePair) {
            jobPercentages.add(pair.second);
        }
        bindChart(peopleGenreJob.findViewById(R.id.chart), jobPercentages);

        ((TextView) peopleGenreJob.findViewById(R.id.title_1))
                .setText(jobUsagePercentagePair.get(0).first.getName());
        ((TextView) peopleGenreJob.findViewById(R.id.title_2))
                .setText(jobUsagePercentagePair.get(1).first.getName());
        ((TextView) peopleGenreJob.findViewById(R.id.title_3))
                .setText(jobUsagePercentagePair.get(2).first.getName());
    }

    @Override
    public void bindRankingViews(List<Rank> totalUsedTimeRank) {
        Log.d(TAG, String.valueOf(totalUsedTimeRank));

        ((TextView) rankBest.findViewById(R.id.title_textview))
                .setText(String.format(getString(R.string.analysis_playtime_rank_number), totalUsedTimeRank.get(0).getRank()));
        ((TextView) rankMine.findViewById(R.id.title_textview))
                .setText(String.format(getString(R.string.analysis_playtime_my_rank_number),totalUsedTimeRank.get(1).getRank()));
        ((TextView) rankWorst.findViewById(R.id.title_textview))
                .setText(R.string.analysis_playtime_rank_number_last);

        ((TextView) rankBest.findViewById(R.id.desc_textview))
                .setText(String.format(getString(R.string.analysis_playtime_rank_hours),
                        presenter.getHour(totalUsedTimeRank.get(0).getContent())));
        ((TextView) rankMine.findViewById(R.id.desc_textview))
                .setText(String.format(getString(R.string.analysis_playtime_rank_hours),
                        presenter.getHour(totalUsedTimeRank.get(1).getContent())));
        ((TextView) rankWorst.findViewById(R.id.desc_textview))
                .setText(String.format(getString(R.string.analysis_playtime_rank_hours),
                        presenter.getHour(totalUsedTimeRank.get(2).getContent())));
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

    @Override
    public void bindMyGames(List<Usage> myAppUsages) {
        myGamesItem1.setTitleText(myAppUsages.get(0).getName());
        myGamesItem1.setDescriptionText(getString(R.string.analysis_my_games_description, presenter.getHour(myAppUsages.get(0).getTotalUsedTime())));
        presenter.getImageLoader().load(myAppUsages.get(0).getAppInfos().get(0).getIconUrl()).into(myGamesItem1.getIconImageView());

        myGamesItem2.setTitleText(myAppUsages.get(1).getName());
        myGamesItem2.setDescriptionText(getString(R.string.analysis_my_games_description, presenter.getHour(myAppUsages.get(1).getTotalUsedTime())));
        presenter.getImageLoader().load(myAppUsages.get(1).getAppInfos().get(0).getIconUrl()).into(myGamesItem2.getIconImageView());

        myGamesItem3.setTitleText(myAppUsages.get(2).getName());
        myGamesItem3.setDescriptionText(getString(R.string.analysis_my_games_description, presenter.getHour(myAppUsages.get(2).getTotalUsedTime())));
        presenter.getImageLoader().load(myAppUsages.get(2).getAppInfos().get(0).getIconUrl()).into(myGamesItem3.getIconImageView());
    }

    @Override
    public void bindPeopleGamesViews(List<Usage> genderAgeAppUsages, List<Usage> jobAppUsages) {
        User userInfo = this.presenter.getUserInfo();

        ((TextView) genderAgeGames.findViewById(R.id.group))
                .setText(String.format(getString(R.string.common_age) + getString(R.string.common_new_line) + getString(R.string.common_string),
                        userInfo.getAge(), getString(userInfo.getGenderToStringResId())));

        presenter.getImageLoader().load(genderAgeAppUsages.get(0).getAppInfos().get(0).getIconUrl()).into((ImageView) genderAgeGames.findViewById(R.id.icon_1));
        presenter.getImageLoader().load(genderAgeAppUsages.get(1).getAppInfos().get(0).getIconUrl()).into((ImageView) genderAgeGames.findViewById(R.id.icon_2));
        presenter.getImageLoader().load(genderAgeAppUsages.get(2).getAppInfos().get(0).getIconUrl()).into((ImageView) genderAgeGames.findViewById(R.id.icon_3));

        ((TextView) genderAgeGames.findViewById(R.id.title_1)).setText(genderAgeAppUsages.get(0).getName());
        ((TextView) genderAgeGames.findViewById(R.id.title_2)).setText(genderAgeAppUsages.get(1).getName());
        ((TextView) genderAgeGames.findViewById(R.id.title_3)).setText(genderAgeAppUsages.get(2).getName());

        ((TextView) jobGames.findViewById(R.id.group))
                .setText(String.format(getString(R.string.common_string), userInfo.getJob()));

        presenter.getImageLoader().load(jobAppUsages.get(0).getAppInfos().get(0).getIconUrl()).into((ImageView) jobGames.findViewById(R.id.icon_1));
        presenter.getImageLoader().load(jobAppUsages.get(1).getAppInfos().get(0).getIconUrl()).into((ImageView) jobGames.findViewById(R.id.icon_2));
        presenter.getImageLoader().load(jobAppUsages.get(2).getAppInfos().get(0).getIconUrl()).into((ImageView) jobGames.findViewById(R.id.icon_3));

        ((TextView) jobGames.findViewById(R.id.title_1)).setText(jobAppUsages.get(0).getName());
        ((TextView) jobGames.findViewById(R.id.title_2)).setText(jobAppUsages.get(1).getName());
        ((TextView) jobGames.findViewById(R.id.title_3)).setText(jobAppUsages.get(2).getName());
    }

    @OnClick(R.id.current_analysis_exit_button)
    public void onConfirmButtonClick() {
        // TODO : 추후 일반 종료로 변경해야 함
        Intent intent = new Intent(getContext(), MainActivity.class);
        startActivity(intent);
        this.getActivity().finish();
    }
}
