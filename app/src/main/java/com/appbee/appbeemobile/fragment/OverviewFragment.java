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
        return view;
    }
}
