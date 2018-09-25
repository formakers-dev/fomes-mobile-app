package com.formakers.fomes.provisioning.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.formakers.fomes.R;
import com.formakers.fomes.common.view.BaseFragment;
import com.formakers.fomes.provisioning.contract.ProvisioningContract;

import butterknife.BindView;
import butterknife.OnTextChanged;

public class ProvisioningLifeGameFragment extends BaseFragment implements ProvisioningActivity.FragmentCommunicator {

    public static final String TAG = ProvisioningLifeGameFragment.class.getSimpleName();

    @BindView(R.id.provision_life_game_content_edittext) EditText lifeGameEditText;

    ProvisioningContract.Presenter presenter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_provision_life_game, container, false);
    }

    public ProvisioningLifeGameFragment setPresenter(ProvisioningContract.Presenter presenter) {
        this.presenter = presenter;
        return this;
    }

    @Override
    public void onSelectedPage() {
        Log.v(TAG, "onSelectedPage");
        if (getView() != null && this.isVisible()) {
            CharSequence nickName = lifeGameEditText.getText();
            onLifeGameTextChanged(nickName, 0, 0, nickName.length());
        }
    }

    @Override
    public void onNextButtonClick() {
        this.presenter.updateLifeGameToUser(lifeGameEditText.getText().toString());
        this.presenter.emitNextPageEvent();
    }

    @OnTextChanged(value = R.id.provision_life_game_content_edittext, callback = OnTextChanged.Callback.TEXT_CHANGED)
    public void onLifeGameTextChanged(CharSequence text, int start, int before, int count) {
        Log.v(TAG, text + " start=" + start + ", before=" + before + ", count=" + count);
        this.presenter.emitFilledUpEvent(this, count > 0);
    }
}
