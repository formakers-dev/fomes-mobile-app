package com.formakers.fomes.provisioning.view;

import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.formakers.fomes.R;
import com.formakers.fomes.common.view.BaseFragment;
import com.formakers.fomes.provisioning.contract.ProvisioningContract;

import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.OnTextChanged;
import rx.android.schedulers.AndroidSchedulers;

public class ProvisioningNickNameFragment extends BaseFragment implements ProvisioningActivity.FragmentCommunicator {

    public static final String TAG = ProvisioningNickNameFragment.class.getSimpleName();
    private static final String NICKNAME_REGEX = "[a-zA-Zㄱ-ㅎ가-힣0-9]{2,10}";

    @BindView(R.id.provision_nickname_content_edittext)
    EditText nickNameEditText;
    @BindView(R.id.provision_nickname_format_warning_textview)
    TextView nickNameWarningTextView;

    ProvisioningContract.Presenter presenter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_provision_nickname, container, false);
    }

    public ProvisioningNickNameFragment setPresenter(ProvisioningContract.Presenter presenter) {
        this.presenter = presenter;
        return this;
    }

    @Override
    public void onSelectedPage() {
        Log.v(TAG, "onSelectedPage");
        if (getView() != null && this.isVisible()) {
            CharSequence nickName = nickNameEditText.getText();
            onNickNameTextChanged(nickName, 0, 0, nickName.length());
        }
    }

    @Override
    public void onNextButtonClick() {
        this.presenter.updateNickNameToUser(nickNameEditText.getText().toString());
        this.presenter.requestUpdateUser()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> {
                            this.presenter.emitNextPageEvent();
                        },
                        e -> Toast.makeText(this.getContext(), "유저 정보 업데이트를 실패하였습니다.", Toast.LENGTH_LONG).show());
    }

    @OnTextChanged(value = R.id.provision_nickname_content_edittext, callback = OnTextChanged.Callback.TEXT_CHANGED)
    public void onNickNameTextChanged(CharSequence text, int start, int before, int count) {
        Log.v(TAG, text + " start=" + start + ", before=" + before + ", count=" + count);

        boolean isMatched = Pattern.matches(NICKNAME_REGEX, text);

        this.presenter.emitFilledUpEvent(this, isMatched);
        nickNameWarningTextView.setVisibility(isMatched ? View.GONE : View.VISIBLE);

        if (isMatched) {
            nickNameEditText.getBackground().setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_ATOP);
        } else {
            nickNameEditText.getBackground().setColorFilter(getResources().getColor(R.color.fomes_red), PorterDuff.Mode.SRC_ATOP);
        }
    }
}
