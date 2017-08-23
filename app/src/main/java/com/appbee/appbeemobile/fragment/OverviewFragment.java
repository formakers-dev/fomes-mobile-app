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

public class OverviewFragment extends Fragment {

    public static final String EXTRA_APP_LIST_COUNT = "EXTRA_APP_LIST_COUNT";
    public static final String EXTRA_APP_LIST_COUNT_TYPE = "EXTRA_APP_LIST_COUNT_TYPE";
    public static final String EXTRA_APP_AVG_TIME = "EXTRA_APP_AVG_TIME";
    public static final String EXTRA_LONGEST_USED_APP_NAME_LIST = "EXTRA_LONGEST_USED_APP_NAME_LIST";
    public static final String EXTRA_CHARACTER_TYPE = "EXTRA_CHARACTER_TYPE";
    public static final String EXTRA_APP_USAGE_TIME_TYPE = "EXTRA_APP_USAGE_TIME_TYPE";

    @BindView(R.id.app_count_textview)
    TextView appCountView;

    @BindView(R.id.app_count_msg_textview)
    TextView appCountMsgView;

    @BindView(R.id.average_app_usage_time_textview)
    TextView averageAppUsageTimeView;

    @BindView(R.id.average_app_usage_time_msg_textview)
    TextView averageAppUsageTimeMsgView;

    @BindView(R.id.average_app_usage_time_description_textview)
    TextView averageAppUsageTimeDescriptionView;

    @BindView(R.id.longest_used_app_layout)
    LinearLayout longestUsedAppLayout;

    @BindView(R.id.character_type_name)
    TextView characterTypeNameView;

    @BindView(R.id.character_type_simple_description)
    TextView characterTypeSimpleDescriptionView;

    @BindView(R.id.character_type_detail_description)
    TextView characterTypeDetailDescriptionView;

    @BindView(R.id.app_count_description_textview)
    TextView appCountDescriptionView;

    private Unbinder binder;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_overview, container, false);
        binder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        displayAppListCountInformation(getArguments().getInt(EXTRA_APP_LIST_COUNT), getArguments().getInt(EXTRA_APP_LIST_COUNT_TYPE));
        displayAppUsageTimeInformation(getArguments().getInt(EXTRA_APP_AVG_TIME), getArguments().getInt(EXTRA_APP_USAGE_TIME_TYPE));
        displayLongestUsedAppInformation(getArguments().getStringArrayList(EXTRA_LONGEST_USED_APP_NAME_LIST));
        displayCharacterTypeInformation(getArguments().getInt(EXTRA_CHARACTER_TYPE));
    }

    private void displayLongestUsedAppInformation(List<String> appNames) {
        if (appNames != null) {
            for (String appName : appNames) {
                TextView textView = new TextView(getActivity().getApplicationContext());
                textView.setText(appName);
                longestUsedAppLayout.addView(textView);
            }
        }
    }

    private void displayAppUsageTimeInformation(int appUsageAverageTime, int appUsageTimeType) {
        averageAppUsageTimeView.setText(String.format(getString(R.string.overview_average_time), appUsageAverageTime));
        averageAppUsageTimeMsgView.setText(getResources().getStringArray(R.array.app_usage_time_type_names)[appUsageTimeType]);
        averageAppUsageTimeDescriptionView.setText(getResources().getStringArray(R.array.app_usage_time_type_descriptions)[appUsageTimeType]);
    }

    private void displayAppListCountInformation(int appListCount, int appListCountType) {
        appCountView.setText(String.valueOf(appListCount));
        appCountMsgView.setText(getResources().getStringArray(R.array.app_list_count_type_names)[appListCountType]);
        appCountDescriptionView.setText(getResources().getStringArray(R.array.app_list_count_type_descriptions)[appListCountType]);
    }

    private void displayCharacterTypeInformation(int characterType) {
        characterTypeNameView.setText(getResources().getStringArray(R.array.character_names)[characterType]);
        characterTypeSimpleDescriptionView.setText(getResources().getStringArray(R.array.character_simple_descriptions)[characterType]);
        characterTypeDetailDescriptionView.setText(getResources().getStringArray(R.array.character_detail_descriptions)[characterType]);
    }

    @Override
    public void onDestroyView() {
        binder.unbind();
        super.onDestroyView();
    }
}
