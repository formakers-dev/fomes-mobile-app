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
    private Unbinder binder;
    public static String EXTRA_MOST_USED_TIME_CATEGORIES = "EXTRA_MOST_USED_TIME_CATEGORIES";

    @BindView(R.id.most_used_time_categories)
    LinearLayout categoryLinearLayout;

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
        for (String mostUsedTimeCategory : mostUsedTimeCategoryList) {
            TextView textView = new TextView(getActivity());
            textView.setText(mostUsedTimeCategory);
            categoryLinearLayout.addView(textView);
        }
    }

    @Override
    public void onDestroyView() {
        binder.unbind();
        super.onDestroyView();
    }
}
