package com.appbee.appbeemobile.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.appbee.appbeemobile.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class FlowerFragment extends Fragment{
    public static final String EXTRA_MOST_USED_TIME_CATEGORIES = "EXTRA_MOST_USED_TIME_CATEGORIES";
    public static final String EXTRA_LEAST_USED_TIME_CATEGORIES = "EXTRA_LEAST_USED_TIME_CATEGORIES";
    public static final String EXTRA_MOST_USED_TIME_CATEGORY_RATE = "EXTRA_MOST_USED_TIME_CATEGORY_RATE";
    public static final String EXTRA_MOST_USED_TIME_CATEGORY_DESC = "EXTRA_MOST_USED_TIME_CATEGORY_DESC";

    @BindView(R.id.most_used_time_categories)
    LinearLayout categoryLinearLayout;

    @BindView(R.id.least_used_time_category)
    TextView leastUsedTimeCategoryTextView;

    @BindView(R.id.most_used_time_category_rate)
    TextView mostUsedTimeCategoryRate;

    @BindView(R.id.most_used_time_category_desc)
    TextView mostUsedTimeCategoryDesc;

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
        if (mostUsedTimeCategoryList != null) {
            for (String mostUsedTimeCategory : mostUsedTimeCategoryList) {
                TextView textView = new TextView(getActivity());
                textView.setText(mostUsedTimeCategory);
                categoryLinearLayout.addView(textView);
            }
            mostUsedTimeCategoryRate.setText(String.format(getString(R.string.category_rate), mostUsedTimeCategoryList.get(0), getArguments().getLong(EXTRA_MOST_USED_TIME_CATEGORY_RATE)));
        }
        List<String> leastUsedTimeCategoryList = getArguments().getStringArrayList(EXTRA_LEAST_USED_TIME_CATEGORIES);
        if (leastUsedTimeCategoryList != null) {
            for (String leastUsedTimeCategory : leastUsedTimeCategoryList) {
                TextView textView = new TextView(getActivity());
                textView.setText(leastUsedTimeCategory);
                leastUsedTimeCategoryTextView.setText(leastUsedTimeCategory);
            }
        }

        mostUsedTimeCategoryDesc.setText(getArguments().getString(EXTRA_MOST_USED_TIME_CATEGORY_DESC));
    }

    @Override
    public void onDestroyView() {
        binder.unbind();
        super.onDestroyView();
    }
}
