package com.formakers.fomes.main.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.formakers.fomes.R;
import com.formakers.fomes.common.constant.Feature;
import com.formakers.fomes.common.util.Log;
import com.formakers.fomes.common.view.BaseFragment;
import com.formakers.fomes.main.contract.BetaTestContract;
import com.formakers.fomes.main.contract.BetaTestListAdapterContract;

import butterknife.BindView;

public class BetaTestFragment extends BaseFragment implements BetaTestContract.View {

    public static final String TAG = "BetaTestFragment";

    @BindView(R.id.feedback_recyclerview) RecyclerView recyclerView;

    BetaTestContract.Presenter presenter;
    BetaTestListAdapterContract.View betaTestListAdapterView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");

        View view;

        if (Feature.BETATEST_ZONE) {
            view = inflater.inflate(R.layout.fragment_betatest, container, false);
        } else {
            view = inflater.inflate(R.layout.fragment_betatest_old, container, false);
        }

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void setPresenter(BetaTestContract.Presenter presenter) {
        this.presenter = presenter;
    }
}
