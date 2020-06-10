package com.formakers.fomes.more;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;

import com.formakers.fomes.FomesApplication;
import com.formakers.fomes.R;
import com.formakers.fomes.common.view.BaseFragment;

import javax.inject.Inject;

public class MenuListFragment extends BaseFragment implements MenuListContract.View {

    @Inject MenuListContract.Presenter presenter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        DaggerMenuListDagger_Component.builder()
                .applicationComponent(FomesApplication.get(this.getActivity()).getComponent())
                .module(new MenuListDagger.Module(this))
                .build()
                .inject(this);

        return inflater.inflate(R.layout.activity_more, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    @Override
    public void setPresenter(MenuListContract.Presenter presenter) {
        this.presenter = presenter;
    }
}
