package com.appbee.appbeemobile.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.appbee.appbeemobile.R;

public class OverviewFragment extends Fragment {

    public static final String EXTRA_APP_LIST_COUNT = "EXTRA_APP_LIST_COUNT";
    public static final String EXTRA_APP_LIST_COUNT_MSG = "EXTRA_APP_LIST_COUNT_MSG";
    public static final String EXTRA_APP_AVG_TIME = "EXTRA_APP_AVG_TIME";
    public static final String EXTRA_APP_USAGE_AVG_TIME_MSG = "EXTRA_APP_USAGE_AVG_TIME_MSG";
    public static final String EXTRA_LONGEST_USED_APP_PACKAGE_NAME = "EXTRA_LONGEST_USED_APP_PACKAGE_NAME";
    public static final String EXTRA_LONGEST_USED_APP_TIME = "EXTRA_LONGEST_USED_APP_TIME";

    View view;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_overview, container, false);

        ((TextView) view.findViewById(R.id.app_count_textview)).setText(String.valueOf(getArguments().getInt(EXTRA_APP_LIST_COUNT)));
        ((TextView) view.findViewById(R.id.app_count_msg_textview)).setText(getArguments().getString(EXTRA_APP_LIST_COUNT_MSG));

        ((TextView) view.findViewById(R.id.average_app_usage_time_textview))
                .setText(String.format(getString(R.string.overview_average_time), getArguments().getInt(EXTRA_APP_AVG_TIME)));
        ((TextView) view.findViewById(R.id.average_app_usage_time_msg_textview)).setText(getArguments().getString(EXTRA_APP_USAGE_AVG_TIME_MSG));

        ((TextView) view.findViewById(R.id.longest_used_app_packagename_textview)).setText(getArguments().getString(EXTRA_LONGEST_USED_APP_PACKAGE_NAME));
        ((TextView) view.findViewById(R.id.longest_used_app_time_textview)).setText(String.valueOf(getArguments().getLong(EXTRA_LONGEST_USED_APP_TIME)));

        return view;
    }
}
