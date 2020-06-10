package com.formakers.fomes.more;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.formakers.fomes.FomesApplication;
import com.formakers.fomes.R;
import com.formakers.fomes.common.view.BaseFragment;

import javax.inject.Inject;

import butterknife.BindView;

public class MenuListFragment extends BaseFragment implements MenuListContract.View {

    @BindView(R.id.more_email) TextView emailTextView;
    @BindView(R.id.more_nickname) TextView nickNameTextView;
    @BindView(R.id.more_participation_count) TextView participationCountTextView;

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

        this.presenter.bindUserInfo();
    }

    @Override
    public void setPresenter(MenuListContract.Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void setUserInfo(String email, String nickName) {
        emailTextView.setText(email);
        nickNameTextView.setText(nickName);
    }
}
