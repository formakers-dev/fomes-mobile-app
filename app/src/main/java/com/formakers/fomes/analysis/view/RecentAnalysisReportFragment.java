package com.formakers.fomes.analysis.view;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.formakers.fomes.FomesApplication;
import com.formakers.fomes.R;
import com.formakers.fomes.analysis.contract.RecentAnalysisReportContract;
import com.formakers.fomes.analysis.presenter.RecentAnalysisReportPresenter;
import com.formakers.fomes.common.network.vo.Rank;
import com.formakers.fomes.common.network.vo.Usage;
import com.formakers.fomes.common.view.BaseFragment;
import com.formakers.fomes.common.view.custom.FavoriteDeveloperItemView;
import com.formakers.fomes.common.view.custom.RankAppItemView;
import com.formakers.fomes.common.dagger.ApplicationComponent;
import com.formakers.fomes.common.util.Log;
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

    @BindView(R.id.analysis_icon_imageview) ImageView iconImageView;
    @BindView(R.id.analysis_title_textview) TextView titleTextView;
    @BindView(R.id.analysis_subtitle_textview) TextView subtitleTextView;
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
    @BindView(R.id.analysis_my_game_suggestion_textview) TextView myGamesSuggestionTextView;
    @BindView(R.id.analysis_people_games_gender_age) ViewGroup genderAgeGames;
    @BindView(R.id.analysis_people_games_job) ViewGroup jobGames;
    @BindView(R.id.analysis_my_playtime_rank_medal_text) TextView myPlaytimeRankMedalTextView;
    @BindView(R.id.analysis_my_playtime_rank_summary) TextView myPlaytimeRankSummaryTextView;
    @BindView(R.id.analysis_my_playtime_rank_description_title) TextView myPlaytimeRankDescriptionTitleTextView;
    @BindView(R.id.analysis_my_playtime_rank_description_content) TextView myPlaytimeRankDescriptionContentTextView;

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
                .doOnSubscribe((subscription) ->
                    presenter.getImageLoader().asGif().load(R.drawable.loading)
                        .apply(new RequestOptions().override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL))
                        .into(loadingImageView)
                ).subscribe(() -> loadingComplete(true), e -> loadingComplete(false))
        );
    }

    private void loadingComplete(boolean isSuccess) {
        // TODO : 아래 뷰들 Fragment 관리로 변경 필요
        loadingLayout.setVisibility(View.GONE);
        contentLayout.setVisibility(isSuccess ? View.VISIBLE : View.GONE);
        errorLayout.setVisibility(isSuccess ? View.GONE : View.VISIBLE);
    }

    @Override
    public void setPresenter(RecentAnalysisReportContract.Presenter presenter) {
        this.presenter = presenter;
    }


    @Override
    public ApplicationComponent getApplicationComponent() {
        return ((FomesApplication) this.getActivity().getApplication()).getComponent();
    }

    @Override
    public void bindErrorHeaderView() {
        iconImageView.setImageDrawable(getResources().getDrawable(R.drawable.fomes_face_cry, null));
        titleTextView.setText(R.string.analysis_error_header_not_enough_data_title);
        subtitleTextView.setText(R.string.analysis_error_header_not_enough_data_subtitle);
    }

    private void bindChart(PieChart pieChart, List<Number> datas) {
        List<PieEntry> pieEntries = new ArrayList<>();
        float total = 0;
        for (Number data : datas) {
            pieEntries.add(new PieEntry(data.floatValue()));
            total += data.floatValue();
        }

        // TODO : Refactor
        if (datas.size() <= 0 || datas.size() >= 3) {
            pieEntries.add(new PieEntry(100 - total));
        }
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
        int size = categoryUsages.size();

        List<Pair<Usage, Integer>> usagePercentagePair = this.presenter.getPercentage(categoryUsages, 0,
                size > 3 ? 3 : size);

        List<Number> percentages = new ArrayList<>();
        for (Pair<Usage, Integer> pair : usagePercentagePair) {
            percentages.add(pair.second);
        }

        if (size <= 0) {
            myGenreItem1.setNumberText("?");
            myGenreItem1.setTitleText(R.string.analysis_my_genre_cannot_know_genre);
            myGenreItem1.setDescriptionText(String.format(getString(R.string.analysis_my_genre_used_time_format), 0));
            myGenreItem1.setVisibility(View.VISIBLE);
        }

        if (size > 0) {
            myGenreItem1.setNumberText("1");
            int bestPercent = usagePercentagePair.get(0).second;
            myGenreItem1.setTitleText(usagePercentagePair.get(0).first.getName());
            myGenreItem1.setDescriptionText(String.format(getString(R.string.analysis_my_genre_used_time_format), bestPercent));
            myGenreItem1.setVisibility(View.VISIBLE);
        }

        // TODO : Refactor
        if (size > 1) {
            int myPercent = usagePercentagePair.get(1).second;
            myGenreItem2.setTitleText(usagePercentagePair.get(1).first.getName());
            myGenreItem2.setDescriptionText(String.format(getString(R.string.analysis_my_genre_used_time_format), myPercent));
            myGenreItem2.setVisibility(View.VISIBLE);
        }

        // TODO : Refactor
        if (size > 2) {
            int worstPercent = usagePercentagePair.get(2).second;
            myGenreItem3.setTitleText(usagePercentagePair.get(2).first.getName());
            myGenreItem3.setDescriptionText(String.format(getString(R.string.analysis_my_genre_used_time_format), worstPercent));
            myGenreItem3.setVisibility(View.VISIBLE);
        }

        bindChart(myGenrePieChart, percentages);
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

    private String getMyRankText(Rank myRank, Rank worstRank) {
        if (!worstRank.getContent().equals(myRank.getContent())) {
            return myRank.isValid() ?
                    getString(R.string.analysis_playtime_rank_number, myRank.getRank())
                    : getString(R.string.analysis_playtime_rank_number_string, "?");
        } else {
            return getString(R.string.analysis_playtime_rank_number_last);
        }
    }

    @Override
    public void bindRankingViews(List<Rank> totalUsedTimeRank) {
        Log.d(TAG, String.valueOf(totalUsedTimeRank));

        Rank bestRank = totalUsedTimeRank.get(0);
        Rank myRank = totalUsedTimeRank.get(1);
        Rank worstRank = totalUsedTimeRank.get(totalUsedTimeRank.size() - 1);

        if (totalUsedTimeRank.size() < 3) {
            myRank = new Rank("", -1, 0L);
        }

        String myRankText = getMyRankText(myRank, worstRank);

        float bestHour = presenter.getHour(bestRank.getContent());
        float worstHour = presenter.getHour(worstRank.getContent());
        float myHour = presenter.getHour(myRank.getContent());

        Resources res = getResources();

        // 나의 플레이 등수 관련 세팅
        myPlaytimeRankMedalTextView.setText(myRankText);
        myPlaytimeRankSummaryTextView.setText(String.format(getString(R.string.analysis_my_playtime_rank_summary), myHour));

        if (myRank.isValid()) {
            myPlaytimeRankDescriptionTitleTextView.setText(String.format(res.getString(R.string.analysis_my_playtime_rank_description_title), myRankText));
        } else {
            myPlaytimeRankDescriptionTitleTextView.setText(res.getString(R.string.analysis_my_playtime_rank_no_data_title));
            myPlaytimeRankDescriptionContentTextView.setText(res.getString(R.string.analysis_my_playtime_rank_no_data_content));
        }

        Map<Float, String> labelMap = new HashMap<>();

        labelMap.put(3f, res.getString(R.string.analysis_playtime_rank_number, bestRank.getRank()));
        labelMap.put(1f, res.getString(R.string.analysis_playtime_rank_number_last));
        labelMap.put(2f, res.getString(R.string.analysis_playtime_my_rank_number, myRankText));

        List<BarEntry> barEntries = new ArrayList<>();
        barEntries.add(new BarEntry(3f, bestHour));
        barEntries.add(new BarEntry(2f, myHour));
        barEntries.add(new BarEntry(1f, worstHour));
        Log.d(TAG, String.valueOf(barEntries));

        BarDataSet barDataSet = new BarDataSet(barEntries, "");

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
        ((TextView) myFavoriteDeveloper.findViewById(R.id.developer_name))
                .setText(!myDeveloperUsages.isEmpty() ? myDeveloperUsages.get(0).getName() : getString(R.string.analysis_error_not_enough_data));
        ((TextView) myFavoriteDeveloper.findViewById(R.id.developer_description))
                .setText(!myDeveloperUsages.isEmpty() ? myDeveloperUsages.get(0).getAppInfos().get(0).getAppName() : getString(R.string.common_dash));


        ((TextView) genderAgeFavoriteDeveloper.findViewById(R.id.group))
                .setText(getString(R.string.analysis_favorite_developer_group,
                        getString(R.string.common_age, userInfo.getAge()) + " " +
                                getString(R.string.common_string, getString(userInfo.getGenderToStringResId()))));
        ((TextView) genderAgeFavoriteDeveloper.findViewById(R.id.developer_name)).setText(genderAgeDeveloperUsages.get(0).getName());
        ((TextView) genderAgeFavoriteDeveloper.findViewById(R.id.developer_description))
                .setText(genderAgeDeveloperUsages.get(0).getAppInfos().get(0).getAppName());


        ((TextView) jobFavoriteDeveloper.findViewById(R.id.group))
                .setText(getString(R.string.analysis_favorite_developer_group, User.JobCategory.get(userInfo.getJob()).getName()));
        ((TextView) jobFavoriteDeveloper.findViewById(R.id.developer_name)).setText(jobDeveloperUsages.get(0).getName());
        ((TextView) jobFavoriteDeveloper.findViewById(R.id.developer_description))
                .setText(jobDeveloperUsages.get(0).getAppInfos().get(0).getAppName());
    }

    @Override
    public void bindMyGames(List<Usage> myAppUsages) {
        int size = myAppUsages.size();

        if (size <= 0) {
            myGamesItem1.setTitleText(R.string.analysis_cannot_know_yet);
            myGamesItem1.setDescriptionText(getString(R.string.analysis_my_games_description, 0f));
            presenter.getImageLoader().load(R.drawable.fomes_crying_app_icon).into(myGamesItem1.getIconImageView());
            myGamesItem1.setVisibility(View.VISIBLE);
        }

        if (size > 0) {
            myGamesItem1.setTitleText(myAppUsages.get(0).getName());
            myGamesItem1.setDescriptionText(getString(R.string.analysis_my_games_description, presenter.getHour(myAppUsages.get(0).getTotalUsedTime())));
            presenter.getImageLoader().load(myAppUsages.get(0).getAppInfos().get(0).getIconUrl()).into(myGamesItem1.getIconImageView());
            myGamesItem1.setVisibility(View.VISIBLE);
        }

        if (size > 1) {
            myGamesItem2.setTitleText(myAppUsages.get(1).getName());
            myGamesItem2.setDescriptionText(getString(R.string.analysis_my_games_description, presenter.getHour(myAppUsages.get(1).getTotalUsedTime())));
            presenter.getImageLoader().load(myAppUsages.get(1).getAppInfos().get(0).getIconUrl()).into(myGamesItem2.getIconImageView());
            myGamesItem2.setVisibility(View.VISIBLE);
        }

        if (size > 2) {
            myGamesItem3.setTitleText(myAppUsages.get(2).getName());
            myGamesItem3.setDescriptionText(getString(R.string.analysis_my_games_description, presenter.getHour(myAppUsages.get(2).getTotalUsedTime())));
            presenter.getImageLoader().load(myAppUsages.get(2).getAppInfos().get(0).getIconUrl()).into(myGamesItem3.getIconImageView());
            myGamesItem3.setVisibility(View.VISIBLE);
        }

        if (size <= 0) {
            myGamesSuggestionTextView.setText(R.string.analysis_game_suggestion_without_1_2_3);
            myGamesSuggestionTextView.setVisibility(View.VISIBLE);
        } else if (size == 1) {
            myGamesSuggestionTextView.setText(R.string.analysis_game_suggestion_without_2_3);
            myGamesSuggestionTextView.setVisibility(View.VISIBLE);
        } else if (size == 2) {
            myGamesSuggestionTextView.setText(R.string.analysis_game_suggestion_without_3);
            myGamesSuggestionTextView.setVisibility(View.VISIBLE);
        } else {
            myGamesSuggestionTextView.setVisibility(View.GONE);
        }
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

    // TODO : 프래그먼트 분리후 제거
    @OnClick(R.id.analysis_error_botton_button)
    public void onErrorBottomButtonClick() {
        onConfirmButtonClick();
    }
}
