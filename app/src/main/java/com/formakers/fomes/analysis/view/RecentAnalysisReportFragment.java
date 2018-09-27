package com.formakers.fomes.analysis.view;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.formakers.fomes.AppBeeApplication;
import com.formakers.fomes.R;
import com.formakers.fomes.analysis.contract.RecentAnalysisReportContract;
import com.formakers.fomes.analysis.presenter.RecentAnalysisReportPresenter;
import com.formakers.fomes.common.network.vo.Rank;
import com.formakers.fomes.common.network.vo.Usage;
import com.formakers.fomes.common.view.BaseFragment;
import com.formakers.fomes.common.view.FavoriteDeveloperItemView;
import com.formakers.fomes.common.view.RankAppItemView;
import com.formakers.fomes.dagger.ApplicationComponent;
import com.formakers.fomes.main.view.MainActivity;
import com.formakers.fomes.model.User;
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import rx.android.schedulers.AndroidSchedulers;

public class RecentAnalysisReportFragment extends BaseFragment implements RecentAnalysisReportContract.View {
    public static final String TAG = RecentAnalysisReportFragment.class.getSimpleName();

    @BindView(R.id.current_analysis_loading_layout) ViewGroup loadingLayout;
    @BindView(R.id.current_analysis_layout) ViewGroup contentLayout;
    @BindView(R.id.current_analysis_error_layout) ViewGroup errorLayout;

    @BindView(R.id.fragment_loading_imageview) ImageView loadingImageView;

    @BindView(R.id.analysis_my_genre_chart) PieChart myGenrePieChart;
    @BindView(R.id.analysis_my_genre_1) RankAppItemView myGenreItem1;
    @BindView(R.id.analysis_my_genre_2) RankAppItemView myGenreItem2;
    @BindView(R.id.analysis_my_genre_3) RankAppItemView myGenreItem3;
    @BindView(R.id.analysis_people_genre_gender_age) ViewGroup peopleGenreGenderAge;
    @BindView(R.id.analysis_people_genre_job) ViewGroup peopleGenreJob;
    @BindView(R.id.analysis_playtime_rank_horizontal_bar_chart) HorizontalBarChart rankHorizontalBarChart;
    @BindView(R.id.analysis_my_favorite_developer) FavoriteDeveloperItemView myFavoriteDeveloper;
    @BindView(R.id.analysis_gender_age_favorite_developer) FavoriteDeveloperItemView genderAgeFavoriteDeveloper;
    @BindView(R.id.analysis_job_favorite_developer) FavoriteDeveloperItemView jobFavoriteDeveloper;
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

        addCompositeSubscription(
            presenter.loading()
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe((subscription) -> {
                    presenter.getImageLoader().asGif()
                            .load(R.drawable.loading)
                            .apply(new RequestOptions().override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL))
                            .into(loadingImageView);
                }).subscribe(() -> {
                    // TODO : 아래 뷰들 Fragment 관리로 변경 필요
                    loadingLayout.setVisibility(View.GONE);
                    contentLayout.setVisibility(View.VISIBLE);
                    errorLayout.setVisibility(View.GONE);
                }, e -> {
                    loadingLayout.setVisibility(View.GONE);
                    contentLayout.setVisibility(View.GONE);
                    errorLayout.setVisibility(View.VISIBLE);
                })
        );
    }

    @Override
    public void setPresenter(RecentAnalysisReportContract.Presenter presenter) {
        this.presenter = presenter;
    }


    @Override
    public ApplicationComponent getApplicationComponent() {
        return ((AppBeeApplication) this.getActivity().getApplication()).getComponent();
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
                .setText(String.format(getString(R.string.common_string), User.JobCategory.get(userInfo.getJob()).getName()));

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

        float best = presenter.getHour(totalUsedTimeRank.get(0).getContent());
        float mine = presenter.getHour(totalUsedTimeRank.get(1).getContent());
        float worst = presenter.getHour(totalUsedTimeRank.get(2).getContent());

        Map<Float, String> labelMap = new HashMap<>();
        labelMap.put(3f, getResources().getString(R.string.analysis_playtime_rank_number, totalUsedTimeRank.get(0).getRank()));
        labelMap.put(2f, getResources().getString(R.string.analysis_playtime_my_rank_number, totalUsedTimeRank.get(1).getRank()));
        labelMap.put(1f, getResources().getString(R.string.analysis_playtime_rank_number_last));

        List<BarEntry> barEntries = new ArrayList<>();
        barEntries.add(new BarEntry(3f, best));
        barEntries.add(new BarEntry(2f, mine));
        barEntries.add(new BarEntry(1f, worst));
        Log.d(TAG, String.valueOf(barEntries));

        BarDataSet barDataSet = new BarDataSet(barEntries, "");

        Resources res = getResources();
        List<Integer> colors = Arrays.asList(res.getColor(R.color.colorPrimary), res.getColor(R.color.fomes_squash),
                res.getColor(R.color.fomes_blush_pink), res.getColor(R.color.fomes_gray));
        barDataSet.setColors(colors);

        barDataSet.setValueFormatter(new PlaytimeFormatter());
        barDataSet.setValueTextSize(9f);
        barDataSet.setValueTextColor(res.getColor(R.color.fomes_warm_gray));
        barDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);

        BarData barData = new BarData(barDataSet);

        rankHorizontalBarChart.setData(barData);
        XAxis HorizontalBarXAxis = rankHorizontalBarChart.getXAxis();

        HorizontalBarXAxis.setTextSize(10f);
        HorizontalBarXAxis.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        HorizontalBarXAxis.setTextColor(res.getColor(R.color.fomes_warm_gray));
        HorizontalBarXAxis.setAxisLineColor(res.getColor(R.color.fomes_light_gray));
        HorizontalBarXAxis.setValueFormatter((value, axis) -> labelMap.get(value));
        HorizontalBarXAxis.setDrawLabels(true);
        HorizontalBarXAxis.setDrawGridLines(false);
        HorizontalBarXAxis.setGranularityEnabled(true);
        HorizontalBarXAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        HorizontalBarXAxis.setXOffset(9);
        HorizontalBarXAxis.setYOffset(0);

        YAxis HorizontalBarYAxis = rankHorizontalBarChart.getAxisLeft();
        HorizontalBarYAxis.setAxisLineColor(Color.TRANSPARENT);
        HorizontalBarYAxis.setDrawLabels(false);
        HorizontalBarYAxis.setDrawGridLines(false);
        HorizontalBarYAxis.setXOffset(0);
        HorizontalBarYAxis.setAxisMinimum(0);

        rankHorizontalBarChart.getAxisRight().setAxisMinimum(0);
        rankHorizontalBarChart.getAxisRight().setAxisMaximum(0);
        rankHorizontalBarChart.getAxisRight().setAxisLineColor(Color.TRANSPARENT);
        rankHorizontalBarChart.getAxisRight().setDrawLabels(false);
        rankHorizontalBarChart.getAxisRight().setDrawGridLines(false);

        rankHorizontalBarChart.setDrawBorders(false);
        rankHorizontalBarChart.setDrawGridBackground(false);
        rankHorizontalBarChart.getDescription().setEnabled(false);
        rankHorizontalBarChart.getLegend().setEnabled(false);
        rankHorizontalBarChart.setTouchEnabled(false);
        rankHorizontalBarChart.setDrawValueAboveBar(true);
        rankHorizontalBarChart.setFitBars(true);
        rankHorizontalBarChart.setTouchEnabled(false);
        rankHorizontalBarChart.setViewPortOffsets(0,0,0,0);
        rankHorizontalBarChart.setExtraRightOffset(30);
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
                .setText(getString(R.string.analysis_favorite_developer_group, User.JobCategory.get(userInfo.getJob()).getName()));

        ((TextView) myFavoriteDeveloper.findViewById(R.id.developer_name)).setText(myDeveloperUsages.get(0).getName());
        ((TextView) genderAgeFavoriteDeveloper.findViewById(R.id.developer_name)).setText(genderAgeDeveloperUsages.get(0).getName());
        ((TextView) jobFavoriteDeveloper.findViewById(R.id.developer_name)).setText(jobDeveloperUsages.get(0).getName());

        ((TextView) myFavoriteDeveloper.findViewById(R.id.developer_description))
                .setText(myDeveloperUsages.get(0).getAppInfos().get(0).getAppName());
        ((TextView) genderAgeFavoriteDeveloper.findViewById(R.id.developer_description))
                .setText(genderAgeDeveloperUsages.get(0).getAppInfos().get(0).getAppName());
        ((TextView) jobFavoriteDeveloper.findViewById(R.id.developer_description))
                .setText(jobDeveloperUsages.get(0).getAppInfos().get(0).getAppName());
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
                .setText(String.format(getString(R.string.common_string), User.JobCategory.get(userInfo.getJob()).getName()));

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
