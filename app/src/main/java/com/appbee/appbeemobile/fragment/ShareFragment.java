package com.appbee.appbeemobile.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.appbee.appbeemobile.AppBeeApplication;
import com.appbee.appbeemobile.R;
import com.appbee.appbeemobile.helper.ShareSnsHelper;
import com.appbee.appbeemobile.util.AppBeeConstants;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class ShareFragment extends Fragment {

    public static final String EXTRA_CHARACTER_TYPE = "CHARACTER_TYPE";

    @BindView(R.id.share_button)
    Button shareButton;

    @Inject
    ShareSnsHelper shareSnsHelper;

    private Unbinder binder;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        ((AppBeeApplication) getActivity().getApplication()).getComponent().inject(this);

        View view = inflater.inflate(R.layout.fragment_share, container, false);
        binder = ButterKnife.bind(this, view);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        shareButton.setOnClickListener(v -> shareSnsHelper.shareKakao((AppBeeConstants.CharacterType) getArguments().getSerializable(EXTRA_CHARACTER_TYPE)));
    }

    @Override
    public void onDestroyView() {
        binder.unbind();
        super.onDestroyView();
    }
}