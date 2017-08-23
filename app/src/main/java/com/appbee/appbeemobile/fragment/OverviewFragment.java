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

public class OverviewFragment extends Fragment {

    public static final String EXTRA_APP_LIST_COUNT = "EXTRA_APP_LIST_COUNT";
    public static final String EXTRA_APP_LIST_COUNT_MSG = "EXTRA_APP_LIST_COUNT_MSG";
    public static final String EXTRA_APP_AVG_TIME = "EXTRA_APP_AVG_TIME";
    public static final String EXTRA_APP_USAGE_AVG_TIME_MSG = "EXTRA_APP_USAGE_AVG_TIME_MSG";
    public static final String EXTRA_LONGEST_USED_APP_NAME_LIST = "EXTRA_LONGEST_USED_APP_NAME_LIST";

    @BindView(R.id.app_count_textview)
    TextView appCountView;

    @BindView(R.id.app_count_msg_textview)
    TextView appCountMsgView;

    @BindView(R.id.average_app_usage_time_textview)
    TextView averageAppUsageTimeView;

    @BindView(R.id.average_app_usage_time_msg_textview)
    TextView averageAppUsageTimeMsgView;

    @BindView(R.id.longest_used_app_layout)
    LinearLayout longestUsedAppLayout;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_overview, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        appCountView.setText(String.valueOf(getArguments().getInt(EXTRA_APP_LIST_COUNT)));
        appCountMsgView.setText(getArguments().getString(EXTRA_APP_LIST_COUNT_MSG));

        averageAppUsageTimeView.setText(String.format(getString(R.string.overview_average_time), getArguments().getInt(EXTRA_APP_AVG_TIME)));
        averageAppUsageTimeMsgView.setText(getArguments().getString(EXTRA_APP_USAGE_AVG_TIME_MSG));

        List<String> appNames = getArguments().getStringArrayList(EXTRA_LONGEST_USED_APP_NAME_LIST);

        if(appNames != null) {
            for(String appName: appNames) {
                TextView textView = new TextView(getActivity().getApplicationContext());
                textView.setText(appName);
                longestUsedAppLayout.addView(textView);
            }
        }
    }
}
