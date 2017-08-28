package com.appbee.appbeemobile.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.appbee.appbeemobile.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class FlowerFragment extends Fragment {
    public static final String EXTRA_MOST_USED_TIME_CATEGORIES = "EXTRA_MOST_USED_TIME_CATEGORIES";
    public static final String EXTRA_LEAST_USED_TIME_CATEGORIES = "EXTRA_LEAST_USED_TIME_CATEGORIES";
    public static final String EXTRA_MOST_USED_TIME_CATEGORY_DESC = "EXTRA_MOST_USED_TIME_CATEGORY_DESC";
    public static final String EXTRA_MOST_USED_TIME_CATEGORY_SUMMARY = "EXTRA_MOST_USED_TIME_CATEGORY_SUMMARY";

    @BindView(R.id.most_used_category1)
    TextView mostUsedCategory1View;

    @BindView(R.id.most_used_category2)
    TextView mostUsedCategory2View;

    @BindView(R.id.most_used_category3)
    TextView mostUsedCategory3View;

    @BindView(R.id.least_used_category)
    TextView leastUsedCategoryView;

    @BindView(R.id.most_used_time_category_summary)
    TextView mostUsedTimeCategorySummaryView;

    @BindView(R.id.most_used_time_category_description)
    TextView mostUsedTimeCategoryDescriptionView;

    private Unbinder binder;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_flower, container, false);
        binder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        List<String> mostUsedTimeCategoryList = getArguments().getStringArrayList(EXTRA_MOST_USED_TIME_CATEGORIES);
        if (isAvailableMostUsedCategoryList(mostUsedTimeCategoryList)) {
            mostUsedCategory1View.setText(mostUsedTimeCategoryList.get(0));
            mostUsedCategory2View.setText(mostUsedTimeCategoryList.get(1));
            mostUsedCategory3View.setText(mostUsedTimeCategoryList.get(2));
        }

        List<String> leastUsedTimeCategoryList = getArguments().getStringArrayList(EXTRA_LEAST_USED_TIME_CATEGORIES);
        if (isAvailableLeastUsedCategoryList(leastUsedTimeCategoryList)) {
            leastUsedCategoryView.setText(String.format(getString(R.string.least_used_category_format), leastUsedTimeCategoryList.get(0)));
        }

        mostUsedTimeCategorySummaryView.setText(getArguments().getString(EXTRA_MOST_USED_TIME_CATEGORY_SUMMARY));
        mostUsedTimeCategoryDescriptionView.setText(getArguments().getString(EXTRA_MOST_USED_TIME_CATEGORY_DESC));
    }

    boolean isAvailableLeastUsedCategoryList(List<String> leastUsedTimeCategoryList) {
        return leastUsedTimeCategoryList != null && !leastUsedTimeCategoryList.isEmpty() && leastUsedTimeCategoryList.size() >= 1;
    }

    boolean isAvailableMostUsedCategoryList(List<String> mostUsedTimeCategoryList) {
        return mostUsedTimeCategoryList != null && !mostUsedTimeCategoryList.isEmpty() && mostUsedTimeCategoryList.size() == 3;
    }

    @Override
    public void onDestroyView() {
        binder.unbind();
        super.onDestroyView();
    }
}
