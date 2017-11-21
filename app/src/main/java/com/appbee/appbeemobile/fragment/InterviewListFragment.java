package com.appbee.appbeemobile.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.appbee.appbeemobile.R;

public class InterviewListFragment extends BaseFragment {
    public static final @StringRes int TITLE_RES_ID = R.string.contents_title_interview;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_interview_list, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // View 작업은 여기서 부터 시작
    }
}
