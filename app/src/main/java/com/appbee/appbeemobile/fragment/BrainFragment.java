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

public class BrainFragment extends Fragment {

    public static final String EXTRA_MOST_USED_CATEGORIES = "EXTRA_MOST_USED_CATEGORIES";


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        return inflater.inflate(R.layout.fragment_brain, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ArrayList<String> mostInstalledCategories = getArguments().getStringArrayList(EXTRA_MOST_USED_CATEGORIES);
        ((TextView) view.findViewById(R.id.most_installed_categories)).setText(mostInstalledCategories.toString());
    }
}
