package com.formakers.fomes.provisioning;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;

import com.formakers.fomes.R;
import com.formakers.fomes.common.constant.FomesConstants;
import com.formakers.fomes.common.network.api.UserAPI;
import com.formakers.fomes.common.util.Log;
import com.formakers.fomes.common.view.BaseFragment;

import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.OnTextChanged;
import retrofit2.adapter.rxjava.HttpException;
import rx.android.schedulers.AndroidSchedulers;

public class ProvisioningNickNameFragment extends BaseFragment implements ProvisioningActivity.FragmentCommunicator {

    public static final String TAG = ProvisioningNickNameFragment.class.getSimpleName();

    @BindView(R.id.provision_nickname_content_edittext) EditText nickNameEditText;
    @BindView(R.id.provision_nickname_format_warning_textview) TextView nickNameWarningTextView;

    ProvisioningContract.Presenter presenter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (this.presenter != null && this.presenter.isSelected(this)) {
            onSelectedPage();
        }

        return inflater.inflate(R.layout.fragment_provision_nickname, container, false);
    }

    public ProvisioningNickNameFragment setPresenter(ProvisioningContract.Presenter presenter) {
        this.presenter = presenter;
        return this;
    }

    @Override
    public void onSelectedPage() {
        Log.v(TAG, "onSelectedPage");

        if (this.presenter == null) {
            return;
        }

        this.presenter.getAnalytics().setCurrentScreen(this);

        if (getView() != null && this.isVisible()) {
            CharSequence nickName = nickNameEditText.getText();
            onNickNameTextChanged(nickName, 0, 0, nickName.length());
        }
    }

    @Override
    public void onNextButtonClick() {
        String nickName = nickNameEditText.getText().toString();

        if (this.presenter == null) {
            return;
        }

        addCompositeSubscription(
            this.presenter.requestVerifyUserNickName(nickName)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> {
                        this.presenter.updateNickNameToUser(nickName);
                        this.presenter.emitNextPageEvent();
                    }, e -> {
                    if (e instanceof HttpException) {
                        if (((HttpException) e).code() == UserAPI.StatusCode.DUPLICATED_NICK_NAME) {
                            setVisibilityWarningView(true, R.string.provision_nickname_already_exist_warning);
                        }
                    } else {
                        Toast.makeText(this.getContext(), "오류가 발생하였습니다. 재시도 부탁드립니다.", Toast.LENGTH_LONG).show();
                    }
                })
        );
    }

    @OnTextChanged(value = R.id.provision_nickname_content_edittext, callback = OnTextChanged.Callback.TEXT_CHANGED)
    public void onNickNameTextChanged(CharSequence text, int start, int before, int count) {
        Log.v(TAG, text + " start=" + start + ", before=" + before + ", count=" + count);

        boolean isWrong = Pattern.matches(FomesConstants.PROVISIONING.NICK_NAME_REGEX.WRONG, text);

        if (isWrong) {
            setVisibilityWarningView(true, R.string.provision_nickname_format_special_string_warning);
        } else {
            boolean isMatched = Pattern.matches(FomesConstants.PROVISIONING.NICK_NAME_REGEX.CORRECT, text);
            setVisibilityWarningView(!isMatched, R.string.provision_nickname_format_warning);
        }
    }

    private void setVisibilityWarningView(boolean isVisible, @StringRes int stringResId) {
        if (isVisible) {
            nickNameWarningTextView.setText(getString(stringResId));
            nickNameWarningTextView.setVisibility(View.VISIBLE);
        } else {
            nickNameWarningTextView.setVisibility(View.GONE);
        }

        if (this.presenter == null) {
            return;
        }

        this.presenter.emitFilledUpEvent(this, !isVisible);
    }
}
