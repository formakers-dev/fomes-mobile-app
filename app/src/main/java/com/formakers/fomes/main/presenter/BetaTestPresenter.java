package com.formakers.fomes.main.presenter;

import com.formakers.fomes.common.network.RequestService;
import com.formakers.fomes.common.util.Log;
import com.formakers.fomes.main.contract.BetaTestContract;
import com.formakers.fomes.main.contract.BetaTestListAdapterContract;
import com.formakers.fomes.main.dagger.scope.BetaTestFragmentScope;

import javax.inject.Inject;

import rx.android.schedulers.AndroidSchedulers;

@BetaTestFragmentScope
public class BetaTestPresenter implements BetaTestContract.Presenter {

    public static String TAG = "BetaTestPresenter";

    private BetaTestListAdapterContract.Model betaTestListAdapterModel;

    private BetaTestContract.View view;
    private RequestService requestService;

    @Inject
    public BetaTestPresenter(BetaTestContract.View view, RequestService requestService) {
        this.view = view;
        this.requestService = requestService;
    }

    @Override
    public void setAdapterModel(BetaTestListAdapterContract.Model adapterModel) {
        this.betaTestListAdapterModel = adapterModel;
    }

    @Override
    public void load() {
        requestService.getFeedbackRequest()
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(() -> view.showLoading())
                .doAfterTerminate(() -> view.hideLoading())
                .subscribe(requests -> {
                    betaTestListAdapterModel.addAll(requests);
                    view.refreshBetaTestList();
                }, e -> Log.e(TAG, String.valueOf(e)));
    }
}
