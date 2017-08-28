package com.appbee.appbeemobile.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.appbee.appbeemobile.R;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class BrainFragment extends Fragment {

    public static final String EXTRA_MOST_INSTALLED_CATEGORIES = "EXTRA_MOST_INSTALLED_CATEGORIES";
    public static final String EXTRA_LEAST_INSTALLED_CATEGORIES = "EXTRA_LEAST_INSTALLED_CATEGORIES";
    public static final String EXTRA_MOST_INSTALLED_CATEGORY_DESCRIPTION = "EXTRA_MOST_INSTALLED_CATEGORY_DESCRIPTION";
    public static final String EXTRA_MOST_INSTALLED_CATEGORY_SUMMARY = "EXTRA_MOST_INSTALLED_CATEGORY_SUMMARY";

    @BindView(R.id.most_installed_category_summary)
    TextView mostInstalledCategorySummaryView;

    @BindView(R.id.most_installed_category_description)
    TextView mostInstalledCategoryDescriptionView;

    @BindView(R.id.most_installed_category1)
    TextView mostInstalledCategory1View;

    @BindView(R.id.most_installed_category2)
    TextView mostInstalledCategory2View;

    @BindView(R.id.most_installed_category3)
    TextView mostInstalledCategory3View;

    @BindView(R.id.least_installed_category)
    TextView leastInstalledCategoryView;

    private Unbinder binder;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.fragment_brain, container, false);
        binder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        // TODO : 카테고리 정보가 없는 경우 예외 케이스 처리
        super.onViewCreated(view, savedInstanceState);

        ArrayList<String> mostInstalledCategoryList = getArguments().getStringArrayList(EXTRA_MOST_INSTALLED_CATEGORIES);
        if (mostInstalledCategoryList.size() > 0) {
            mostInstalledCategory1View.setText(mostInstalledCategoryList.get(0));
            mostInstalledCategory2View.setText(mostInstalledCategoryList.get(1));
            mostInstalledCategory3View.setText(mostInstalledCategoryList.get(2));
        }

        ArrayList<String> leastInstalledCategoryList = getArguments().getStringArrayList(EXTRA_LEAST_INSTALLED_CATEGORIES);
        if (leastInstalledCategoryList.size() > 0) {
            leastInstalledCategoryView.setText(leastInstalledCategoryList.get(0));
        }

        mostInstalledCategorySummaryView.setText(getArguments().getString(EXTRA_MOST_INSTALLED_CATEGORY_SUMMARY));
        mostInstalledCategoryDescriptionView.setText(getArguments().getString(EXTRA_MOST_INSTALLED_CATEGORY_DESCRIPTION));
    }

    @Override
    public void onDestroyView() {
        binder.unbind();
        super.onDestroyView();
    }
}
