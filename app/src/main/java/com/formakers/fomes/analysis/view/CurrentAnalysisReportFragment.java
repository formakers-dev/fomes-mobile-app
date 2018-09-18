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
import com.formakers.fomes.analysis.contract.CurrentAnalysisReportContract;
import com.formakers.fomes.analysis.presenter.CurrentAnalysisReportPresenter;
import com.formakers.fomes.dagger.ApplicationComponent;
import com.formakers.fomes.fragment.BaseFragment;
import com.formakers.fomes.model.CategoryUsage;

import java.util.List;

import butterknife.BindView;
import rx.android.schedulers.AndroidSchedulers;

public class CurrentAnalysisReportFragment extends BaseFragment implements CurrentAnalysisReportContract.View {
    public static final String TAG = CurrentAnalysisReportFragment.class.getSimpleName();

    @BindView(R.id.current_analysis_loading_layout) ViewGroup loadingLayout;
    @BindView(R.id.current_analysis_layout) ViewGroup contentLayout;
    @BindView(R.id.current_analysis_error_layout) ViewGroup errorLayout;
    @BindView(R.id.current_analysis_my_genre_1) ViewGroup myGenreItem1;
    @BindView(R.id.current_analysis_my_genre_2) ViewGroup myGenreItem2;
    @BindView(R.id.current_analysis_my_genre_3) ViewGroup myGenreItem3;

    CurrentAnalysisReportContract.Presenter presenter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setPresenter(new CurrentAnalysisReportPresenter(this));

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
    public void setPresenter(CurrentAnalysisReportContract.Presenter presenter) {
        this.presenter = presenter;
    }


    @Override
    public ApplicationComponent getApplicationComponent() {
        return ((AppBeeApplication) this.getActivity().getApplication()).getComponent();
    }

    @Override
    public void bindMyGenreViews(List<CategoryUsage> genres) {
        List<Pair<CategoryUsage, Integer>> usagePercentagePair = this.presenter.getPercentage(genres, 0, 3);

        ((TextView) myGenreItem1.findViewById(R.id.name_textview)).setText(usagePercentagePair.get(0).first.getCategoryName());
        ((TextView) myGenreItem1.findViewById(R.id.used_time_textview))
                .setText(String.format(getString(R.string.analysis_my_genre_used_time_format), usagePercentagePair.get(0).second));
        ((TextView) myGenreItem2.findViewById(R.id.name_textview)).setText(usagePercentagePair.get(1).first.getCategoryName());
        ((TextView) myGenreItem2.findViewById(R.id.used_time_textview))
                .setText(String.format(getString(R.string.analysis_my_genre_used_time_format), usagePercentagePair.get(1).second));
        ((TextView) myGenreItem3.findViewById(R.id.name_textview)).setText(usagePercentagePair.get(2).first.getCategoryName());
        ((TextView) myGenreItem3.findViewById(R.id.used_time_textview))
                .setText(String.format(getString(R.string.analysis_my_genre_used_time_format), usagePercentagePair.get(2).second));
    }
}
