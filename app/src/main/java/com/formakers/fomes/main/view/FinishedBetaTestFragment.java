package com.formakers.fomes.main.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.formakers.fomes.R;
import com.formakers.fomes.common.view.BaseFragment;

public class FinishedBetaTestFragment extends BaseFragment implements MainActivity.FragmentCommunicator {
    public static final String TAG = "FinishedBetaTestFragment";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_finished_betatest, container, false);
    }

    @Override
    public void onSelectedPage() {

    }
}
