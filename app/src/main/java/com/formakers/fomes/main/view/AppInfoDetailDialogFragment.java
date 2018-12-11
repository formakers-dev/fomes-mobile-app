package com.formakers.fomes.main.view;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.formakers.fomes.FomesApplication;
import com.formakers.fomes.R;
import com.formakers.fomes.common.FomesConstants;
import com.formakers.fomes.common.util.Log;
import com.formakers.fomes.common.view.custom.RecommendAppItemView;
import com.formakers.fomes.main.contract.AppInfoDetailContract;
import com.formakers.fomes.main.dagger.AppInfoDetailFragmentModule;
import com.formakers.fomes.main.dagger.DaggerAppInfoDetailFragmentComponent;
import com.formakers.fomes.model.AppInfo;
import com.google.common.base.Joiner;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import rx.Completable;
import rx.android.schedulers.AndroidSchedulers;

public class AppInfoDetailDialogFragment extends BottomSheetDialogFragment implements AppInfoDetailContract.View {

    public static final String TAG = AppInfoDetailDialogFragment.class.getSimpleName();

    @BindView(R.id.app_detail_view) RecommendAppItemView appDetailView;
    @BindView(R.id.download_button) Button downloadButton;

    Unbinder unbinder;

    @Inject AppInfoDetailContract.Presenter presenter;
    private Communicator communicator;

    public AppInfoDetailDialogFragment() {
    }

    public void setCommunicator(Communicator communicator) {
        this.communicator = communicator;
    }

    @Override
    public void setPresenter(AppInfoDetailContract.Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(BottomSheetDialogFragment.STYLE_NORMAL, R.style.RecommendTheme_BottomSheetDialog);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        DaggerAppInfoDetailFragmentComponent.builder()
                .applicationComponent(FomesApplication.get(this.getActivity()).getComponent())
                .appInfoDetailFragmentModule(new AppInfoDetailFragmentModule(this))
                .build()
                .inject(this);

        View view = inflater.inflate(R.layout.fragment_dialog_appinfo_detail, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle bundle = getArguments();

        if (bundle == null) {
            return;
        }

        AppInfo appInfo = bundle.getParcelable(FomesConstants.EXTRA.APPINFO);
        int recommendType = bundle.getInt(FomesConstants.EXTRA.RECOMMEND_TYPE);
        List<String> recommendCriteria = bundle.getStringArrayList(FomesConstants.EXTRA.RECOMMEND_CRITERIA);

        String packageName = appInfo.getPackageName();

        Log.v(TAG, String.valueOf(appInfo));

        appDetailView.bindAppInfo(appInfo);
        appDetailView.setRecommendType(recommendType);
        appDetailView.setLabelText(Joiner.on(" ").join(recommendCriteria.toArray()));
        appDetailView.setOnWishListCheckedChangeListener((v, isChecked) -> {
            Completable requestUpdateWishList = isChecked ? this.presenter.requestSaveToWishList(packageName) : this.presenter.requestRemoveFromWishList(packageName);

            requestUpdateWishList .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(() -> this.communicator.emitUpdateWishedStatusEvent(packageName, isChecked)
                            , e -> Toast.makeText(this.getActivity(), "위시리스트 " + (isChecked ? "등록" : "삭제") + "에 실패하였습니다.", Toast.LENGTH_LONG).show());
        });

        downloadButton.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("market://details?id=" + appInfo.getPackageName()));
            startActivity(intent);
        });
    }

    @Override
    public void onDestroyView() {
        if (unbinder != null) {
            unbinder.unbind();
        }

        super.onDestroyView();
    }

    // TODO : 네이밍 고민
    interface Communicator {
        void emitUpdateWishedStatusEvent(String packageName, boolean isWished);
    }
}
